package com.pokeskies.skieskits.placeholders

interface ServerPlaceholder {
    fun handle(args: List<String>): GenericResult
    fun id(): List<String>
}
