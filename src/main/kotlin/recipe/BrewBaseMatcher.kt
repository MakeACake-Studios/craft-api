
package recipe

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.makeacake.craft.item.ItemRegistry

internal object BrewBaseMatcher {

    fun matches(material: Material, stack: ItemStack): Boolean {
        if (stack.type != material) return false

        return material != Material.POTION ||
            ItemRegistry.byItemStack(stack) == null
    }
}
