package org.makeacake.craft.item

import org.bukkit.inventory.ItemStack

/**
 * Реестр кастомных предметов сервера.
 *
 * @author nalart11
 * @since 0.7b
 */
object ItemRegistry {

    private val items = mutableMapOf<ItemKey, CustomItem>()

    fun register(item: CustomItem) {
        items[item.key] = item
    }

    fun byItemStack(stack: ItemStack?): CustomItem? {
        return items.values.firstOrNull { it.matches(stack) }
    }

    fun byKey(key: ItemKey): CustomItem? {
        return items[key]
    }
}
