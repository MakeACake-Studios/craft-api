# Craft-api

[English](README.md) | [Русский](README.ru.md) | [日本語](README.jp.md)

Library for creating custom items with custom actions, cooldowns, and recipes for PaperMC servers.

---

### Requirements
* **Paper** 1.21.11 through 26.3.
* **Java 25 or newer** for the server running this library.
* **Kotlin 2.4.10**.

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
    implementation("com.github.MakeACake-Studios:craft-api:1.1.2")
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
  Actions have no cooldown unless `cooldownSeconds` is set.
* **ActionTypes**: `RIGHT_CLICK`, `LEFT_CLICK`, `SHIFT_RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, `CRAFT`, `BLOCK_PLACE`.

To register several shaped recipes for one item, pass a different `NamespacedKey` as the second argument to each `RecipeBuilder`.
Call `CraftAPI.init(this)` once on enable; the API releases its registrations when the plugin is disabled.

The library targets Java 25 bytecode and compiles against Paper 1.21.11 by default. On Paper 26.3 it registers brewing recipes through `BrewingRecipe`; older versions use `PotionMix`. To check a newer API locally, run `./gradlew build -PpaperApiVersion=26.3.build.+`; CI checks 1.21.11, 26.1.1, 26.1.2, 26.2, and 26.3. Paper 1.21.11 recommends Java 21, so a server using this library must instead run Java 25. Set your consuming plugin's `api-version` to `1.21.11` if the same plugin JAR must load on the entire range. Bundle this library and a compatible Kotlin runtime in the consuming plugin, or configure a plugin dependency loader.

For hands-on server checks, run the bundled [test plugin](test-plugin/README.md) on Paper 1.21.11 with `./gradlew :test-plugin:runServer`, or on Paper 26.3 with `./gradlew :test-plugin:runServer -PpaperServerVersion=26.3`.
