package de.c4vxl.kitpvp.handlers

import de.c4vxl.gamemanager.gma.player.GMAPlayer.Companion.gma
import de.c4vxl.kitpvp.Main
import de.c4vxl.kitpvp.data.extensions.Extensions.kitData
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * This handler takes care of implementing kit rules that need custom handling
 */
class KitRulesHandler : Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, Main.instance)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val game = event.player.gma.game ?: return
        val kit = game.kitData.kit ?: return

        // Block breaking enabled
        if (kit.rules.isAllowBlockBreaking)
            return

        event.isCancelled = true
    }
}