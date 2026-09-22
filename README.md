# SellBot

Client-side automation mod for Minecraft Java Edition 26.2 using Fabric.

## Features

- Toggle automation with a configurable keybind.
- Default keybind: Right Shift.
- Opens `/sell`.
- Moves non-empty player inventory slots into the sell container.
- Activates the sell action.
- Repeats when the inventory is full again.

## Target

- Minecraft: 26.2
- Fabric Loader: 0.19.5
- Fabric API: 0.161.0+26.2
- Java: 25

## Important

The current implementation assumes the server's `/sell` screen matches the slot layout used during development:

- Sell action: container slot 44
- Player inventory: container slots 45-80
- Screen title: `Sell`

Server-specific GUI layouts may require adjustment.

## Development

Use the included Gradle wrapper when available:

```powershell
.\gradlew.bat build
```

The project uses separate main/client source sets because this is a client-only mod.

## License

A license has intentionally not been selected for this template. Add an appropriate license before publishing.
