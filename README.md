# Craft-api

[EN] Library for creating custom items with custom actions, cooldowns, and recipes for PaperMC servers.

[RU] Библиотека для создания кастомных предметов с кастомными действиями, перезарядками и рецептами для серверов PaperMC.

---

## English

### Requirements
* **Paper API** 1.21.1 or higher.
* **Java 21** or higher.
* **Kotlin 1.9.0+**.

### Installation

#### Gradle (Kotlin DSL)

1. Add the JitPack repository to your `build.gradle.kts`:
```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```
2. Add the dependency:
```kotlin
dependencies {
    implementation("com.github.YourUser:craft-api:Tag")
}
```

*Note: If the repository is private, you will need to add credentials with your JitPack token to the repository block.*

### Getting Started

#### 1. Initialization
In your main plugin class, initialize the API. This automatically registers all event listeners.

**`MyPlugin.kt`**
```kotlin
class MyPlugin : JavaPlugin() {
    override fun onEnable() {
        CraftAPI.init(this)
    }
}
```

#### 2. Creating a Custom Item
Use `CustomItemBuilder` to create an item with logic.

**`ExampleItem.kt`**
```kotlin
val magicStick = CustomItemBuilder("magic_stick")
    .item(ItemStack(Material.STICK))
    .name(Component.text("Magic Stick").color(NamedTextColor.GOLD))
    .lore(Component.text("Shoots sparks on click!"))
    .actions(ActionType.RIGHT_CLICK, cooldownSeconds = 5) { player, item, event ->
        player.sendMessage("You used magic!")
        player.world.spawnParticle(Particle.FIREWORKS_SPARK, player.location.add(0.0, 1.0, 0.0), 10)
    }
    .build()

ItemRegistry.register(magicStick)
```

#### 3. Creating a Recipe
You can create a recipe for your item using `RecipeBuilder`.

**`ExampleRecipe.kt`**
```kotlin
RecipeBuilder(magicStick)
    .shape(" G ", " S ", " S ")
    .ingredient('G', Material.GOLD_INGOT)
    .ingredient('S', Material.STICK)
    .onCraft { player, itemStack, event ->
        player.sendMessage("You crafted a legendary artifact!")
    }
    .build()
```

---

## Русский

### Требования
* **Paper API** 1.21.1 или выше.
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
    implementation("com.github.ВашНик:craft-api:Tag")
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

## Features / Особенности

*   **Permissions / Права**: `yourplugin.item.item_id`
*   **Cooldowns / Перезарядка**: Automated cooldown management via `CooldownManager`.
*   **ActionTypes / Типы действий**: `RIGHT_CLICK`, `LEFT_CLICK`, `SHIFT_RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, `CRAFT`, `BLOCK_PLACE`.
