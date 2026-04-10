package de.c4vxl.kitpvp.handlers

import de.c4vxl.gamelobby.lobby.Lobby.isInLobby
import de.c4vxl.kitpvp.Main
import de.c4vxl.kitpvp.ui.type.UI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

/**
 * Handles different ui events
 */
class UIHandler : Listener {
    companion object {
        val nonClosable = mutableMapOf<UUID, UI>()
    }
    
    init {
        Bukkit.getPluginManager().registerEvents(this, Main.instance)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        nonClosable.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onInvClose(event: InventoryCloseEvent) {
        if (!(event.player as? Player ?: return).isInLobby)
            return

        val ui = nonClosable[event.player.uniqueId] ?: return
        if (event.reason == InventoryCloseEvent.Reason.PLAYER) {
            Bukkit.getScheduler().runTask(Main.instance, Runnable {
                ui.open()
            })
        }

        nonClosable.remove(event.player.uniqueId)
    }
}