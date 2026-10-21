# Craft-api

[English](README.md) | [Русский](README.ru.md) | [日本語](README.jp.md)

PaperMCサーバー向けに、カスタムアクション、クールダウン、レシピを備えたカスタムアイテムを作成するためのライブラリです。

---

### 要件
* **Paper** 1.21.11 から 26.3
* **Java 25 以上**（このライブラリを使用するサーバー）
* **Kotlin 2.4.10**

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
    implementation("com.github.MakeACake-Studios:craft-api:1.1.2")
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
  `cooldownSeconds` を設定しない場合、クールダウンはありません。
* **アクションタイプ (ActionTypes)**: `RIGHT_CLICK`, `LEFT_CLICK`, `SHIFT_RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, `CRAFT`, `BLOCK_PLACE`

同じアイテムに複数のレシピを登録する場合は、各 `RecipeBuilder` の第2引数に異なる `NamespacedKey` を指定してください。
プラグインの有効化時に `CraftAPI.init(this)` を呼び出すと、無効化時に API の登録が解除されます。

このライブラリは Java 25 のバイトコードでビルドされ、デフォルトでは Paper 1.21.11 API を使用します。Paper 26.3 では `BrewingRecipe`、それ以前では `PotionMix` を使用します。新しい API の確認には `./gradlew build -PpaperApiVersion=26.3.build.+` を実行してください。CI は 1.21.11、26.1.1、26.1.2、26.2、26.3 を確認します。Paper 1.21.11 の推奨 Java は 21 ですが、このライブラリを使用するサーバーには Java 25 が必要です。同じプラグイン JAR を全バージョンで読み込むには、`plugin.yml` に `api-version: '1.21.11'` を指定してください。ライブラリと互換性のある Kotlin ランタイムをプラグイン JAR に同梱するか、依存関係ローダーを設定してください。

サーバーでの確認には、Paper 1.21.11 で [テストプラグイン](test-plugin/README.md) を `./gradlew :test-plugin:runServer` で起動します。Paper 26.3 では `./gradlew :test-plugin:runServer -PpaperServerVersion=26.3` を使用してください。
