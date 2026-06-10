package org.makeacake.craft.listener

import org.bukkit.Bukkit
import org.bukkit.block.BrewingStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.makeacake.craft.CraftAPI
import org.makeacake.craft.recipe.RecipeRegistry

class BrewListener : Listener {

    @EventHandler
    fun onBrew(event: BrewEvent) {
        val inventory = event.contents
        val ingredient = inventory.ingredient ?: return
        val stand = event.block.state as? BrewingStand ?: return

        var handled = false

        for (i in 0..2) {
            val baseItem = inventory.getItem(i) ?: continue
            val recipe = RecipeRegistry.matchBrew(ingredient, baseItem)

            if (recipe != null) {
                event.isCancelled = true
                handled = true

                val resultItem = recipe.item.createItemStack()
                inventory.setItem(i, resultItem)

                recipe.brewActions.forEach { action ->
                    action.handler(stand, resultItem, event)
                }
            }
        }

        if (handled) {
            val remainingIngredient = ingredient.clone()
            remainingIngredient.amount -= 1
            inventory.ingredient = if (remainingIngredient.amount > 0) remainingIngredient else null
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.clickedInventory?.type != InventoryType.BREWING) return

        Bukkit.getScheduler().runTaskLater(CraftAPI.plugin, Runnable {
            val inventory = event.clickedInventory ?: return@Runnable
            val stand = inventory.holder as? BrewingStand ?: return@Runnable

            if (stand.brewingTime > 0) return@Runnable

            val ingredient = inventory.getItem(3) ?: return@Runnable

            var canBrew = false
            for (i in 0..2) {
                val base = inventory.getItem(i) ?: continue
                if (RecipeRegistry.matchBrew(ingredient, base) != null) {
                    canBrew = true
                    break
                }
            }

            if (canBrew) {
                stand.brewingTime = 400
                stand.update(true)
            }
        }, 1L)
    }
}