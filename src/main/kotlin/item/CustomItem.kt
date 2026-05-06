package org.makeacake.craft.item

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.makeacake.craft.action.ItemAction

/**
 * A custom item representation within the server.
 *
 * Defines an item that can be created or obtained by players,
 * assigning it unique properties and custom interaction behaviors.
 *
 * @property key The unique identifier ([ItemKey]) of the custom item.
 * @property item The base vanilla [ItemStack] used as a template for this custom item.
 * @property actions A list of [ItemAction]s defining behaviors upon interaction.
 *
 * @author nalart11
 * @since 1.0.0
 */
class CustomItem(
    val key: ItemKey,
    val item: ItemStack,
    val actions: List<ItemAction>
) {

    /**
     * Checks if the specified [ItemStack] matches this custom item.
     *
     * This is determined by verifying the presence of the custom item's
     * namespaced key within the item's PersistentDataContainer.
     *
     * @param stack The [ItemStack] to verify. Can be null.
     * @return `true` if the item matches this custom item, `false` otherwise.
     */
    fun matches(stack: ItemStack?): Boolean {
        if (stack == null || !stack.hasItemMeta()) return false
        val pdc = stack.itemMeta.persistentDataContainer
        return pdc.has(key.namespacedKey, PersistentDataType.BYTE)
    }

    /**
     * Creates a new [ItemStack] based on the custom item template.
     *
     * Automatically injects the custom item's identifier into the PersistentDataContainer
     * to allow future identification via the [matches] method.
     *
     * @param amount The stack size of the newly created item. Defaults to 1.
     * @param modifier An optional lambda to override specific meta properties
     * (e.g., display name, lore, custom model data, or skull texture).
     * @return A ready-to-use custom [ItemStack].
     */
    fun createItemStack(amount: Int = 1, modifier: (ItemStack) -> Unit = {}): ItemStack {
        val stack = item.clone()
        stack.amount = amount
        stack.editMeta { meta ->
            meta.persistentDataContainer.set(key.namespacedKey, PersistentDataType.BYTE, 1)
        }

        modifier(stack)

        return stack
    }
}