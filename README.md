# SkiesKits
<img height="50" src="https://camo.githubusercontent.com/a94064bebbf15dfed1fddf70437ea2ac3521ce55ac85650e35137db9de12979d/68747470733a2f2f692e696d6775722e636f6d2f6331444839564c2e706e67" alt="Requires Fabric Kotlin"/>

A Fabric (1.20.1) server-sided Kits mod! Creating a new kit is as easy as creating a new file in the `kits` folder and copying the basic formatting.

More information on configuration can be found on the [Wiki](https://github.com/PokeSkies/SkiesKits/wiki)!

## Features
- Create practically infinite Kits *(idk, haven't tested that)*
- Customizable kit cooldowns
- Define the maximum kit uses
- Customizable kit menu system
- Set custom requirements to claim a kit (permissions, placeholders, etc.)
- Customize the actions a kit will execute depending on the result (success, cooldown, usage, etc.)

## Installation
1. Download the latest version of the mod from the Releases tab.
2. Download all required dependencies:
   - [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) 
   - [Fabric Permissions API](https://github.com/PokeSkies/fabric-permissions-api)
   - [GooeyLibs](https://github.com/NickImpact/GooeyLibs/tree/1.20.1)
3. Download any optional dependencies:
   - [Impactor](https://modrinth.com/mod/impactor) **_(Economy, Placeholders)_**
   - Pebbles Economy **_(Economy)_**
   - [PlaceholderAPI]() **_(Placeholders)_**
   - [Plan]() **_(Additional Requirements)_**
4. Install the mod and dependencies into your server's `mods` folder.
5. Configure your Kits in the `./config/skieskits/kits/` folder.

## Commands/Permissions
| Command                            | Description                                                                    | Permission                      |
|------------------------------------|--------------------------------------------------------------------------------|---------------------------------|
| /kits reload                       | Reload SkiesKits                                                               | skieskits.command.reload        |
| /kits debug                        | Toggle the debug mode for more insight into errors                             | skieskits.command.debug         |
| /kits                              | Opens the kits menu system                                                     | skieskits.command.base          |
| /kits <kit>                        | Attempts to claim a kit                                                        | skieskits.command.claim         |
| /kits claim <kit>                  | Attempts to claim a kit                                                        | skieskits.command.claim         |
| /kits give <kit> <player> [bypass] | Attempts to give a player a kit. Set bypass to true to bypass any requirements | skieskits.command.give          |
| /kits resetusage <player> [kit]    | Reset a player's kit usage for all kits or define a specific one               | skieskits.command.resetusage    |
| /kits resetcooldown <player> [kit] | Reset a player's kit cooldown for all kits or define a specific one            | skieskits.command.resetcooldown |

## Planned Features
- Better/more debugging and error handling
- In-game kit editing (via commands and GUI editor)

**If you have any suggestions, feel free to message me on Discord (@stampede2011)**