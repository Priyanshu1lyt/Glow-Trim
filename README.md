# Glow Trims

A client-side Fabric mod for **Minecraft Java 1.21.11**.

## What it does

- Renders vanilla armor-trim overlays at full brightness.
- Preserves the original trim material colors and vanilla trim patterns.
- Works as a client-side visual effect; no server installation is required.
- Intended to make trims easier to see in dark scenes while recording videos.

## Important visual note

This is a full-bright/emissive-style rendering effect, not a light source. It does not illuminate nearby blocks and does not add a bloom halo. A bloom effect would need a post-processing shader or a compatible shader pack.

## Build (GitHub Actions - works from phone)

Every push to `main` builds the mod automatically.

1. Open the **Actions** tab of this repo.
2. Open the latest **Build** run.
3. Download the **glowtrims-jar** artifact (zip) and extract `glowtrims-1.0.0.jar`.

You can also run the workflow manually: Actions > Build > Run workflow.

## Build locally

Requirements:
- JDK 21
- Gradle 9.4 or newer (Loom 1.17 requirement)

```bash
gradle build
```

The output JAR will be in `build/libs/`.

## Install

1. Put `glowtrims-1.0.0.jar` in your Minecraft `mods` folder.
2. Launch Minecraft 1.21.11 with Fabric Loader 0.19.3 or newer and Fabric API 0.141.6+1.21.11.

## Current implementation scope

The project uses official Mojang mappings for 1.21.11, matching Fabric's recommendation for this final obfuscated Minecraft version. The mixin targets the vanilla armor-trim submit operation in the equipment renderer and changes its packed-light value to full brightness. The vanilla trim sprite and its tint are not replaced.

If the game reports a Mixin target error, capture the full latest.log and the exact Fabric Loader / Fabric API versions; the renderer's internal call order can change between game builds.
