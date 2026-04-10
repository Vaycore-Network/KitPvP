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
            val kits = Database.get(player).kits
            val numKits = Main.config.getInt("config.kits.num-kits", 6)
            for (i in 0..<numKits)
                add(
                    kits[i] ?:
                    Kit.new(
                        player.language.child("kitpvp").get("kit.name.untitled", (i - kits.size + 1).toString()),
                        player
                    )
                )
        }
}