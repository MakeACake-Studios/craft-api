package org.makeacake.craft.item

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * A centralized registry for managing all custom server items.
 *
 * This singleton handles the registration and retrieval of [CustomItem] instances,
 * allowing developers to easily resolve custom items either by their unique [ItemKey]
 * or by inspecting an in-game [ItemStack].
 *
 * @author nalart11
 * @since 1.0.0
 */
object ItemRegistry {

    private val items = mutableMapOf<ItemKey, CustomItem>()

    /**
     * Registers a [CustomItem] into the system.
     *
     * If an item with the same [ItemKey] is already registered, it will be overwritten.
     *
     * @param item The custom item instance to register.
     */
    fun register(item: CustomItem) {
        items[item.key] = item
    }

    /**
     * Attempts to resolve a given Bukkit [ItemStack] to a registered [CustomItem].
     *
     * This method iterates through all registered items and relies on the
     * [CustomItem.matches] method to verify the item's PersistentDataContainer tags.
     *
     * @param stack The [ItemStack] to evaluate. Can be null.
     * @return The matching [CustomItem], or `null` if the stack is null or not recognized as a custom item.
     */
    fun byItemStack(stack: ItemStack?): CustomItem? {
        val pdc = stack?.persistentDataContainer ?: return null
        return items.values.firstOrNull { customItem ->
            pdc.has(customItem.key.namespacedKey, PersistentDataType.BYTE)
        }
    }

    /**
     * Retrieves a registered [CustomItem] directly by its specific [ItemKey].
     *
     * This lookup is highly efficient as it uses the underlying Map structure (O(1)).
     *
     * @param key The unique identifier of the item.
     * @return The corresponding [CustomItem], or `null` if no item with this key is registered.
     */
    fun byKey(key: ItemKey): CustomItem? = items[key]

    /**
     * Retrieves a registered [CustomItem] directly by its specific [ItemKey.id].
     *
     * This lookup is highly efficient as it uses the underlying Map structure (O(1)).
     *
     * @param id String identifier of the item.
     * @return The corresponding [CustomItem], or `null` if no item with this id is registered.
     */
    fun byId(id: String): CustomItem? = items[ItemKey(id)]
}