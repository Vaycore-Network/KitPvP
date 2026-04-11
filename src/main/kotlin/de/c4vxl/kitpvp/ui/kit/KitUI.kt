package de.c4vxl.kitpvp.ui.kit

import de.c4vxl.gamemanager.language.Language
import de.c4vxl.gamemanager.language.Language.Companion.language
import de.c4vxl.gamemanager.utils.ItemBuilder
import de.c4vxl.kitpvp.data.Database
import de.c4vxl.kitpvp.data.PlayerKitData
import de.c4vxl.kitpvp.data.ServerKits
import de.c4vxl.kitpvp.data.struct.kit.Kit
import de.c4vxl.kitpvp.data.struct.kit.ServerKit
import de.c4vxl.kitpvp.ui.inspect.KitInspector
import de.c4vxl.kitpvp.ui.type.UI
import de.c4vxl.kitpvp.utils.Item
import de.c4vxl.kitpvp.utils.Item.addMarginItems
import de.c4vxl.kitpvp.utils.Item.enchantmentGlow
import de.c4vxl.kitpvp.utils.Item.guiItem
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class KitUI(
    val player: Player,
    private val mode: Mode = Mode.CHOOSE,
    val onChoose: ((Kit) -> Unit)? = null,
    val language: Language = player.language.child("kitpvp")
): UI {
    private var currentPage = Page.SERVER_KITS

    enum class Mode {
        EDIT,
        CHOOSE
    }

    private val baseInventory: Inventory get() =
        Bukkit.createInventory(null, 9 * 5, language.getCmp("ui.kits.title.${currentPage.name.lowercase()}"))
            .apply {
                // Margin items
                addMarginItems(0..17, 36..44, 0..36 step 9, 8..44 step 9)

                // Tab items
                setItem(3, ItemBuilder(
                    Material.DIAMOND_SWORD,
                    language.getCmp("ui.kits.item.general_kits.name")
                )
                    .guiItem { open(Page.SERVER_KITS) }
                    .build().let { if (currentPage == Page.SERVER_KITS) it.enchantmentGlow() else it })

                setItem(5, ItemBuilder(
                    Material.NETHER_STAR,
                    language.getCmp("ui.kits.item.your_kits.name")
                )
                    .guiItem { open(Page.CUSTOM_KITS) }
                    .build().let { if (currentPage == Page.CUSTOM_KITS) it.enchantmentGlow() else it })


                when (currentPage) {
                    // Add custom kits
                    Page.CUSTOM_KITS -> {
                        val allowEdits = mode == Mode.EDIT
                        PlayerKitData.getKits(player, allowEdits).let { kits ->
                            repeat(14) { i ->
                                addItem(kits.getOrNull(i)?.let { customKitItem(it, i) }
                                    ?: Item.marginItem(Material.GRAY_STAINED_GLASS_PANE))
                            }

                            if (kits.isEmpty() && !allowEdits)
                                setItem(37, ItemBuilder(
                                    Material.RED_STAINED_GLASS_PANE,
                                    language.getCmp("ui.kits.item.notice.no_kits.0"),
                                    lore = mutableListOf(
                                        language.getCmp("ui.kits.item.notice.no_kits.1") as TextComponent,
                                        language.getCmp("ui.kits.item.notice.no_kits.2") as TextComponent
                                    )
                                ).guiItem().build())
                        }
                    }

                    // Add server kits
                    Page.SERVER_KITS -> {
                        ServerKits.kits.let { kits ->
                            repeat(14) { i ->
                                addItem(kits.getOrNull(i)?.let { serverKitItem(it) }
                                    ?: Item.marginItem(Material.GRAY_STAINED_GLASS_PANE))
                            }
                        }
                    }
                }
            }

    enum class Page {
        SERVER_KITS,
        CUSTOM_KITS
    }

    private fun serverKitItem(kit: ServerKit) =
        ItemBuilder(
            kit.iconMaterial,
            language.getCmp("ui.kits.item.kit.name", kit.kit.metadata.name),
            lore = mutableListOf(language.getCmp("ui.kits.item.kit.lore.${mode.name.lowercase()}") as TextComponent)
        )
            .guiItem {
                when (mode) {
                    Mode.CHOOSE -> onChoose?.invoke(kit.kit)
                    Mode.EDIT -> KitLayout(
                        player,
                        kit.kit,
                        { updated ->
                            Database.update(player) {
                                this.offsets[kit.kit.metadata.name] = updated
                            }
                        },
                        Database.get(player).offsets.getOrDefault(kit.kit.metadata.name, mapOf()),
                        this
                    )
                }
            }
            .build()

    private fun customKitItem(kit: Kit, idx: Int) =
        ItemBuilder(
            if (kit.isEmpty) Material.WRITABLE_BOOK
            else Material.BOOK,
            language.getCmp("ui.kits.item.kit.name", kit.metadata.name),
            lore = buildList {
                val start = if (kit.isEmpty) 3 else 0
                for (i in start..2) {
                    add(language.getCmp("ui.kits.item.kit.lore.${i + 1}", kit.metadata.createdAt, kit.metadata.lastEdit) as TextComponent)
                }
                add(language.getCmp("ui.kits.item.kit.lore.${mode.name.lowercase()}") as TextComponent)
            }.toMutableList()
        )
            .guiItem {
                when (mode) {
                    Mode.CHOOSE -> onChoose?.invoke(kit)
                    Mode.EDIT -> KitInspector(player, kit, onUpdate = { updated ->
                        Database.update(player) {
                            if (updated == null) {
                                if (kits.size > idx)
                                    kits.removeAt(idx)
                            } else {
                                if (kits.size <= idx)
                                    kits.add(kit)
                                else
                                    kits[idx] = updated
                            }
                        }

                        open()
                    }, returnTo = this@KitUI)
                }
            }
            .build()

    init {
        open()
    }

    private fun open(page: Page) {
        this.currentPage = page
        player.playSound(player.location, Sound.BLOCK_SCAFFOLDING_BREAK, 5f, 0.5f)
        player.openInventory(baseInventory)
        player.inventory.clear()
    }

    override fun open() {
        open(currentPage)
    }
}