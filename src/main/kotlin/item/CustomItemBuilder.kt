package org.makeacake.craft.item

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.ClickAction
import org.makeacake.craft.action.ItemAction

/**
 * A builder class designed for constructing [CustomItem] instances in a fluent, declarative manner.
 *
 * @param id The raw string identifier that will be used to generate the [ItemKey].
 *
 * @author nalart11
 * @since 1.0.0
 */
class CustomItemBuilder(private val id: String) {

    private lateinit var item: ItemStack
    private var displayName: Component? = null
    private var lore: List<Component>? = null
    private val actions = mutableListOf<ItemAction>()

    /**
     * Sets the base vanilla [ItemStack] used as a template for the custom item.
     * This is a required step before calling [build].
     *
     * @param stack The base item stack.
     * @return This builder instance for chaining.
     */
    fun item(stack: ItemStack) = apply { this.item = stack }

    /**
     * Sets the custom display name using the Adventure API [Component].
     *
     * @param name The display name component.
     * @return This builder instance for chaining.
     */
    fun name(name: Component) = apply { this.displayName = name }

    /**
     * Sets the custom lore for the item using Adventure API [Component]s.
     *
     * @param lines An array of components representing the lore lines.
     * @return This builder instance for chaining.
     */
    fun lore(vararg lines: Component) = apply { this.lore = lines.toList() }

    /**
     * Registers a specific [ItemAction] to be triggered when interacting with this item.
     *
     * @param action The action implementation to add.
     * @param cooldownSeconds An optional cooldown override for this specific action.
     * @return This builder instance for chaining.
     */
    fun action(action: ItemAction, cooldownSeconds: Int? = null) = apply {
        if (cooldownSeconds != null) {
            action.cooldownSeconds = cooldownSeconds
        }
        this.actions += action
    }

    /**
     * A convenience method to register a generic [ClickAction] for multiple [ActionType]s at once.
     *
     * @param types The interaction types (e.g., RIGHT_CLICK, LEFT_CLICK) that will trigger the handler.
     * @param cooldownSeconds Optional cooldown in seconds applied to all generated actions.
     * @param handler The logic to execute upon interaction.
     * @return This builder instance for chaining.
     */
    fun actions(
        vararg types: ActionType,
        cooldownSeconds: Int? = null,
        handler: (Player, ItemStack, PlayerInteractEvent) -> Unit
    ) = apply {
        for (type in types) {
            this.actions.add(ClickAction(type, cooldownSeconds, handler))
        }
    }

    /**
     * Compiles the configuration and constructs the final [CustomItem].
     *
     * Applies the custom display name, lore, and injects the generated [ItemKey]
     * into the item's PersistentDataContainer.
     *
     * @return A ready-to-use [CustomItem] instance.
     * @throws UninitializedPropertyAccessException If the base [item] was not set before building.
     */
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