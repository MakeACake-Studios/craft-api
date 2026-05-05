package org.makeacake.craft.item

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.ClickAction
import org.makeacake.craft.action.ItemAction

/**
 * Builder для кастомных предметов.
 *
 * @author nalart11
 * @since 0.7b
 */
class CustomItemBuilder(private val id: String) {

    private lateinit var item: ItemStack
    private var displayName: Component? = null
    private var lore: List<Component>? = null
    private val actions = mutableListOf<ItemAction>()

    fun item(stack: ItemStack) = apply { this.item = stack }
    fun name(name: Component) = apply { this.displayName = name }
    fun lore(vararg lines: Component) = apply { this.lore = lines.toList() }
    fun action(action: ItemAction, cooldownSeconds: Int? = null) = apply {
        if (cooldownSeconds != null) {
            action.cooldownSeconds = cooldownSeconds
        }
        this.actions += action
    }
    fun actions(
        vararg types: ActionType,
        cooldownSeconds: Int? = null,
        handler: (Player, ItemStack, PlayerInteractEvent) -> Unit
    ) = apply {
        for (type in types) {
            this.actions.add(ClickAction(type, cooldownSeconds, handler))
        }
    }

    fun build(): CustomItem {
        val itemKey = ItemKey(id)
        val template = item.clone()
        template.editMeta { meta ->
            displayName?.let { meta.displayName(it) }
            lore?.let { meta.lore(it) }
            meta.persistentDataContainer.set(itemKey.namespacedKey, PersistentDataType.BYTE, 1)
        }

        return CustomItem(itemKey, template, actions)
    }
}
