# ChatTweaks

[![Modrinth downloads](https://img.shields.io/modrinth/dt/8LNT47ZH?style=flat&logo=modrinth&label=Modrinth&color=1bd96a)](https://modrinth.com/mod/chattweaks)
[![CurseForge downloads](https://img.shields.io/curseforge/dt/1740825?style=flat&logo=curseforge&label=CurseForge&color=f16436)](https://www.curseforge.com/minecraft/mc-mods/chattweaks)

ChatTweaks stacks repeated chat messages into one line with a counter. It also keeps your chat between sessions, previews image links and can add a timestamp to each message.

## Features

### Compact chat

When a message repeats within 60 seconds, ChatTweaks removes the earlier copy and adds a counter such as `(2)` to the new one. You can set the time between 1 and 120 seconds, pick one of 10 counter formats and change the counter color.

Screenshot messages stay separate, so each one keeps its own file link. You can turn that off. A separate option only stacks a message when it repeats the message directly above it.

### Chat history

- ChatTweaks keeps 1000 messages in the chat instead of the vanilla 100. A slider sets the limit between 100 and 16384.
- The chat stays in place when you disconnect from a world or server.
- ChatTweaks saves the chat to disk for each account and each server or singleplayer world, and restores it when you connect again. It writes the file every 30 seconds, and you can set the interval between 5 and 60 seconds. The files are in the `chattweaks/chat-history` folder of your game directory.
- A divider with the date and time marks each new connection.

You can turn each of these off.

### Image preview

Open the chat and hover over a message that contains a link. ChatTweaks loads the image and draws it next to your cursor, with the file name and the host above it. If the link points to a web page, ChatTweaks shows the page's preview image. Hovering over the name of a file in your screenshots folder previews that screenshot.

The preview uses up to 25% of the screen width. A slider sets that between 5% and 50%. Hold Shift to fit the image to the window.

### Timestamps

Timestamps are off by default. Turn them on to put the time in front of each message, or to show it when you hover over a message.

- Choose a 12-hour or 24-hour clock, with or without seconds.
- Choose the brackets around the time from five presets, or type your own.
- Change the timestamp color.
- Show the time only when it changes, with later messages aligned under it.

### Commands and clicks

- ChatTweaks lets you type commands longer than the vanilla chat box limit. This is on by default.
- Safe chat clicks adds a line to a message's hover text that shows the command it runs or the link it opens when you click it. A second option adds those clicked commands to your sent message history. Both are off by default.
- Shift chat keeps the chat open when you hold Shift and press Enter to send. It is off by default.
- Fuzzy autocomplete suggests commands that contain what you typed, not only commands that start with it. It is off by default and is not in the 1.8.9 build.
- An option hides chat messages that have no text. It is off by default.

### Vanilla chat settings

The ChatTweaks settings screen also shows Minecraft's own chat options, such as chat visibility, colors, web links and the link prompt. The Fabric builds add command suggestions, hide matched names, only show secure chat and chat delay.

## Compatibility

- ChatTweaks does not load alongside Chat Patches.

## Supported versions

- Minecraft 1.8.9 on Ornithe.
- Minecraft 1.21.1, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1, 26.2 and 26.3 on Fabric.

ChatTweaks needs [OneConfig](https://modrinth.com/mod/oneconfig). The Fabric builds also need Fabric API.

## Support

- Report bugs and ask questions on the [Polyfrost Discord](https://polyfrost.org/discord).
- Check [Polyfrost Status](https://status.polyfrost.org/) if a Polyfrost service is down.

## License

ChatTweaks is adapted from [PolyPatcher](https://modrinth.com/mod/patcher) by Polyfrost. Both use the CC BY-NC-SA 4.0 license.

## Building

```sh
./gradlew ":26.3:build"
```

Replace `26.3` with any supported Minecraft version. The build needs JDK 21 and JDK 25 installed.
