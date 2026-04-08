package de.c4vxl.kitpvp.data

import de.c4vxl.gamemanager.utils.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material

data class KitItem(
    val material: Material,
    val amount: Int = 1,
    val unbreakable: Boolean = false,
    val name: String? = null
) {
    val nameComponent =
        // Load custom name as component
        name?.let { MiniMessage.miniMessage().deserialize(it) }

            // Otherwise use default name
            ?: Component.translatable(material.translationKey())

    val builder: ItemBuilder =
        ItemBuilder(
            material = material,
            name = nameComponent,
            amount = amount,
            unbreakable = unbreakable
        )
}