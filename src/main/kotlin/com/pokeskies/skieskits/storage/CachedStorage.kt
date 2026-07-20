package com.pokeskies.skieskits.storage

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.utils.Utils
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CachedStorage(val backend: IStorage) : IStorage {
    private val cache: Cache<UUID, UserData> = Caffeine.newBuilder().build()

    private val dbExecutor: ExecutorService = Executors.newSingleThreadExecutor { runnable ->
        val thread = Thread(runnable, "SkiesKits-DB-Writer")
        thread.isDaemon = true
        thread
    }

    fun loadUserAsync(uuid: UUID): CompletableFuture<UserData> {
        val cached = cache.getIfPresent(uuid)
        if (cached != null) {
            return CompletableFuture.completedFuture(cached)
        }
        val future = CompletableFuture<UserData>()
        dbExecutor.execute {
            try {
                val user = backend.getUser(uuid)
                cache.put(uuid, user)
                future.complete(user)
            } catch (e: Exception) {
                Utils.printError("Error loading user $uuid asynchronously: ${e.message}")
                e.printStackTrace()
                future.complete(UserData())
            }
        }
        return future
    }

    fun unloadUser(uuid: UUID) {
        val cached = cache.getIfPresent(uuid)
        if (cached != null) {
            if (cached.isDirty) {
                dbExecutor.execute {
                    try {
                        if (cached.isDirty) {
                            backend.saveUser(uuid, cached)
                            cached.isDirty = false
                        }
                    } catch (e: Exception) {
                        Utils.printError("Error doing final save for user $uuid on disconnect: ${e.message}")
                    }
                }
            }
            cache.invalidate(uuid)
        }
    }

    override fun getUser(uuid: UUID): UserData {
        val cached = cache.getIfPresent(uuid)
        if (cached != null) {
            return cached
        }
        val user = backend.getUser(uuid)
        cache.put(uuid, user)
        return user
    }

    override fun saveUser(uuid: UUID, userData: UserData): CompletableFuture<Boolean> {
        cache.put(uuid, userData)

        if (!userData.isDirty) {
            return CompletableFuture.completedFuture(true)
        }

        userData.isDirty = false

        val future = CompletableFuture<Boolean>()
        dbExecutor.execute {
            try {
                backend.saveUser(uuid, userData).thenAccept { success ->
                    future.complete(success)
                }.exceptionally { ex ->
                    future.completeExceptionally(ex)
                    null
                }
            } catch (e: Exception) {
                future.complete(false)
            }
        }
        return future
    }

    override fun saveAll(users: Map<UUID, UserData>): CompletableFuture<Void> {
        val dirtyUsers = users.filter { it.value.isDirty }
        if (dirtyUsers.isEmpty()) {
            return CompletableFuture.completedFuture(null)
        }
        
        dirtyUsers.values.forEach { it.isDirty = false }

        val future = CompletableFuture<Void>()
        dbExecutor.execute {
            try {
                backend.saveAll(dirtyUsers).thenAccept {
                    future.complete(null)
                }.exceptionally { ex ->
                    future.completeExceptionally(ex)
                    null
                }
            } catch (e: Exception) {
                future.complete(null)
            }
        }
        return future
    }

    override fun close() {
        val dirtyUsers = cache.asMap().filter { it.value.isDirty }
        if (dirtyUsers.isNotEmpty()) {
            Utils.printInfo("Saving ${dirtyUsers.size} dirty users during server shutdown...")
            try {
                backend.saveAll(dirtyUsers)
                dirtyUsers.values.forEach { it.isDirty = false }
            } catch (e: Exception) {
                Utils.printError("Failed to perform final bulk save on shutdown: ${e.message}")
            }
        }

        dbExecutor.shutdown()
        try {
            if (!dbExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                Utils.printError("Database writer executor did not terminate in 10 seconds. Forcing shutdown.")
                dbExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            dbExecutor.shutdownNow()
        }
        backend.close()
    }
}
