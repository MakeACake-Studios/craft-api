# Craft-api

[English](README.md) | [Русский](README.ru.md) | [日本語](README.jp.md)

Library for creating custom items with custom actions, cooldowns, and recipes for PaperMC servers.

---

### Requirements
* **Paper API** 1.21.11 or higher.
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
    implementation("com.github.MakeACake-Studios:craft-api:1.0.2")
}
```

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

### Features
* **Permissions**: `yourplugin.item.item_id`
* **Cooldowns**: Automated cooldown management via `CooldownManager`.
* **ActionTypes**: `RIGHT_CLICK`, `LEFT_CLICK`, `SHIFT_RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, `CRAFT`, `BLOCK_PLACE`.