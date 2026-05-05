package org.makeacake.craft.item

import org.bukkit.NamespacedKey
import org.makeacake.craft.CraftAPI

data class ItemKey(val id: String) {
    val namespacedKey = NamespacedKey(CraftAPI.plugin, id.lowercase())
}