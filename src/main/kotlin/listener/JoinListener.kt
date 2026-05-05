package org.makeacake.craft.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.makeacake.craft.recipe.RecipeRegistry
import org.makeacake.craft.util.CooldownManager

/**
 * Слушатель входа игрока.
 * Выдает новые рецепты и очищает кулдауны.
 *
 * @author nalart11
 * @since 0.7b
 */
class JoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        val allCustomKeys = RecipeRegistry.getAllKeys()
        val newKeys = allCustomKeys.filter { key -> !player.hasDiscoveredRecipe(key) }
        if (newKeys.isNotEmpty()) {
            player.discoverRecipes(newKeys)
        }
    }
}
