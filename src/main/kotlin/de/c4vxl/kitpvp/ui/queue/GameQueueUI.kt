package de.c4vxl.kitpvp.ui.queue

import de.c4vxl.gamemanager.gma.GMA
import de.c4vxl.gamemanager.gma.game.type.GameSize
import de.c4vxl.gamemanager.gma.player.GMAPlayer.Companion.gma
import de.c4vxl.gamemanager.language.Language
import de.c4vxl.gamemanager.language.Language.Companion.language
import de.c4vxl.gamemanager.utils.ItemBuilder
import de.c4vxl.kitpvp.queuing.Queuing
import de.c4vxl.kitpvp.ui.kit.KitUI
import de.c4vxl.kitpvp.ui.type.UI
import de.c4vxl.kitpvp.utils.Item.addMarginItems
import de.c4vxl.kitpvp.utils.Item.guiItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

/**
 * UI for queuing games
 */
class GameQueueUI(
    val player: Player,
    val language: Language = player.language.child("kitpvp")
): UI {
    private var currentSizeIndex: Int = 0
    private val sizes = GMA.possibleGameSizes
    private val size get() =
        GameSize.fromString(sizes.getOrNull(currentSizeIndex) ?: "") ?: GameSize(2, 1)

    private val baseInventory: Inventory
        get() =
            Bukkit.createInventory(null, 9 * 4, language.getCmp("ui.queue.title"))
                .apply {
                    addMarginItems(0..35)

                    // Size switcher
                    val previousSizeIndex = (currentSizeIndex - 1).let { if (it < 0) sizes.size - 1 else it }
                    val nextSizeIndex = (currentSizeIndex + 1).let { if (it >= sizes.size) 0 else it }

                    setItem(31, ItemBuilder(
                        Material.BEACON,
                        language.getCmp("ui.queue.item.size.name", sizes.getOrNull(currentSizeIndex) ?: "/"),
                        lore = mutableListOf(
                            language.getCmp("ui.queue.item.size.lore.1") as TextComponent,
                            Component.empty(),
                            language.getCmp("ui.queue.item.size.lore.2", sizes[nextSizeIndex]) as TextComponent,
                            language.getCmp("ui.queue.item.size.lore.3", sizes[previousSizeIndex]) as TextComponent
                        )
                    ).guiItem {
                        if (it.isLeftClick)
                            currentSizeIndex = nextSizeIndex

                        if (it.isRightClick)
                            currentSizeIndex = previousSizeIndex

                        open()
                    }
                        .build())

                    // Host item
                    setItem(12, ItemBuilder(Material.BOOKSHELF, language.getCmp("ui.queue.item.host.name"),
                        lore = mutableListOf(language.getCmp("ui.queue.item.host.lore.1") as TextComponent))
                        .guiItem {
                            KitUI(player, { kit ->
                                val game = Queuing.getGame(this@GameQueueUI.size, kit) ?: return@KitUI
                                player.closeInventory()
                                player.gma.join(game)
                            }, false)
                        }
                        .build())

                    // Join item
                    setItem(14, ItemBuilder(Material.IRON_SWORD, language.getCmp("ui.queue.item.join.name"),
                        lore = mutableListOf(language.getCmp("ui.queue.item.join.lore.1") as TextComponent))
                        .guiItem {
                            player.closeInventory()
                            val game = Queuing.findGame(this@GameQueueUI.size) ?: run {
                                player.sendMessage(language.getCmp("msg.error.no_game_found"))
                                return@guiItem
                            }
                            
                            player.gma.join(game)
                        }
                        .build())
                }

    init {
        open()
    }

    override fun open() {
        player.playSound(player.location, Sound.BLOCK_SCAFFOLDING_BREAK, 5f, 0.5f)
        player.openInventory(baseInventory)
        player.inventory.clear()
    }
}