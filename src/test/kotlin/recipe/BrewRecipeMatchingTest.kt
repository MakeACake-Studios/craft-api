
package org.makeacake.craft.recipe

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.makeacake.craft.item.CustomItem
import org.makeacake.craft.item.ItemKey
import org.makeacake.craft.item.ItemRegistry
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import recipe.BrewBaseMatcher
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BrewRecipeMatchingTest {

    private lateinit var beer: CustomItem
    private lateinit var matcha: CustomItem
    private lateinit var matchaLatte: CustomItem
    private lateinit var mulledWine: CustomItem
    private lateinit var hotMulledWine: CustomItem
    private lateinit var kvas: CustomItem
    private lateinit var koumis: CustomItem

    @BeforeTest
    fun setUp() {
        RecipeRegistry.clear()
        ItemRegistry.clear()

        beer = customItem("beer")
        matcha = customItem("matcha")
        matchaLatte = customItem("matchaLatte")
        mulledWine = customItem("mulledWine")
        hotMulledWine = customItem("hotMulledWine")
        kvas = customItem("kvas")
        koumis = customItem("koumis")

        // Water + bush -> beer
        registerRecipe(
            result = beer,
            ingredient = Material.BUSH,
            baseMaterial = Material.POTION
        )

        // Water + milk -> koumis
        registerRecipe(
            result = koumis,
            ingredient = Material.MILK_BUCKET,
            baseMaterial = Material.POTION
        )

        // Matcha + milk -> matcha latte
        registerRecipe(
            result = matchaLatte,
            ingredient = Material.MILK_BUCKET,
            baseCustomItem = matcha
        )

        // Mulled wine + glowstone -> hot mulled wine
        registerRecipe(
            result = hotMulledWine,
            ingredient = Material.GLOWSTONE_DUST,
            baseCustomItem = mulledWine
        )
    }

    @AfterTest
    fun tearDown() {
        RecipeRegistry.clear()
        ItemRegistry.clear()
    }

    @Test
    fun `water and bush produce beer`() {
        val result = brew(
            ingredient = Material.BUSH,
            base = itemStack(Material.POTION)
        )

        assertSame(beer, result)
    }

    @Test
    fun `matcha and bush do not produce beer`() {
        val result = brew(
            ingredient = Material.BUSH,
            base = itemStack(Material.POTION, matcha)
        )

        assertNull(result)
    }

    @Test
    fun `mulled wine and bush do not produce beer`() {
        val result = brew(
            ingredient = Material.BUSH,
            base = itemStack(Material.POTION, mulledWine)
        )

        assertNull(result)
    }

    @Test
    fun `kvas and bush do not produce beer`() {
        val result = brew(
            ingredient = Material.BUSH,
            base = itemStack(Material.POTION, kvas)
        )

        assertNull(result)
    }

    @Test
    fun `matcha and milk produce matcha latte`() {
        val result = brew(
            ingredient = Material.MILK_BUCKET,
            base = itemStack(Material.POTION, matcha)
        )

        // Must not match the earlier registered koumis recipe.
        assertSame(matchaLatte, result)
    }

    @Test
    fun `water and milk produce koumis`() {
        val result = brew(
            ingredient = Material.MILK_BUCKET,
            base = itemStack(Material.POTION)
        )

        assertSame(koumis, result)
    }

    @Test
    fun `mulled wine and glowstone produce hot mulled wine`() {
        val result = brew(
            ingredient = Material.GLOWSTONE_DUST,
            base = itemStack(Material.POTION, mulledWine)
        )

        assertSame(hotMulledWine, result)
    }

    @Test
    fun `matcha and glowstone do not produce hot mulled wine`() {
        val result = brew(
            ingredient = Material.GLOWSTONE_DUST,
            base = itemStack(Material.POTION, matcha)
        )

        assertNull(result)
    }


    @Test
    fun `potion matcher rejects registered custom potions`() {
        val water = itemStack(Material.POTION)
        val matchaStack = itemStack(Material.POTION, matcha)
        val wineStack = itemStack(Material.POTION, mulledWine)
        val kvasStack = itemStack(Material.POTION, kvas)

        assertTrue(
            BrewBaseMatcher.matches(Material.POTION, water)
        )

        assertFalse(
            BrewBaseMatcher.matches(Material.POTION, matchaStack)
        )

        assertFalse(
            BrewBaseMatcher.matches(Material.POTION, wineStack)
        )

        assertFalse(
            BrewBaseMatcher.matches(Material.POTION, kvasStack)
        )
    }

    @Test
    fun `non potion materials still match normally`() {
        assertTrue(
            BrewBaseMatcher.matches(
                Material.HONEY_BOTTLE,
                itemStack(Material.HONEY_BOTTLE)
            )
        )

        assertFalse(
            BrewBaseMatcher.matches(
                Material.HONEY_BOTTLE,
                itemStack(Material.POTION)
            )
        )
    }

    @Test
    fun `missing ingredients do not match recipes`() {
        val water = itemStack(Material.POTION)
        val bush = itemStack(Material.BUSH)

        assertNull(RecipeRegistry.matchBrew(null, water))
        assertNull(RecipeRegistry.matchBrew(bush, null))
        assertNull(RecipeRegistry.matchBrew(null, null))
    }

    // ---------- Test helpers ----------

    private fun brew(
        ingredient: Material,
        base: ItemStack
    ): CustomItem? {
        return RecipeRegistry.matchBrew(
            itemStack(ingredient),
            base
        )?.item
    }

    private fun registerRecipe(
        result: CustomItem,
        ingredient: Material,
        baseMaterial: Material? = null,
        baseCustomItem: CustomItem? = null
    ) {
        RecipeRegistry.register(
            CustomBrewRecipe(
                item = result,
                ingredientMaterial = ingredient,
                ingredientCustomItem = null,
                baseMaterial = baseMaterial,
                baseCustomItem = baseCustomItem,
                brewActions = emptyList()
            )
        )
    }

    private fun customItem(id: String): CustomItem {
        // Mock ItemKey to avoid requiring CraftAPI.init().
        val key = mock(ItemKey::class.java)

        `when`(key.id).thenReturn(id)
        `when`(key.namespacedKey).thenReturn(
            NamespacedKey("craftapi", id.lowercase())
        )

        val item = CustomItem(
            key = key,
            item = mock(ItemStack::class.java),
            actions = emptyList()
        )

        ItemRegistry.register(item)
        return item
    }

    private fun itemStack(
        material: Material,
        customItem: CustomItem? = null
    ): ItemStack {
        val stack = mock(ItemStack::class.java)
        val meta = mock(ItemMeta::class.java)
        val pdc = mock(PersistentDataContainer::class.java)

        `when`(stack.type).thenReturn(material)
        `when`(stack.hasItemMeta()).thenReturn(true)
        `when`(stack.itemMeta).thenReturn(meta)

        // ItemStack returns a read-only PDC view.
        `when`(stack.persistentDataContainer).thenReturn(pdc)
        `when`(meta.persistentDataContainer).thenReturn(pdc)

        if (customItem != null) {
            `when`(
                pdc.has(
                    customItem.key.namespacedKey,
                    PersistentDataType.BYTE
                )
            ).thenReturn(true)
        }

        return stack
    }
}
