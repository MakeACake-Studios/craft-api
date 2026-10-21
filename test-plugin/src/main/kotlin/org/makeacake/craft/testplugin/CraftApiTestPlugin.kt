package org.makeacake.craft.testplugin

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
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

class CraftApiTestPlugin : JavaPlugin() {
    private lateinit var freeWand: CustomItem
    private lateinit var cooldownWand: CustomItem
    private lateinit var brewBase: CustomItem
    private var freeClicks = 0
    private var cooldownClicks = 0
    private var crafts = 0
    private var brews = 0

    override fun onEnable() {
        CraftAPI.init(this)

        freeWand = CustomItemBuilder("free_wand")
            .item(ItemStack(Material.STICK))
            .name(Component.text("Craft API: no cooldown"))
            .actions(ActionType.RIGHT_CLICK) { player, _, _ ->
                player.sendMessage("No cooldown: click #${++freeClicks}.")
            }
            .build()
        ItemRegistry.register(freeWand)

        cooldownWand = CustomItemBuilder("cooldown_wand")
            .item(ItemStack(Material.BLAZE_ROD))
            .name(Component.text("Craft API: 3 second cooldown"))
            .actions(ActionType.RIGHT_CLICK, cooldownSeconds = 3) { player, _, _ ->
                player.sendMessage("Cooldown action #${++cooldownClicks} ran. Try again in 3 seconds.")
            }
            .build()
        ItemRegistry.register(cooldownWand)

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
                player.sendMessage("Primary craft callback #${++crafts} ran.")
            }
            .build()
        RecipeBuilder(craftResult, NamespacedKey(this, "craft_result_alt"))
            .shape("P", "R")
            .ingredient('P', Material.PAPER)
            .ingredient('R', Material.REDSTONE)
            .onCraft { player, _, _ ->
                player.sendMessage("Alternate craft callback #${++crafts} ran.")
            }
            .build()

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
            .onBrew(BrewAction { _, _, _ -> logger.info("Custom brew callback #${++brews} ran.") })
            .build()

        logger.info("Craft API test plugin enabled. Use /craftapitest give.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (args.singleOrNull()?.lowercase()) {
            "status" -> {
                sender.sendMessage("Craft API counters: free=$freeClicks, cooldown=$cooldownClicks, crafts=$crafts, brews=$brews")
                return true
            }
            "reset" -> {
                freeClicks = 0
                cooldownClicks = 0
                crafts = 0
                brews = 0
                sender.sendMessage("Craft API counters reset.")
                return true
            }
            "give" -> Unit
            else -> {
                sender.sendMessage("Usage: /craftapitest <give|status|reset>")
                return true
            }
        }
        val player = sender as? Player ?: run {
            sender.sendMessage("Run this command as a player.")
            return true
        }

        val items = listOf(
            freeWand.createItemStack(), cooldownWand.createItemStack(),
            ItemStack(Material.PAPER, 4), ItemStack(Material.STICK, 4),
            brewBase.createItemStack(), potion(PotionType.SWIFTNESS),
            ItemStack(Material.REDSTONE, 4), ItemStack(Material.BLAZE_POWDER, 4)
        )
        items.forEach { stack ->
            player.inventory.addItem(stack).values.forEach { leftover ->
                player.world.dropItemNaturally(player.location, leftover)
            }
        }
        player.sendMessage("Items given. Test both wands, craft PAPER over STICK or REDSTONE, then brew with REDSTONE.")
        return true
    }

    private fun potion(type: PotionType): ItemStack = ItemStack(Material.POTION).apply {
        val meta = itemMeta as PotionMeta
        meta.basePotionType = type
        itemMeta = meta
    }
}
