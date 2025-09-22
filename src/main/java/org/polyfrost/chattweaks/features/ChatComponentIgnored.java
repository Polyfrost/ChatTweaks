package org.polyfrost.chattweaks.features;

import net.minecraft.util.ChatComponentText;

public class ChatComponentIgnored extends ChatComponentText {
    public ChatComponentIgnored(String msg) {
        super(msg);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}