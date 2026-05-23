# AGENTS.md

MMORealms `auction-house-module` — a Minecraft 1.21.1 server-side module loaded by the MMORealms `loader`. Not a standalone app. Java source 21, Gradle toolchain JDK 25, Kotlin DSL.

## Submodule

`buildSrc/` is a git submodule (`https://git.mmorealms.gg/mmorealms/build-src`). After cloning the repo:

```
git submodule update --init --recursive
```

Without this Gradle can't even configure — every plugin id (`mmorealms_base`, `common`, `architectury_common`, `architectury_fabric`, `architectury_neoforge`, `velocity`) lives there.

## Gradle layout

Subprojects (declared in `settings.gradle.kts`, paths are non-default):

- `:common` → `src/common` — platform-agnostic, no Minecraft deps.
- `:backend-common` → `src/backend/common` — Architectury common (Fabric+NeoForge).
- `:backend-fabric` → `src/backend/fabric` — Loom/Fabric platform jar.
- `:backend-neoforge` → `src/backend/neoforge` — Loom/NeoForge platform jar.
- `:velocity` → `src/velocity` — Velocity proxy plugin side.

Each subproject's `build.gradle.kts` is one line applying a precompiled plugin; real build logic is in `buildSrc/src/main/kotlin/*.gradle.kts`.

## Build commands

```
./gradlew build
```

The root `build` task is finalized by `copySubprojectJars`, which wipes `build/libs/` and copies every subproject's `jar`/`shadowJar`/`remapJar` into:

- `build/libs/`            — release jars (Velocity shadow, Fabric/NeoForge remapped)
- `build/libs/dev-jars/`   — `-dev` classifier jars (do not ship)
- `build/libs/common-jars/`— `-common` classifier jars (do not ship)

There are no tests in the tree. JUnit 5 is wired in `mmorealms_base.gradle.kts`, so `./gradlew test` is a no-op today; if you add tests, place them under any subproject's `src/test/java`.

Useful one-offs:

- `./gradlew :backend-fabric:remapJar` — build just the Fabric jar.
- `./gradlew :velocity:shadowJar`      — build the proxy plugin jar.
- `./gradlew printDependencyBytecode`  — flags any jar with class major > 65 (Java 21).

## Required Gradle properties / env

Dependency resolution depends on the MMORealms private Maven, configured in `mmorealms_base.gradle.kts` and `buildSrc/build.gradle.kts`. Provide either:

- `gg.mmorealms.username` / `gg.mmorealms.password` / `gg.mmorealms.url` (direct), or
- `gg.mmorealms.proxy=true` with `gg.mmorealms.proxy.url.private`, `gg.mmorealms.proxy.url.public`, `gg.mmorealms.proxy.username`, `gg.mmorealms.proxy.password`.

Each property is also read from the env var with `.` → `_` and uppercase (`GG_MMOREALMS_USERNAME`, etc.). Without these, a fresh clone fails at configuration time on private artifacts. Put them in `~/.gradle/gradle.properties` (do not commit).

Other knobs (`buildSrc/.../utils/Statics.kt`):

- `gg.mmorealms.publish` = `local`|`remote` — selects publish destination.
- `gg.mmorealms.publish.version` — overrides the build's version (default `0.0.0-local`).
- `gg.mmorealms.warnings_as_errors[_override]` = `true` — adds `-Werror` to javac.

## Version handling

- `version.txt` is **legacy / deprecated** (`Utils.readVersion` / `writeVersion` are `@Deprecated`). CI ignores it (`paths-ignore` in `.github/workflows/build_and_publish.yml`). Do not hand-edit and do not rely on it.
- Real release version comes from CI via the MMO-REALMS/control-plane orchestrator, which sets `gg.mmorealms.publish.version` and pushes a `Build Bot` commit. Locally `Utils.getVersion()` returns `0.0.0-local` unless that property is supplied.

## Generated sources — do not edit

`mmorealms_base.gradle.kts` registers `generateBuildConstants` (runs before `compileJava`). It writes:

```
<subproject>/build/generated/sources/buildConstants/
  gg/mmorealms/module/auction_house/AuctionHouseModuleBuildConstants.java
```

containing `ID`, `VERSION`, `DEPENDENCIES`. The module entrypoints (e.g. `AuctionHouseCommonModule`) reference this class via `@Module(...)` — if it looks "missing" in your IDE, run a Gradle sync/build first.

## Module dependency list

`gradle.properties` → `module_dependencies=core,pokemon,economy` drives `compileOnly`/`modCompileOnlyApi` cross-module deps in `common.gradle.kts`, `architectury_*.gradle.kts`, and `velocity.gradle.kts`. To add another MMORealms module dep:

1. Add its id to `module_dependencies` (comma-separated, ids match `buildSrc/.../utils/InternalLibs.kt` entries — e.g. `core`, `pokemon`, `auction-house`).
2. The constant `InternalLibs.<name>` declares the version. That list is maintained in the `build-src` submodule (and auto-bumped by GitHub workflow `update-internal-libs-event`); do not edit `InternalLibs.kt` from this repo — push changes upstream.

## Local dependency overrides

For local dev against in-progress upstream modules, drop a `local.dependencies` file at the repo root (gitignored) or set the `LOCAL_DEPENDENCIES` env var. Format: comma-separated `id=version`, e.g. `core=1.0.150,economy`. Missing version defaults to `InternalLibs.LOCAL_DEPENDENCY_VERSION` (`1000.0.0`). Versions are also forced via `resolutionStrategy.force` in `mmorealms_base.gradle.kts`.

## Code conventions

- Lombok is on every subproject (`@Getter`, `@Accessors(fluent = true)`, etc.). Annotation processor is pre-wired — no need to add it.
- Dependency injection uses `com.raduvoinea.utils.dependency_injection.@Inject` (field injection on module classes, see `AuctionHouseBackendModule.java`).
- Module entrypoints follow a strict pattern: `AuctionHouseCommonModule` (common) → `AuctionHouseBackendModule` (backend-common) → `AuctionHouseFabricModule` / `AuctionHouseNeoForgeModule` (platform). Mirror this when adding code.
- `fabric.mod.json` and `META-INF/neoforge.mods.toml` are templated by `processResources` with `${id}`, `${version}`, `${minecraft_version}`, `${fabric_loader_version}`, `${fabric_api_version}`, `${java_version}`, `${architectury_version}`, `${neoforge_version}`. Use the placeholders; do not hardcode.
- Hikari is shadow-relocated to `gg.mmorealms.shaded.hikari`. The LuckPerms API jar is excluded from shadow output.

## Running / debugging

Locally there is no `./gradlew runServer`. Servers run through the sibling `MMO-REALMS/control-plane` repo:

- IntelliJ run config `app` (`.run/app.run.xml`) invokes `../control-plane/app.py --copy-dev-files --debug` after a Gradle `build`.
- `.run/Run Cobblemon Servers.run.xml` / `Run Pixelmon Servers.run.xml` spin up the Docker Compose stack at `../control-plane/run/<flavor>/docker-compose.yml`.
- The `Debug *.run.xml` configurations attach to those containers.

If `control-plane` is not checked out as a sibling directory, none of those run configs work; just use `./gradlew build` and copy the jars manually.

## CI

- `.github/workflows/build_and_publish.yml` — runs on `main`/`master` on a self-hosted runner, delegates the whole build/version-bump/publish to `control-plane/app.py --build-and-bump`, then creates a GitHub release and triggers a `update-internal-libs-event` dispatch on `MMO-REALMS/build-src`. Skips itself when the head commit author is `Build Bot`.
- `.github/workflows/pr_check.yml` — same orchestrator in `--get` mode for PR validation.
- `.gitlab-ci.yml` includes a template from `mmorealms/ci-templates` — secondary.

Do not push changes that touch `version.txt` or `buildSrc/` expecting CI to run; both are in `paths-ignore`.

## PRs

`.github/pull_request_template.md` expects four sections: `Public Patch Notes`, `Staff Patch Notes`, `Notes` (with `Dependencies:` linking to dependent loader/module PRs), and a `Demo` video. Fill or remove placeholder lines — don't leave `line 1 / line 2 / line 3`.
