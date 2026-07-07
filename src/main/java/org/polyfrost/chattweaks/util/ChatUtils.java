package org.polyfrost.chattweaks.util;

import org.polyfrost.chattweaks.ChatTweaks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatUtils {
    private static final Pattern TIMESTAMP = Pattern.compile("^(?:\\[\\d\\d?:\\d\\d(?::\\d\\d)?(?: AM| PM|)]|<\\d\\d?:\\d\\d>) ");
    private static final Pattern COUNTER = Pattern.compile(" \\((\\d+)\\)\\s*$");
    private static final Pattern COLOR = Pattern.compile("(?i)\\u00A7.");

    public static String cleanColor(String in) {
        return COLOR.matcher(in).replaceAll("");
    }

    public static String compactKey(String raw) {
        String clean = cleanColor(raw);
        clean = TIMESTAMP.matcher(clean).replaceAll("");
        clean = COUNTER.matcher(clean).replaceAll("");
        return clean.trim();
    }

    public static int extractCount(String raw) {
        Matcher m = COUNTER.matcher(cleanColor(raw));
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 1;
    }

    public static boolean isDivider(String clean) {
        clean = compactKey(clean);
        if (clean.length() < 5) {
            return false;
        }
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            if (c != '-' && c != '=' && c != '▬') {
                return false;
            }
        }
        return true;
    }

    public static String getCurrentTime() {
        String pattern = ChatTweaks.config.timestampsFormat == 1 ? "HH:mm" : "hh:mm a";
        if (ChatTweaks.config.secondsOnTimestamps) {
            pattern = ChatTweaks.config.timestampsFormat == 1 ? "HH:mm:ss" : "hh:mm:ss a";
        }
        return LocalTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
}
