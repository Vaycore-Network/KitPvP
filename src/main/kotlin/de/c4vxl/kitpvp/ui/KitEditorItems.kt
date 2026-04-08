package de.c4vxl.kitpvp.ui

import com.google.gson.Gson
import de.c4vxl.gamemanager.language.Language.Companion.language
import de.c4vxl.gamemanager.utils.ItemBuilder
import de.c4vxl.kitpvp.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player

object KitEditorItems{
    val file
        get() = Main.instance.dataFolder.resolve("kiteditor.json")

    fun getSections(player: Player): Map<String, List<ItemBuilder>> {
        val lang = player.language.child("kitpvp")

        val materials = Gson().fromJson<Map<String, List<String>>>(file.readText(), Map::class.java)

        return materials.mapValues { (section, names) ->
            names.mapNotNull { name ->
                val material = Material.entries.find { it.name == name } ?: return@mapNotNull null

                return@mapNotNull ItemBuilder(
                    material,
                    Component.translatable(material.translationKey()).color(NamedTextColor.WHITE),
                    lore = mutableListOf(lang.getCmp("editor.item.equip.lore") as TextComponent)
                )
            }
        }
    }

    fun getItems(player: Player, section: String) =
        getSections(player).getOrDefault(section, emptyList())
}