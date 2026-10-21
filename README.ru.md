# Craft-api

[English](README.md) | [Русский](README.ru.md) | [日本語](README.jp.md)

Библиотека для создания кастомных предметов с кастомными действиями, перезарядками и рецептами для серверов PaperMC.

---

### Требования
* **Paper** от 1.21.11 до 26.3.
* **Java 25 или новее** на сервере, где используется библиотека.
* **Kotlin 2.4.10**.

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
    implementation("com.github.MakeACake-Studios:craft-api:1.1.2")
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
  Без `cooldownSeconds` перезарядка не применяется.
* **Типы действий**: `RIGHT_CLICK`, `LEFT_CLICK`, `SHIFT_RIGHT_CLICK`, `SHIFT_LEFT_CLICK`, `CRAFT`, `BLOCK_PLACE`.

Для нескольких рецептов одного предмета передайте каждому `RecipeBuilder` отдельный `NamespacedKey` вторым аргументом.
Вызовите `CraftAPI.init(this)` при включении плагина; при его отключении API удалит свои регистрации.

Библиотека собирается в байткод Java 25 и по умолчанию использует API Paper 1.21.11. На Paper 26.3 рецепты варки регистрируются через `BrewingRecipe`, на более старых версиях — через `PotionMix`. Для проверки новой версии запустите `./gradlew build -PpaperApiVersion=26.3.build.+`; CI проверяет 1.21.11, 26.1.1, 26.1.2, 26.2 и 26.3. Для Paper 1.21.11 рекомендуется Java 21, но сервер с этой библиотекой должен работать на Java 25. Если один JAR плагина должен загружаться на всём диапазоне, укажите в его `plugin.yml` значение `api-version: '1.21.11'`. Включите библиотеку и совместимую среду Kotlin в JAR плагина либо настройте загрузчик зависимостей.

Для проверки на сервере запустите [тестовый плагин](test-plugin/README.md) на Paper 1.21.11 командой `./gradlew :test-plugin:runServer` или на Paper 26.3 командой `./gradlew :test-plugin:runServer -PpaperServerVersion=26.3`.
