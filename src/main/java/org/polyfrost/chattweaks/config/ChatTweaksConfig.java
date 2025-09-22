package org.polyfrost.chattweaks.config;

import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class ChatTweaksConfig extends Config {
    public ChatTweaksConfig() {
        super(ChatTweaks.ID + ".json", ChatTweaks.NAME, Category.QOL);
    }

    @Switch(
            title = "Remove Blank Messages",
            description = "Stop messages with no content from showing up in chat."
    )
    public boolean removeBlankMessages = false;


    @Switch(
            title = "Compact Chat",
            description = "Clean up the chat by stacking duplicate messages."
    )
    public boolean compactChat = true;

    @Switch(
            title = "Consecutive Compact Chat",
            description = "Only compact messages if they're consecutive."
    )
    public boolean consecutiveCompactChat;

    @Slider(
            title = "Compact Chat Time",
            description = "Change the amount of time old messages take to stop being compacted. Measured in seconds.",
            min = 1, max = 120
    )
    public int compactChatTime = 60;


    @Switch(
            title = "Chat Timestamps",
            description = "Add timestamps before a message."
    )
    public boolean timestamps;

    @Dropdown(
            title = "Chat Timestamps Format",
            description = "Change the time format of Chat Timestamps.",
            options = {"12 Hour", "24 Hour"}
    )
    public int timestampsFormat = 0;

    @Dropdown(
            title = "Chat Timestamps Style",
            description = "Choose how Chat Timestamps should appear.",
            options = {"Always Present", "Message Hover"}
    )
    public int timestampsStyle = 0;

    @Switch(
            title = "Show Seconds on Timestamps",
            description = "Show the seconds on a timestamped message."
    )
    public boolean secondsOnTimestamps;


    @Switch(
            title = "Shift Chat",
            description = "Keep chat open while sending a message if Shift is held while pressing Enter."
    )
    public boolean shiftChat;


    @Switch(
            title = "Safe Chat Clicks",
            description = "Show the command or link that is run/opened on click. "
    )
    public boolean safeChatClicks;

    @Switch(
            title = "Safe Chat Clicks History",
            description = "Adds commands sent from clicking chat messages to the chat history."
    )
    public boolean safeChatClicksHistory;


    @Switch(
            title = "Image Preview",
            description = "Preview image links when hovering over a supported URL." +
                    "\nPress Shift to use fullscreen and Control to render in native image resolution."
    )
    public boolean imagePreview = true;

    @Slider(
            title = "Image Preview Width",
            description = "The %% of screen width to be used for image preview.",
            min = 0F, max = 1F
    )
    public float imagePreviewWidth = 0.50F;
}
