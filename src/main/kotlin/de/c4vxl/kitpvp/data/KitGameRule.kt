package de.c4vxl.kitpvp.data

import de.c4vxl.gamemanager.language.Language
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material

enum class KitGameRule(
    val icon: Material,
    val type: Class<*>
) {
    ALWAYS_DAY(Material.CLOCK, Boolean::class.java),
    KEEP_INVENTORY(Material.CHEST, Boolean::class.java),
    FALL_DAMAGE(Material.DIAMOND_BOOTS, Boolean::class.java),
    NUM_ROUNDS(Material.COMPASS, Int::class.java),
    HEALTH(Material.GOLDEN_APPLE, Double::class.java)

    ;

    fun getNameKey(value: Any) =
        when (type) {
            Boolean::class.java -> "rule.${this.name.lowercase()}.name.$value"
            else -> "rule.${this.name.lowercase()}.name"
        }

    fun getLore(language: Language, value: Any) =
        buildList {
            val typeName = when (this@KitGameRule.type) {
                Boolean::class.java -> "boolean"
                Int::class.java -> "int"
                Double::class.java -> "double"
                else -> "num"
            }

            for (i in 1..10) {
                val key = "rule.type.$typeName.lore.$i"
                if (language.get(key) == key)
                    break

                add(language.getCmp(key, value.toString()) as TextComponent)
            }
        }
}