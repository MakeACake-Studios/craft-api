
package org.makeacake.craft.testplugin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionType
import org.makeacake.craft.CraftAPI
import org.makeacake.craft.action.ActionType
import org.makeacake.craft.action.BrewAction
import org.makeacake.craft.item.CustomItem
import org.makeacake.craft.item.CustomItemBuilder
import org.makeacake.craft.item.ItemRegistry
import org.makeacake.craft.recipe.BrewRecipeBuilder
import org.makeacake.craft.recipe.RecipeBuilder
import org.makeacake.craft.recipe.RecipeRegistry

class CraftApiTestPlugin : JavaPlugin(), Listener {

    private lateinit var freeWand: CustomItem
    private lateinit var cooldownWand: CustomItem
    private lateinit var brewBase: CustomItem

    private lateinit var seaEssence: CustomItem
    private lateinit var milkMist: CustomItem
    private lateinit var creamyTide: CustomItem
    private lateinit var amberTide: CustomItem
    private lateinit var cloudEssence: CustomItem

    private var freeClicks = 0
    private var cooldownClicks = 0
    private var crafts = 0
    private var brews = 0

    private var brewPasses = 0
    private var brewFailures = 0

    private data class BrewCase(
        val name: String,
        val ingredient: Material,
        val base: ItemStack,
        val expectedId: String?
    )

    private data class PendingBrewCheck(
        val slot: Int,
        val inputId: String?,
        val expectedId: String?
    )

    override fun onEnable() {
        CraftAPI.init(this)
        server.pluginManager.registerEvents(this, this)

        registerWands()
        registerCrafting()
        registerOriginalBrewingTest()
        registerExperimentalBrewing()

        logger.info(
            "Craft API test plugin enabled. " +
                    "Use /craftapitest check or /craftapitest give brew."
        )
    }

    private fun registerWands() {
        freeWand = CustomItemBuilder("free_wand")
            .item(ItemStack(Material.STICK))
            .name(Component.text("Craft API: no cooldown"))
            .actions(ActionType.RIGHT_CLICK) { player, _, _ ->
                player.sendMessage(
                    "No cooldown: click #${++freeClicks}."
                )
            }
            .build()

        ItemRegistry.register(freeWand)

        cooldownWand = CustomItemBuilder("cooldown_wand")
            .item(ItemStack(Material.BLAZE_ROD))
            .name(Component.text("Craft API: 3 second cooldown"))
            .actions(
                ActionType.RIGHT_CLICK,
                cooldownSeconds = 3
            ) { player, _, _ ->
                player.sendMessage(
                    "Cooldown action #${++cooldownClicks} ran."
                )
            }
            .build()

        ItemRegistry.register(cooldownWand)
    }

    private fun registerCrafting() {
        val craftResult = CustomItemBuilder("craft_result")
            .item(ItemStack(Material.EMERALD))
            .name(Component.text("Craft API: crafted item"))
            .build()

        ItemRegistry.register(craftResult)

        RecipeBuilder(craftResult)
            .shape("P", "S")
            .ingredient('P', Material.PAPER)
            .ingredient('S', Material.STICK)
            .onCraft { player, _, _ ->
                player.sendMessage(
                    "Primary craft callback #${++crafts} ran."
                )
            }
            .build()

        RecipeBuilder(
            craftResult,
            NamespacedKey(this, "craft_result_alt")
        )
            .shape("P", "R")
            .ingredient('P', Material.PAPER)
            .ingredient('R', Material.REDSTONE)
            .onCraft { player, _, _ ->
                player.sendMessage(
                    "Alternate craft callback #${++crafts} ran."
                )
            }
            .build()
    }

    private fun registerOriginalBrewingTest() {
        brewBase = CustomItemBuilder("brew_base")
            .item(potion(PotionType.WATER))
            .name(Component.text("Craft API: brewing base"))
            .build()

        ItemRegistry.register(brewBase)

        val brewResult = CustomItemBuilder("brew_result")
            .item(potion(PotionType.SWIFTNESS))
            .name(Component.text("Craft API: brewed result"))
            .build()

        ItemRegistry.register(brewResult)

        BrewRecipeBuilder(brewResult)
            .ingredient(Material.REDSTONE)
            .base(brewBase)
            .onBrew(
                BrewAction { _, _, _ ->
                    logger.info(
                        "Custom brew callback #${++brews} ran."
                    )
                }
            )
            .build()
    }

    private fun registerExperimentalBrewing() {
        seaEssence = customPotion(
            "sea_essence",
            "Морская эссенция",
            Color.fromRGB(33, 153, 165)
        )

        milkMist = customPotion(
            "milk_mist",
            "Молочный туман",
            Color.fromRGB(228, 226, 207)
        )

        creamyTide = customPotion(
            "creamy_tide",
            "Сливочный прилив",
            Color.fromRGB(147, 202, 183)
        )

        amberTide = customPotion(
            "amber_tide",
            "Янтарный прилив",
            Color.fromRGB(218, 158, 63)
        )

        cloudEssence = customPotion(
            "cloud_essence",
            "Облачная эссенция",
            Color.fromRGB(187, 218, 249)
        )

        // Standard potion -> custom potion.
        BrewRecipeBuilder(seaEssence)
            .base(Material.POTION)
            .ingredient(Material.KELP)
            .build()

        // Broad potion recipe registered before a more specific recipe.
        // This tests whether custom potions are excluded correctly.
        BrewRecipeBuilder(milkMist)
            .base(Material.POTION)
            .ingredient(Material.MILK_BUCKET)
            .build()

        // Only Sea Essence + Milk should produce Creamy Tide.
        BrewRecipeBuilder(creamyTide)
            .base(seaEssence)
            .ingredient(Material.MILK_BUCKET)
            .build()

        BrewRecipeBuilder(amberTide)
            .base(seaEssence)
            .ingredient(Material.HONEY_BOTTLE)
            .build()

        BrewRecipeBuilder(cloudEssence)
            .base(Material.POTION)
            .ingredient(Material.FEATHER)
            .build()

        logger.info("Experimental brewing recipes registered.")
    }

    private fun customPotion(
        id: String,
        name: String,
        color: Color
    ): CustomItem {
        val item = CustomItemBuilder(id)
            .item(potion(PotionType.WATER, color))
            .name(Component.text(name))
            .build()

        ItemRegistry.register(item)
        return item
    }

    // --------------------------------------------------
    // Recipe registry checks
    // --------------------------------------------------

    private fun checkRecipes(sender: CommandSender) {
        val unknownPotion = potion(PotionType.WATER).apply {
            editMeta {
                it.persistentDataContainer.set(
                    NamespacedKey(
                        this@CraftApiTestPlugin,
                        "unregistered_test_tag"
                    ),
                    PersistentDataType.BYTE,
                    1.toByte()
                )
            }
        }

        val cases = listOf(
            BrewCase(
                "Water + Kelp",
                Material.KELP,
                potion(PotionType.WATER),
                "sea_essence"
            ),
            BrewCase(
                "Sea Essence + Kelp (must reject)",
                Material.KELP,
                seaEssence.createItemStack(),
                null
            ),
            BrewCase(
                "Cloud Essence + Kelp (must reject)",
                Material.KELP,
                cloudEssence.createItemStack(),
                null
            ),
            BrewCase(
                "Water + Milk",
                Material.MILK_BUCKET,
                potion(PotionType.WATER),
                "milk_mist"
            ),
            BrewCase(
                "Sea Essence + Milk",
                Material.MILK_BUCKET,
                seaEssence.createItemStack(),
                "creamy_tide"
            ),
            BrewCase(
                "Cloud Essence + Milk (must reject)",
                Material.MILK_BUCKET,
                cloudEssence.createItemStack(),
                null
            ),
            BrewCase(
                "Sea Essence + Honey",
                Material.HONEY_BOTTLE,
                seaEssence.createItemStack(),
                "amber_tide"
            ),
            BrewCase(
                "Water + Honey (must reject)",
                Material.HONEY_BOTTLE,
                potion(PotionType.WATER),
                null
            ),
            BrewCase(
                "Cloud Essence + Honey (must reject)",
                Material.HONEY_BOTTLE,
                cloudEssence.createItemStack(),
                null
            ),
            BrewCase(
                "Water + Feather",
                Material.FEATHER,
                potion(PotionType.WATER),
                "cloud_essence"
            ),
            BrewCase(
                "Sea Essence + Feather (must reject)",
                Material.FEATHER,
                seaEssence.createItemStack(),
                null
            ),
            BrewCase(
                "Test Custom Base + Redstone",
                Material.REDSTONE,
                brewBase.createItemStack(),
                "brew_result"
            ),
            BrewCase(
                "Vanilla Swiftness + Redstone",
                Material.REDSTONE,
                potion(PotionType.SWIFTNESS),
                null
            ),
            BrewCase(
                "Non-potion + Kelp (must reject)",
                Material.KELP,
                ItemStack(Material.HONEY_BOTTLE),
                null
            ),
            BrewCase(
                "Unregistered PDC + Kelp",
                Material.KELP,
                unknownPotion,
                "sea_essence"
            )
        )

        sender.sendMessage("=== CraftAPI Brewing Registry Checks ===")

        var passed = 0
        var failed = 0

        for (case in cases) {
            val actual = RecipeRegistry.matchBrew(
                ItemStack(case.ingredient),
                case.base
            )?.item?.key?.id

            if (actual == case.expectedId) {
                passed++
                sender.sendMessage(
                    "[PASS] ${case.name}: ${actual ?: "no recipe"}"
                )
            } else {
                failed++
                sender.sendMessage(
                    "[FAIL] ${case.name}: " +
                            "expected=${case.expectedId ?: "none"}, " +
                            "actual=${actual ?: "none"}"
                )
            }
        }

        sender.sendMessage(
            "Registry checks: $passed passed, $failed failed."
        )

        logger.info(
            "Registry checks completed: " +
                    "$passed passed, $failed failed."
        )
    }

    // --------------------------------------------------
    // Real brewing stand checks
    // --------------------------------------------------

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun onBrewObserved(event: BrewEvent) {
        val inventory = event.contents
        val ingredient = inventory.ingredient ?: return

        val checks = (0..2).mapNotNull { slot ->
            val base = inventory.getItem(slot) ?: return@mapNotNull null

            val inputId = ItemRegistry
                .byItemStack(base)?.key?.id

            val expectedId = RecipeRegistry
                .matchBrew(ingredient, base)
                ?.item?.key?.id

            // For ordinary vanilla-only brewing there is no
            // custom expectation. Let Minecraft handle it.
            if (inputId == null && expectedId == null) {
                return@mapNotNull null
            }

            PendingBrewCheck(
                slot = slot,
                inputId = inputId,
                expectedId = expectedId
            )
        }

        if (checks.isEmpty()) return

        // Wait one tick: Paper applies the brewing results
        // after BrewEvent has been processed.
        Bukkit.getScheduler().runTask(
            this,
            Runnable {
                for (check in checks) {
                    val result = inventory.getItem(check.slot)

                    val actualId = ItemRegistry
                        .byItemStack(result)?.key?.id

                    val expectedId =
                        check.expectedId ?: check.inputId

                    val success = actualId == expectedId

                    val message =
                        "BREW ${if (success) "PASS" else "FAIL"} " +
                                "slot=${check.slot}, " +
                                "input=${check.inputId ?: "vanilla"}, " +
                                "expected=${expectedId ?: "none"}, " +
                                "actual=${actualId ?: "vanilla/unknown"}"

                    if (success) {
                        brewPasses++
                        logger.info(message)
                    } else {
                        brewFailures++
                        logger.warning(message)
                    }
                }
            }
        )
    }

    // --------------------------------------------------
    // Commands
    // --------------------------------------------------

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        when (args.firstOrNull()?.lowercase()) {
            "check" -> {
                checkRecipes(sender)
                return true
            }

            "status" -> {
                sender.sendMessage(
                    "CraftAPI counters: " +
                            "free=$freeClicks, " +
                            "cooldown=$cooldownClicks, " +
                            "crafts=$crafts, " +
                            "brewCallbacks=$brews, " +
                            "brewPass=$brewPasses, " +
                            "brewFail=$brewFailures"
                )
                return true
            }

            "reset" -> {
                freeClicks = 0
                cooldownClicks = 0
                crafts = 0
                brews = 0
                brewPasses = 0
                brewFailures = 0

                sender.sendMessage("CraftAPI counters reset.")
                return true
            }

            "give" -> {
                val player = sender as? Player ?: run {
                    sender.sendMessage("Run this command as a player.")
                    return true
                }

                val group = args.getOrNull(1)?.lowercase() ?: "all"

                when (group) {
                    "basic" -> giveBasicItems(player)
                    "brew" -> giveBrewingItems(player)
                    "all" -> {
                        giveBasicItems(player)
                        giveBrewingItems(player)
                    }
                    else -> {
                        sender.sendMessage(
                            "Usage: /craftapitest give [all|basic|brew]"
                        )
                    }
                }
                return true
            }

            else -> {
                sender.sendMessage(
                    "Usage: /craftapitest " +
                            "<give|check|status|reset>"
                )
                return true
            }
        }
    }

    private fun giveBasicItems(player: Player) {
        give(
            player,
            listOf(
                freeWand.createItemStack(),
                cooldownWand.createItemStack(),
                ItemStack(Material.PAPER, 4),
                ItemStack(Material.STICK, 4),
                ItemStack(Material.REDSTONE, 4),
                ItemStack(Material.EMERALD)
            )
        )

        player.sendMessage("Basic test items given.")
    }

    private fun giveBrewingItems(player: Player) {
        give(
            player,
            listOf(
                potion(PotionType.WATER),
                potion(PotionType.WATER),
                potion(PotionType.WATER),
                potion(PotionType.SWIFTNESS),
                brewBase.createItemStack(),
                seaEssence.createItemStack(),
                seaEssence.createItemStack(),
                cloudEssence.createItemStack(),
                milkMist.createItemStack(),
                creamyTide.createItemStack(),
                amberTide.createItemStack(),
                ItemStack(Material.KELP, 8),
                ItemStack(Material.MILK_BUCKET, 4),
                ItemStack(Material.HONEY_BOTTLE, 4),
                ItemStack(Material.FEATHER, 8),
                ItemStack(Material.REDSTONE, 8),
                ItemStack(Material.BLAZE_POWDER, 16)
            )
        )

        player.sendMessage("Experimental brewing items given.")
        player.sendMessage(
            "Try Water + Kelp, Sea Essence + Milk, " +
                    "and Cloud Essence + Milk."
        )
        player.sendMessage(
            "Use /craftapitest check for registry diagnostics."
        )
    }

    private fun give(player: Player, items: List<ItemStack>) {
        for (stack in items) {
            player.inventory.addItem(stack).values.forEach { leftover ->
                player.world.dropItemNaturally(
                    player.location,
                    leftover
                )
            }
        }
    }

    private fun potion(
        type: PotionType,
        color: Color? = null
    ): ItemStack = ItemStack(Material.POTION).apply {
        val meta = itemMeta as PotionMeta
        meta.basePotionType = type

        if (color != null) {
            meta.color = color
        }

        itemMeta = meta
    }
}
