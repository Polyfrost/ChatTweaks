package org.polyfrost.chattweaks.config;

import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Color;
import org.polyfrost.oneconfig.api.config.v1.annotations.Dropdown;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;
import org.polyfrost.oneconfig.api.config.v1.annotations.Text;

public class ChatTweaksConfig extends Config {
    public ChatTweaksConfig() {
        super(ChatTweaks.ID + ".json", ChatTweaks.NAME, Category.QOL);

        loadFrom("patcher.toml");
    }

    @Override
    protected void initialize(boolean byConfigManager) {
        super.initialize(byConfigManager);
        hideIf("timestampsLeftBracket", () -> timestampsBrackets != 5);
        hideIf("timestampsRightBracket", () -> timestampsBrackets != 5);
    }

    @Switch(
            title = "Remove Blank Messages",
            description = "Stop messages with no content from showing up in chat.",
            subcategory = "Cleanup"
    )
    public boolean removeBlankMessages = false;


    @Switch(
            title = "Compact Chat",
            description = "Clean up the chat by stacking duplicate messages.",
            subcategory = "Compact Chat"
    )
    public boolean compactChat = true;

    @Switch(
            title = "Consecutive Compact Chat",
            description = "Only compact messages if they're consecutive.",
            subcategory = "Compact Chat"
    )
    public boolean consecutiveCompactChat;

    @Slider(
            title = "Compact Chat Time",
            description = "Change the amount of time old messages take to stop being compacted. Measured in seconds.",
            subcategory = "Compact Chat",
            min = 1, max = 120
    )
    public int compactChatTime = 60;

    @Dropdown(
            title = "Compact Chat Format",
            description = "Change how the counter appended to compacted messages is formatted.",
            options = {"(2)", "[2]", "{2}", "<2>", "(x2)", "[x2]", "{x2}", "<x2>", "x2", "2x"},
            subcategory = "Compact Chat"
    )
    public int compactChatFormat = 0;

    @Color(
            title = "Compact Chat Color",
            description = "Change the color of the counter appended to compacted messages.",
            subcategory = "Compact Chat",
            alpha = false
    )
    public java.awt.Color compactChatColor = new java.awt.Color(0xAAAAAA);


    @Switch(
            title = "Chat Timestamps",
            description = "Add timestamps before a message.",
            subcategory = "Timestamps"
    )
    public boolean timestamps;

    @Dropdown(
            title = "Chat Timestamps Format",
            description = "Change the time format of Chat Timestamps.",
            options = {"12 Hour", "24 Hour"},
            subcategory = "Timestamps"
    )
    public int timestampsFormat = 0;

    @Dropdown(
            title = "Chat Timestamps Style",
            description = "Choose how Chat Timestamps should appear.",
            options = {"Always Present", "Message Hover"},
            subcategory = "Timestamps"
    )
    public int timestampsStyle = 0;

    @Dropdown(
            title = "Chat Timestamps Brackets",
            description = "Change the brackets that surround Chat Timestamps.",
            options = {"[12:00]", "(12:00)", "{12:00}", "<12:00>", "12:00", "Custom"},
            subcategory = "Timestamps"
    )
    public int timestampsBrackets = 0;

    @Text(
            title = "Custom Left Bracket",
            description = "The text placed before the time. Only used when Chat Timestamps Brackets is set to Custom.",
            subcategory = "Timestamps",
            placeholder = "["
    )
    public String timestampsLeftBracket = "[";

    @Text(
            title = "Custom Right Bracket",
            description = "The text placed after the time. Only used when Chat Timestamps Brackets is set to Custom.",
            subcategory = "Timestamps",
            placeholder = "]"
    )
    public String timestampsRightBracket = "]";

    @Color(
            title = "Chat Timestamps Color",
            description = "Change the color of Chat Timestamps.",
            subcategory = "Timestamps",
            alpha = false
    )
    public java.awt.Color timestampsColor = new java.awt.Color(0xAAAAAA);

    @Switch(
            title = "Only Show New Timestamps",
            description = "Only timestamp the first message of each new time, and align the rest below it. Has no effect when the style is Message Hover.",
            subcategory = "Timestamps"
    )
    public boolean onlyNewTimestamps;

    @Switch(
            title = "Show Seconds on Timestamps",
            description = "Show the seconds on a timestamped message.",
            subcategory = "Timestamps"
    )
    public boolean secondsOnTimestamps;


    @Switch(
            title = "Shift Chat",
            description = "Keep chat open while sending a message if Shift is held while pressing Enter.",
            subcategory = "QOL"
    )
    public boolean shiftChat;

    @Switch(
            title = "Safe Chat Clicks",
            description = "Show the command or link that is run/opened on click. ",
            subcategory = "Safe Chat Clicks"
    )
    public boolean safeChatClicks;

    @Switch(
            title = "Safe Chat Clicks History",
            description = "Adds commands sent from clicking chat messages to the chat history.",
            subcategory = "Safe Chat Clicks"
    )
    public boolean safeChatClicksHistory;

    @Switch(
            title = "Image Preview",
            description = "Preview image links when hovering over a supported URL." +
                    "\nPress Shift to use fullscreen and Control to render in native image resolution.",
            subcategory = "Image Preview"
    )
    public boolean imagePreview = true;

    @Slider(
            title = "Image Preview Width",
            description = "The % of screen width to be used for image preview.",
            subcategory = "Image Preview"
    )
    public float imagePreviewWidth = 50F;
}
