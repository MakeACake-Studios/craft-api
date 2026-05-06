package org.makeacake.craft.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.makeacake.craft.recipe.RecipeRegistry

/**
 * A Bukkit listener responsible for handling player join events.
 *
 * This listener automatically grants any undiscovered custom recipes to the player's
 * recipe book upon joining the server.
 *
 * @author nalart11
 * @since 1.0.0
 */
class JoinListener : Listener {

    /**
     * Handles the [PlayerJoinEvent] triggered when a player connects to the server.
     *
     * Retrieves all registered custom recipe keys from [RecipeRegistry] and checks which ones
     * the player has not yet unlocked. Any undiscovered recipes are then added directly
     * to the player's recipe book.
     *
     * @param event The Bukkit event containing details about the joining player.
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        val allCustomKeys = RecipeRegistry.getAllKeys()
        val newKeys = allCustomKeys.filter { !player.hasDiscoveredRecipe(it) }

        if (newKeys.isNotEmpty()) {
            player.discoverRecipes(newKeys)
        }
    }
}