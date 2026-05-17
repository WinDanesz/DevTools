# DevTools

Minimal Minecraft 1.12.2 client-side mod that I made mainy for my dev env to save some time typing commands, but it can be just as useful for any player or pack maker. Client-side only, not loaded on the server side.

All keybindings are short actions to send the equivalent vanilla command as a chat message, this is how the mod is able to be client-side only while still working on any server. The **Create Test World** button creates a new world with the same settings each time, perfect for testing things in a controlled environment.

## Keybindings

| Key | Action |
|-----|--------|
| `F6` | Switch to Survival mode |
| `F7` | Switch to Creative mode |
| `F8` | Switch to Spectator mode |
| `F9` | Toggle day / night |
| `F10` | Fully heal (restores HP and hunger) |
| `F12` | Repeat last command sent in chat |

All keybindings are rebindable in the vanilla Controls screen under the **DevTools** category.

### Repeat Last Command (F12)

Any command you send in chat (starting with `/`) is remembered. Press `F12` at any time to resend it. Useful for rapidly re-running `/give`, `/summon`, or any other command during testing.

## Main Menu: Create Test World

A **Create Test World** button is added next to the Singleplayer button on the main menu. It instantly creates and launches a superflat creative world with:

- Creative mode
- Cheats enabled
- `doDaylightCycle=false`
- `doWeatherCycle=false`
- `doMobSpawning=false`

Each click creates a new world. Existing test worlds are not overwritten.

## How it works

The mod is `clientSideOnly`, so Forge will not require it on the server and clients can connect to servers that don't have it.

All state-changing actions (gamemode, time, heal) work by sending the corresponding vanilla command as a chat message. This means the server is always authoritative — no client-side state is manipulated directly. It also means the gamemode and heal keys require the player to have operator permissions (or cheats enabled) on the server. In the generated test world cheats are always on, so all keys work there out of the box.

The **Create Test World** button bypasses the normal world creation screen by directly writing world NBT data and launching the integrated server, which is why it's instant.

The **Repeat Last Command** feature hooks into `ClientChatEvent` to record any message starting with `/` before it is sent, then replays it on demand.
