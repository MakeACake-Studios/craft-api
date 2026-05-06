# Craft-api

[English](README.md) | [Русский](README.ru.md) | [日本語](README.jp.md)

PaperMCサーバー向けに、カスタムアクション、クールダウン、レシピを備えたカスタムアイテムを作成するためのライブラリです。

---

### 要件
* **Paper API** 1.21.11 以上
* **Java 21** 以上
* **Kotlin 1.9.0+**

### インストール

#### Gradle (Kotlin DSL)

1. `build.gradle.kts` に JitPack リポジトリを追加します：
```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```
2. 依存関係を追加します：
```kotlin
dependencies {
    implementation("com.github.MakeACake-Studios:craft-api:1.0.2")
}
```

### 始め方

#### 1. 初期化
プラグインのメインクラスでAPIを初期化します。これにより、すべてのイベントリスナーが自動的に登録されます。

**`MyPlugin.kt`**
```kotlin
class MyPlugin : JavaPlugin() {
    override fun onEnable() {
        CraftAPI.init(this)
    }
}
```

#### 2. カスタムアイテムの作成
`CustomItemBuilder` を使用して、ロジックを持つアイテムを作成します。

**`ExampleItem.kt`**
```kotlin
val magicStick = CustomItemBuilder("magic_stick")
    .item(ItemStack(Material.STICK))
    .name(Component.text("魔法のステッキ").color(NamedTextColor.GOLD))
    .lore(Component.text("クリックすると火花を放ちます！"))
    .actions(ActionType.RIGHT_CLICK, cooldownSeconds = 5) { player, item, event ->
        player.sendMessage("魔法を使いました！")
        player.world.spawnParticle(Particle.FIREWORKS_SPARK, player.location.add(0.0, 1.0, 0.0), 10)
    }
    .build()

ItemRegistry.register(magicStick)
```

#### 3. レシピの作成
`RecipeBuilder` を使用して、アイテムのレシピを作成できます。

**`ExampleRecipe.kt`**
```kotlin
RecipeBuilder(magicStick)
    .shape(" G ", " S ", " S ")
    .ingredient('G', Material.GOLD_INGOT)
    .ingredient('S', Material.STICK)
    .onCraft { player, itemStack, event ->
        player.sendMessage("伝説のアーティファクトを作成しました！")
    }
    .build()
```

### 特徴
* **権限 (Permissions)**: `yourplugin.item.item_id`
* **クールダウン (Cooldowns)**: `CooldownManager` による自動クールダウン管理。
* **アクションタイプ (ActionTypes)**: `RIGHT_CLICK`, `LEFT_CLICK`, `SHIFT_RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, `CRAFT`, `BLOCK_PLACE`
```