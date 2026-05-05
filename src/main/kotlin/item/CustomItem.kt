package org.makeacake.craft.item

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.makeacake.craft.action.ItemAction

/**
 * Кастомный айтем внутри сервера
 *
 * Создаёт айтем доступный для создания игроками внутри сервера
 * и возможность задания ему особых свойств
 *
 * @property key ID предмета
 * @property item Используемый ванильный предмет для реализации нашего кастомного предмета
 * @property actions Список действий выполняемых при взаимодействии с предметом
 * @author nalart11
 * @since 0.7b
 * **/

class CustomItem(
    val key: ItemKey,
    val item: ItemStack,
    val actions: List<ItemAction>
) {

    fun matches(stack: ItemStack?): Boolean {
        if (stack == null || !stack.hasItemMeta()) return false
        val pdc = stack.itemMeta.persistentDataContainer
        return pdc.has(key.namespacedKey, PersistentDataType.BYTE)
    }

    /**
     * Создает копию предмета из шаблона.
     * @param amount Количество предметов
     * @param modifier Опциональная лямбда для переопределения названия, лора, текстуры головы и т.д.
     */
    fun createItemStack(amount: Int = 1, modifier: (ItemStack) -> Unit = {}): ItemStack {
        val stack = item.clone()
        stack.amount = amount
        stack.editMeta { meta ->
            meta.persistentDataContainer.set(key.namespacedKey, PersistentDataType.BYTE, 1)
        }

        modifier(stack)

        return stack
    }
}
