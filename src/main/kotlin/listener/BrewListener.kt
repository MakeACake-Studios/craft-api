package org.makeacake.craft.listener

import org.bukkit.block.BrewingStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.BrewEvent
import org.makeacake.craft.recipe.RecipeRegistry

class BrewListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBrew(event: BrewEvent) {
        val inventory = event.contents
        val ingredient = inventory.ingredient ?: return
        val stand = event.block.state as? BrewingStand ?: return

        for (slot in 0 until minOf(3, event.results.size)) {
            val base = inventory.getItem(slot) ?: continue
            val recipe = RecipeRegistry.matchBrew(ingredient, base) ?: continue
            val result = recipe.item.createItemStack()
            event.results[slot] = result
            recipe.brewActions.forEach { it.handler(stand, result, event) }
        }
    }
}
