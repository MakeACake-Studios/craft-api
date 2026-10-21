`````# Craft API test plugin
```
Run a local Paper 1.21.11 server with Java 25 from the repository root:

```bash
./gradlew :test-plugin:runServer
```

To test Paper 26.3, use a separate server directory automatically selected by version:

```bash
./gradlew :test-plugin:runServer -PpaperServerVersion=26.3
```

The task builds and installs the test plugin JAR. Server files are kept in `test-plugin/build/run-server/<version>/`. On first launch, accept the Minecraft EULA in that directory's `eula.txt` and run the command again. Stop the server with `stop` in its console.

You can also build the JAR for a manually managed Paper server:

```bash
./gradlew :test-plugin:jar
```

Copy `test-plugin/build/libs/craft-api-test-plugin.jar` into the `plugins/` directory of a Paper 1.21.11 or 26.3 server running Java 25. The JAR includes Craft API and the Kotlin runtime. Start the server, join as an operator, and run `/craftapitest give`.

1. Run `/craftapitest reset`. Right click repeatedly with the named stick. Every click should print a message. Right click with the named blaze rod: its action should run only once every three seconds.
2. Put paper above a stick in a crafting grid, then paper above redstone. Both recipes should give the same named emerald, but print different craft callback messages.
3. Put the named water potion in one brewing slot and the other swiftness potion in another. Add blaze powder as fuel and redstone as the ingredient. The named potion should become `Craft API: brewed result`; the ordinary swiftness potion should become a long swiftness potion. The server log should contain `Custom brew callback ran.`
4. Run `/craftapitest status` to see action counts. Stop and start the server again, then repeat the command. Startup should not report duplicate recipe registrations.

The plugin's `api-version` is `1.21.11`, so the same JAR can be tested on both versions.
