package de.c4vxl.kitpvp.data

import de.c4vxl.gamelobby.Main
import de.c4vxl.gamemanager.language.Language.Companion.language
import de.c4vxl.kitpvp.data.struct.kit.Kit
import org.bukkit.entity.Player

/**
 * Access point for accessing player-specific kit data
 */
object PlayerKitData {
    /**
     * Returns all kits a player owns
     */
    fun getKits(player: Player) =
        buildList {
            val numKits = Main.config.getInt("config.kits.num-kits", 6)

            // Add first n kits
            addAll(Database.get(player).kits.take(numKits))

            // Add one extra kit if enough space
            if (size < numKits)
                add(Kit.new(
                    player.language.child("kitpvp").get("kit.name.untitled"),
                    player
                ))
        }
}