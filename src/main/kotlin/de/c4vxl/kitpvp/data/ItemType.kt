package de.c4vxl.kitpvp.data

import org.bukkit.Material

private val tierOrder = mapOf(
    "WOODEN" to 0,
    "STONE" to 1,
    "GOLDEN" to 2,
    "COPPER" to 3,
    "IRON" to 4,
    "DIAMOND" to 5,
    "NETHERITE" to 6
)

private fun findMaterials(id: String) =
    Material.entries.filter { it.name.endsWith("_$id") }
        .sortedBy { tierOrder.getOrDefault(it.name.substringBefore("_"), Int.MAX_VALUE) }

enum class ItemType(
    val materials: List<Material>
) {
    PICKAXE(findMaterials("PICKAXE")),
    AXE(findMaterials("AXE")),
    SWORD(findMaterials("SWORD")),
    SHOVEL(findMaterials("SHOVEL")),
    SPEAR(findMaterials("SPEAR")),
    HELMET(findMaterials("HELMET")),
    CHESTPLATE(findMaterials("CHESTPLATE")),
    LEGGINGS(findMaterials("LEGGINGS")),
    BOOTS(findMaterials("BOOTS"))


    ;

    companion object {
        /**
         * Returns an item type from a material
         * @param material The material
         */
        fun fromMaterial(material: Material): ItemType? =
            ItemType.entries.find { it.materials.contains(material) }
    }
}