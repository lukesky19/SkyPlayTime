# SkyPlayTime
## Description
* SkyPlayTime tracks play time with options to not track play time for inactive (AFK) players.

## Features
* Tracks session, daily, weekly, monthly, yearly, and total play time.
* Automatically resets play time as necessary.
* Option to save a backup of the database on reset.
* Option to save a leaderboard snapshot on reset.
* Won't display AFK messages for vanished players.
* Optional auto-AFK timer, AFK pool detection, AFK mining, and AFK fishing detection.
* An API is available to interface with the plugin.
* Supports PlaceholderAPI.

## Dependencies
* [SkyLib](https://github.com/lukesky19/SkyLib)

## Soft Dependencies
* [NewPlayerPerks](https://github.com/lukesky19/NewPlayerPerks)
* PlaceholderAPI

## Commands
* /skyplaytime - The base command
  * Alias: /playtime 
* /skyplaytime reload - Reloads the plugin.
* /skyplaytime help - View the plugin's help message.
* /skyplaytime time \[player_name] <session | daily | weekly | monthly | yearly | total> - View the play time for yourself or a player.
* /skyplaytime afk \[player_name] - Mark yourself or another player as either AFK or no longer AFK.
  * Alias: /afk 
* /skyplaytime list - List all online players and their AFK status. Excludes vanished players.
  * Alias: /list  
* /skypaytime leaderboard <session | daily | weekly | monthly | yearly | total> - View the play time leaderboard.
* /skyplaytime add <session | daily | weekly | monthly | yearly | total> <player_name> <time> - Add play time to a player.
  * The time should be formatted like 1y3M2w1d12m32s 
* /skyplaytime remove <session | daily | weekly | monthly | yearly | total> <player_name> <time> - Remove play time from a player.
  * The time should be formatted like 1y3M2w1d12m32s
* /skyplaytime set <session | daily | weekly | monthly | yearly | total> <player_name> <time> - Set the play time for a player.
  * The time should be formatted like 1y3M2w1d12m32s
* /skyplaytime reset <session | daily | weekly | monthly | yearly | total> \[player_name] - Reset the play time for all players or a specific player.
* /skyplaytime backup - Backup the database.
* /skyplaytime exempt <player_name> - Marks a player as exempt from leaderboard reporting.
* /skyplaytime unexempt <player_name> - Marks a player as not exempt from leaderboard reporting.
* /skyplaytime debug - The base command for debugging. You shouldn't need to use this.
* /skyplaytime debug status <player_name> - View the player's AFK status.
* /skyplaytime debug last-move - View the last time the player moved.
* /skyplaytime debug last-action - View the last time the player completed an action.
* /skyplaytime debug list - View a list that displays whether a player is online, offline, or unknown and whether their play time is being tracked.

## Permissions
* `skyplaytime.command.skyplaytime` - Base Command Permission
* `skyplaytime.command.skyplaytime.afk` - Permission to toggle your AFK status.
* `skyplaytime.command.skyplaytime.afk.others` - Permission to toggle other player's AFK status.
* `skyplaytime.command.skyplaytime.time` - Permission to view your own play time.
* `skyplaytime.command.skyplaytime.time.others` - Permission to view other player's play time.
* `skyplaytime.command.skyplaytime.leaderboard` - Permission to view the play time leaderboards.
* `skyplaytime.command.skyplaytime.add` - Permission to add play time to a player.
* `skyplaytime.command.skyplaytime.remove` - Permission to remove play time from a player.
* `skyplaytime.command.skyplaytime.set` - Permission to set play time for a player.
* `skyplaytime.command.skyplaytime.reset` - Permission to reset play time for a player or all players.
* `skyplaytime.command.skyplaytime.list` - Permission to list online players and their AFK status.
* `skyplaytime.command.skyplaytime.reload` - Permission to reload the plugin.
* `skyplaytime.command.skyplaytime.save` - Permission to save play time stored in memory to the database.
* `skyplaytime.command.skyplaytime.backup` - Permission to backup the database.
* `skyplaytime.command.skyplaytime.exempt` - Permission to mark a player exempt from the leaderboards.
* `skyplaytime.command.skyplaytime.unexempt` - Permission to mark a player unexempt from the leaderboards.
* `skyplaytime.command.skyplaytime.debug` - The base permission to access the debug command.
* `skyplaytime.command.skyplaytime.debug.status` - Permission to view your own internal afk status.
* `skyplaytime.command.skyplaytime.debug.status.others` - Permission to view the internal afk status of other players.
* `skyplaytime.command.skyplaytime.debug.last-move` - Permission to check when a player last moved.
* `skyplaytime.command.skyplaytime.debug.last-action` - Permission to check when a player last completed an action.
* `skyplaytime.command.skyplaytime.debug.list` - Permission to view a list that displays whether a player is online, offline, or unknown and whether their play time is being tracked.

## FAQ
Q: What versions does this plugin support?

A: 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, and 1.21.10.

Q: Are there any plans to support any other versions?

A: I will always do my best to support the latest versions of the game. I will sometimes support other versions until I no longer use them.

Q: Does this work on Spigot and Paper?

A: Only Paper is supported. There are no plans to support any other server software (i.e., Spigot, Folia).

## Issues, Bugs, or Suggestions
* Please create a new [GitHub Issue](https://github.com/lukesky19/SkyPlayTime/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyPlayTime and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.

## For Server Admins/Owners
* Download the plugin [SkyLib](https://github.com/lukesky19/SkyLib/releases).
* Download the plugin from the releases tab and add it to your server.

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* * Go to [NewPlayerPerks](https://github.com/lukesky19/NewPlayerPerks) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## For Developers
```./gradlew build```

```koitlin
repositories {
  mavenLocal()
}
```

```koitlin
dependencies {
  compileOnly("com.github.lukesky19:SkyPlayTime:1.0.0.0")
}
```

## How To Access The API
Follow the "For Developers" section above and then add this code to your plugin.
Then follow the code example below:

```java
private SkyPlayTimeAPI api;

public SkyPlayTimeAPI getSkyPlayTimeAPI() {
  return api;
}

@Override
public void onEnable() {
  loadSkyPlayTimeAPI();
  if(api == null) {
      this.getServer().getPluginManager().disablePlugin(this);
      return;
  }
  
  // The rest of your plugin's onEnable code.
}

private void loadSkyPlayTimeAPI() {
  @Nullable RegisteredServiceProvider<SkyShopAPI> rsp = this.getServer().getServicesManager().getRegistration(SkyPlayTimeAPI.class);
  if(rsp != null) {
    api = rsp.getProvider();
  }
}
```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
