package de.c4vxl.kitpvp.ui.kit

import de.c4vxl.gamemanager.language.Language
import de.c4vxl.gamemanager.language.Language.Companion.language
import de.c4vxl.gamemanager.utils.ItemBuilder
import de.c4vxl.kitpvp.data.Database
import de.c4vxl.kitpvp.data.PlayerKitData
import de.c4vxl.kitpvp.data.ServerKits
import de.c4vxl.kitpvp.data.extensions.Extensions.lastKit
import de.c4vxl.kitpvp.data.struct.kit.Kit
import de.c4vxl.kitpvp.data.struct.kit.ServerKit
import de.c4vxl.kitpvp.ui.general.PlayerSearchUI
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

                if (mode == Mode.CHOOSE)
                    setItem(8, ItemBuilder(
                        Material.NAME_TAG,
                        language.getCmp("ui.kits.item.others_kits.name"),
                        lore = listOf(language.getCmp("ui.kits.item.others_kits.desc"))
                    ).guiItem {
                        PlayerSearchUI(player, false, { other ->
                            if (other == null) {
                                player.sendMessage(language.getCmp("ui.kits.others_kits.error.invalid_player"))
                                return@PlayerSearchUI
                            }

                            KitListUI(player, other, { onChoose?.invoke(it) }, this@KitUI)
                        })
                    }.build())

                // Last kit item
                if (mode == Mode.CHOOSE)
                    player.lastKit?.let { lastKit ->
                        setItem(17, ItemBuilder(
                            Material.GREEN_SHULKER_BOX,
                            language.getCmp("ui.kits.item.last_kit.name"),
                            lore = listOf(
                                language.getCmp("ui.kits.item.last_kit.lore.1"),
                                language.getCmp("ui.kits.item.last_kit.lore.2", lastKit.metadata.name)
                            )
                        ).guiItem { onChoose?.invoke(lastKit) }.build())
                    }

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
            lore = buildList {
                add(language.getCmp("ui.kits.item.kit.lore.${mode.name.lowercase()}"))

                if (mode == Mode.CHOOSE)
                    add(language.getCmp("ui.kits.item.kit.lore.view"))
            }
        )
            .guiItem {
                when (mode) {
                    Mode.CHOOSE -> {
                        if (it.isRightClick)
                            openEditor(player, kit.kit, Mode.CHOOSE, this)
                        else
                            onChoose?.invoke(kit.kit)
                    }
                    Mode.EDIT -> openEditor(player, kit.kit, Mode.EDIT, this)
                }
            }
            .build()

    companion object {
        fun openEditor(player: Player, kit: Kit, mode: Mode, returnTo: UI?) =
            KitLayout(
                player,
                kit,
                mode,
                { updated ->
                    Database.update(player) {
                        this.offsets[kit.metadata.name] = updated
                    }
                },
                Database.get(player).offsets.getOrDefault(kit.metadata.name, mapOf()),
                returnTo
            )

        fun customKitItem(kit: Kit, language: Language, mode: Mode) =
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

                    if (mode == Mode.CHOOSE)
                        add(language.getCmp("ui.kits.item.kit.lore.view"))
                }.toMutableList()
            )
    }

    private fun customKitItem(kit: Kit, idx: Int) =
        Companion.customKitItem(kit, language, mode)
            .guiItem {
                when (mode) {
                    Mode.CHOOSE -> {
                        if (it.isRightClick)
                            openEditor(player, kit, Mode.CHOOSE, this)
                        else
                            onChoose?.invoke(kit)
                    }
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
            }.build()

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