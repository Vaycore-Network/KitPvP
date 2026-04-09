package de.c4vxl.kitpvp.data

/**
 * Data class holding kit game rules
 */
data class KitGameRules(
    var isAlwaysDay: Boolean = true,
    var isKeepInventory: Boolean = true,
    var isFallDamage: Boolean = true,
    var numRounds: Int = 3,
    var health: Double = 20.0
)
