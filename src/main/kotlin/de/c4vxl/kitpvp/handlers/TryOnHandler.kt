package de.c4vxl.kitpvp.handlers

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import de.c4vxl.gamemanager.gma.player.GMAPlayer.Companion.gma
import de.c4vxl.kitpvp.Main
import de.c4vxl.kitpvp.utils.TryOn
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TryOnHandler : Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, Main.instance)
    }

    @EventHandler
    fun onJump(event: PlayerJumpEvent) {
        if (!TryOn.players.remove(event.player.uniqueId))
            return

        event.player.gma.quit()
    }
}