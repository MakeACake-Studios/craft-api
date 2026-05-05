package org.makeacake.craft.recipe

import org.bukkit.inventory.Recipe
import java.util.UUID

/**
 * Реестр всех кастомных рецептов сервера.
 *
 * Хранит связь между Bukkit Recipe и CustomRecipe.
 *
 * @author nalart11
 * @since 0.7b
 */
object RecipeRegistry {

    private val recipes = mutableMapOf<Recipe, CustomRecipe>()

    /**
     * Регистрирует кастомный рецепт.
     *
     * @param recipe Кастомный рецепт
     */
    fun register(recipe: CustomRecipe) {
        recipes[recipe.recipe] = recipe
    }

    /**
     * Получает кастомный рецепт по Bukkit Recipe.
     *
     * @param recipe Bukkit-рецепт
     * @return CustomRecipe или null
     */
    fun byRecipe(recipe: Recipe?): CustomRecipe? {
        if (recipe == null) return null
        return recipes[recipe]
    }

    fun getAllKeys(): List<org.bukkit.NamespacedKey> {
        return recipes.keys.filterIsInstance<org.bukkit.inventory.ShapedRecipe>()
            .map { it.key }
    }
}
