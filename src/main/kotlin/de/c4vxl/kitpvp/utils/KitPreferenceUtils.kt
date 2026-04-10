package de.c4vxl.kitpvp.utils

import de.c4vxl.kitpvp.data.struct.kit.item.KitItem

object KitPreferenceUtils {
    /**
     * Calculates the offsets between the original kit inventory and the modified one
     * @param originalInventory The original inventory
     * @param updatedInventory The updated inventory
     */
    fun calculateOffsets(originalInventory: Map<Int, KitItem>, updatedInventory: Map<Int, KitItem>): MutableMap<Int, Int> {
        val offsets = mutableMapOf<Int, Int>()

        for (slot in 0..35) {
            val newItem = updatedInventory[slot] ?: continue

            val originalSlot = originalInventory.entries
                .firstOrNull { (_, value) -> value == newItem }
                ?.key

            if (originalSlot != null && originalSlot != slot)
                offsets[originalSlot] = slot
        }

        return offsets
    }

    /**
     * Applies an offset mapping to an inventory
     * @param inventory The inventory
     * @param offsets The offsets to apply
     */
    fun applyOffsets(inventory: Map<Int, KitItem>, offsets: Map<Int, Int>) =
        buildMap {
            inventory.forEach { (slot, item) ->
                val newSlot = offsets.getOrDefault(slot, slot)
                put(newSlot, item)
            }
        }

}