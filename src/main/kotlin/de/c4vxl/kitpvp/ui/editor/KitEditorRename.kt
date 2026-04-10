package de.c4vxl.kitpvp.ui.editor

import de.c4vxl.kitpvp.data.Kit
import de.c4vxl.kitpvp.handlers.UIHandler
import de.c4vxl.kitpvp.ui.general.AnvilUI
import org.bukkit.Sound
import org.bukkit.entity.Player

object KitEditorRename {
    fun open(player: Player, kit: Kit, onDone: (Kit) -> Unit) {
        AnvilUI(
            player,
            "editor.page.rename.title",
            "editor.page.rename.confirm",
            {
                kit.name = it.takeIf { it.isNotBlank() } ?: kit.name
                player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 5f, 1f)
                UIHandler.nonClosable.remove(player.uniqueId)
                onDone(kit)
            },
            kit.name
        )
    }
}