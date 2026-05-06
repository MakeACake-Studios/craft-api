package org.makeacake.craft.item

import org.bukkit.NamespacedKey
import org.makeacake.craft.CraftAPI

/**
 * Represents a unique identifier for a custom item.
 *
 * This class acts as a wrapper around a raw string ID and automatically
 * generates a Bukkit [NamespacedKey] associated with the main plugin instance.
 *
 * @property id The raw string identifier for the custom item (e.g., "magic_wand").
 *
 * @author nalart11
 * @since 1.0.0
 */
data class ItemKey(val id: String) {

    /**
     * The generated [NamespacedKey] used for storing and identifying the item
     * inside a PersistentDataContainer.
     *
     * It uses the plugin instance from [CraftAPI.plugin] and automatically
     * converts the [id] to lowercase to strictly comply with Bukkit's namespace rules.
     */
    val namespacedKey = NamespacedKey(CraftAPI.plugin, id.lowercase())
}