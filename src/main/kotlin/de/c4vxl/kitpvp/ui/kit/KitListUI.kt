package de.c4vxl.kitpvp.ui.kit

import de.c4vxl.gamemanager.language.Language
import de.c4vxl.gamemanager.language.Language.Companion.language
import de.c4vxl.kitpvp.data.PlayerKitData
import de.c4vxl.kitpvp.data.struct.kit.Kit
import de.c4vxl.kitpvp.handlers.UIHandler
import de.c4vxl.kitpvp.ui.type.UI
import de.c4vxl.kitpvp.utils.Item.addMarginItems
import de.c4vxl.kitpvp.utils.Item.guiItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * UI of kits of another player to choose
 * @param holder The player whose kits to choose of
 */
class KitListUI(
    val player: Player,
    val holder: OfflinePlayer,
    val onChoose: ((Kit) -> Unit)? = null,
    val returnTo: UI? = null,
    val language: Language = player.language.child("kitpvp")
): UI {
    private val baseInventory: Inventory get() =
        Bukkit.createInventory(null, 9 * 5, language.getCmp("ui.kits_list.title", holder.name ?: "???"))
            .apply {
                // Margin items
                addMarginItems(0..17, 36..44, 0..36 step 9, 17..44 step 9)

                setItem(4, ItemStack(Material.PLAYER_HEAD)
                    .apply {
                        (itemMeta as? SkullMeta)?.apply {
                            owningPlayer = this@KitListUI.holder
                            displayName(language.getCmp("ui.kits_list.item.head", this@KitListUI.holder.name ?: ""))
                        }?.let { this.itemMeta = it }
                    })

                PlayerKitData.getKits(this@KitListUI.holder, false).forEach { kit ->
                    addItem(KitUI.customKitItem(kit, language, KitUI.Mode.CHOOSE)
                        .guiItem {
                            if (it.isRightClick)
                                KitUI.openEditor(player, kit, KitUI.Mode.CHOOSE, this@KitListUI)
                            else
                                onChoose?.invoke(kit)
                        }.build())
                }
            }

    init {
        open()
    }

    override fun open() {
        player.playSound(player.location, Sound.BLOCK_SCAFFOLDING_BREAK, 5f, 0.5f)
        player.openInventory(baseInventory)
        player.inventory.clear()
        returnTo?.let { UIHandler.nonClosable[player.uniqueId] = it }
    }
}