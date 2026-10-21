package org.makeacake.craft.recipe

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import java.util.function.Predicate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrewingRecipeRegistrarTest {
    @Test
    fun usesTheApiAvailableOnThisPaperVersion() {
        val version = System.getProperty("paperApiVersion")
        assertEquals(version.startsWith("26.3."), BrewingRecipeRegistrar.usesModernApi)
        if (!BrewingRecipeRegistrar.usesModernApi) return

        val recipeClass = Class.forName("org.bukkit.inventory.BrewingRecipe")
        assertTrue(Recipe::class.java.isAssignableFrom(recipeClass))
        recipeClass.getConstructor(
            NamespacedKey::class.java, ItemStack::class.java,
            RecipeChoice::class.java, RecipeChoice::class.java
        )
        RecipeChoice::class.java.getMethod(
            "predicateChoice", Predicate::class.java, ItemStack::class.java
        )
    }
}
