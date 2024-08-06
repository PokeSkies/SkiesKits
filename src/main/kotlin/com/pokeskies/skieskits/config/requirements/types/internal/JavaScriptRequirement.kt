package com.pokeskies.skieskits.config.requirements.types.internal


import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.level.ServerPlayer
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Value

class JavaScriptRequirement(
    type: RequirementType = RequirementType.PERMISSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val expression: String = ""
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData): Boolean {
        if (!checkComparison())
            return false

        val context = Context.newBuilder()
            .engine(SkiesKits.INSTANCE.graalEngine)
            .build()

        try {
            val parsed = Utils.parsePlaceholders(player, expression, kitId, kit, kitData)

            Utils.printDebug("Checking a ${type?.identifier} Requirement with parsed expression='$parsed': $this")

            val result: Value = context.eval("js", parsed)

            if (!result.isBoolean) {
                Utils.printError("A Javascript Requirement expression '$expression' did not return a boolean!")
                return false
            }

            return if (comparison == ComparisonType.EQUALS) result.asBoolean() else !result.asBoolean()
        } catch (e: Exception) {
            Utils.printError("An error occurred while parsing the Javascript Requirement expression '$expression': ${e.printStackTrace()}")
        }

        return false
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }

    override fun toString(): String {
        return "JavascriptRequirement(comparison=$comparison, expression='$expression')"
    }

}
