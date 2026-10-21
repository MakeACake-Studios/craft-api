package org.makeacake.craft.recipe

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.makeacake.craft.item.CustomItem
import java.util.function.Predicate

/** Uses the 26.3 BrewingRecipe when present without linking it on older servers. */
internal object BrewingRecipeRegistrar {
    private val modernRecipeClass: Class<*>? by lazy {
        try {
            Class.forName("org.bukkit.inventory.BrewingRecipe", false, Recipe::class.java.classLoader)
        } catch (_: ClassNotFoundException) {
            null
        }
    }

    internal val usesModernApi: Boolean
        get() = modernRecipeClass != null

    fun customChoice(custom: CustomItem): RecipeChoice {
        if (modernRecipeClass == null) return LegacyBrewingRecipeRegistrar.customChoice(custom)

        return modernChoice(custom.createItemStack(), Predicate { custom.matches(it) })
    }

    private fun modernChoice(example: ItemStack, predicate: Predicate<ItemStack>): RecipeChoice {
        val factory = RecipeChoice::class.java.getMethod(
            "predicateChoice", Predicate::class.java, ItemStack::class.java
        )
        return factory.invoke(null, predicate, example) as RecipeChoice
    }

    /** Returns true if the recipe was registered through the new Bukkit recipe registry. */
    fun register(
        key: NamespacedKey,
        result: ItemStack,
        input: RecipeChoice,
        ingredient: RecipeChoice
    ): Boolean {
        val recipeClass = modernRecipeClass ?: run {
            LegacyBrewingRecipeRegistrar.register(key, result, input, ingredient)
            return false
        }

        val recipe = createModernRecipe(recipeClass, key, result, input, ingredient)
        check(Bukkit.addRecipe(recipe)) { "Could not register brewing recipe $key" }
        return true
    }

    private fun createModernRecipe(
        recipeClass: Class<*>, key: NamespacedKey, result: ItemStack,
        input: RecipeChoice, ingredient: RecipeChoice
    ): Recipe = recipeClass.getConstructor(
            NamespacedKey::class.java, ItemStack::class.java,
            RecipeChoice::class.java, RecipeChoice::class.java
        ).newInstance(key, result, input, ingredient) as Recipe
}
