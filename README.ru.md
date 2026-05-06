# Craft-api

[English](README.md) | [Русский](README.ru.md) | [日本語](README.jp.md)

Библиотека для создания кастомных предметов с кастомными действиями, перезарядками и рецептами для серверов PaperMC.

---

### Требования
* **Paper API** 1.21.11 или выше.
* **Java 21** или выше.
* **Kotlin 1.9.0+**.

### Установка

#### Gradle (Kotlin DSL)

1. Добавьте репозиторий JitPack в ваш `build.gradle.kts`:
```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```
2. Добавьте зависимость:
```kotlin
dependencies {
    implementation("com.github.MakeACake-Studios:craft-api:1.0.2")
}
```

*Примечание: Если репозиторий приватный, вам потребуется добавить `credentials` с вашим JitPack токеном в блок репозитория.*

### Начало работы

#### 1. Инициализация
В главном классе вашего плагина необходимо инициализировать API. Это автоматически зарегистрирует все слушатели событий.

**`MyPlugin.kt`**
```kotlin
class MyPlugin : JavaPlugin() {
    override fun onEnable() {
        CraftAPI.init(this)
    }
}
```

#### 2. Создание кастомного предмета
Используйте `CustomItemBuilder` для быстрого создания предмета с логикой.

**`ExampleItem.kt`**
```kotlin
val magicStick = CustomItemBuilder("magic_stick")
    .item(ItemStack(Material.STICK))
    .name(Component.text("Волшебная палочка").color(NamedTextColor.GOLD))
    .lore(Component.text("Стреляет искрами при клике!"))
    .actions(ActionType.RIGHT_CLICK, cooldownSeconds = 5) { player, item, event ->
        player.sendMessage("Вы использовали магию!")
        player.world.spawnParticle(Particle.FIREWORKS_SPARK, player.location.add(0.0, 1.0, 0.0), 10)
    }
    .build()

ItemRegistry.register(magicStick)
```

#### 3. Создание рецепта
Вы можете создать рецепт для вашего предмета с помощью `RecipeBuilder`.

**`ExampleRecipe.kt`**
```kotlin
RecipeBuilder(magicStick)
    .shape(" G ", " S ", " S ")
    .ingredient('G', Material.GOLD_INGOT)
    .ingredient('S', Material.STICK)
    .onCraft { player, itemStack, event ->
        player.sendMessage("Вы создали легендарный артефакт!")
    }
    .build()
```

### Особенности
* **Права (Permissions)**: `yourplugin.item.item_id`
* **Перезарядка**: Автоматическое управление кулдаунами через `CooldownManager`.
* **Типы действий**: `RIGHT_CLICK`, `LEFT_CLICK`, `SHIFT_RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, `CRAFT`, `BLOCK_PLACE`.