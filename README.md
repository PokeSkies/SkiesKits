# SkiesKits
<img height="50" src="https://camo.githubusercontent.com/a94064bebbf15dfed1fddf70437ea2ac3521ce55ac85650e35137db9de12979d/68747470733a2f2f692e696d6775722e636f6d2f6331444839564c2e706e67" alt="Requires Fabric Kotlin"/>

A Fabric (1.20.1) server-sided Kits mod! Creating a new kit is as easy as creating a new file in the `kits` folder and copying the basic formatting.

More information on configuration can be found on the [Wiki](https://github.com/PokeSkies/SkiesGUIs/wiki)!

## Features
- Create practically infinite Kits *(idk, haven't tested that)*
- Variable kit cooldowns
- Define the maximum amount of kit uses
- Set custom requirements to claim a kit
- Customize the actions a kit will execute depending on the result (success, cooldown, usage, etc.)

## Installation
1. Download the latest version of the mod from the Releases tab.
2. Download all required dependencies:
   - [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) 
   - [Fabric Permissions API](https://github.com/PokeSkies/fabric-permissions-api)
   - [GooeyLibs](https://github.com/NickImpact/GooeyLibs/tree/1.20.1)
3. Download any optional dependencies:
   - [Impactor](https://modrinth.com/mod/impactor) **_(OPTIONAL)_**
4. Install the mod and dependencies into your server's `mods` folder.
5. Configure your Kits in the `./config/skieskits/kits/` folder.

## Commands/Permissions
| Command                     | Description                                        | Permission                 |
|-----------------------------|----------------------------------------------------|----------------------------|
| /kits reload                | Reload SkiesKits                                   | skiesguis.command.reload   |
| /kits debug                 | Toggle the debug mode for more insight into errors | skiesguis.command.deug     |

## Planned Features
- Better/more debugging and error handling
- More Placeholders (support Placeholders mod?)

**If you have any suggestions, feel free to message me on Discord (@stampede2011)**