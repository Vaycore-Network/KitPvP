package de.c4vxl.kitpvp.data.struct.kit

import org.bukkit.Material

/**
 * Data object for server predefined kits
 */
data class ServerKit(
    val icon: String,
    val kit: Kit
) {
    val iconMaterial get() =
        Material.entries.find { it.name == icon } ?: Material.DIAMOND
}
