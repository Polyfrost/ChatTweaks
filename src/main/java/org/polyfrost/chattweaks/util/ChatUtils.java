package org.polyfrost.chattweaks.util;

import org.polyfrost.chattweaks.ChatTweaks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatUtils {
    private static final Pattern TIMESTAMP = Pattern.compile("(?i)^\\S{0,3}?\\d\\d?:\\d\\d(?::\\d\\d)?(?: ?[AP]M)?\\S{0,3}? ");
    private static final Pattern COUNTER = Pattern.compile(" (?:[(\\[{<]x?(\\d+)x?[)\\]}>]|x(\\d+)|(\\d+)x)\\s*$");
    private static final String[] COUNTER_BRACKETS = {"()", "[]", "{}", "<>", "()", "[]", "{}", "<>"};
    private static final String[] TIMESTAMP_BRACKETS = {"[]", "()", "{}", "<>"};
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
            for (int i = 1; i <= m.groupCount(); i++) {
                if (m.group(i) == null) {
                    continue;
                }
                try {
                    return Integer.parseInt(m.group(i));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 1;
    }

    public static String formatCount(int count) {
        int format = ChatTweaks.config.compactChatFormat;
        if (format == 8) {
            return " x" + count;
        }
        if (format == 9) {
            return " " + count + "x";
        }
        String brackets = COUNTER_BRACKETS[Math.max(0, Math.min(format, COUNTER_BRACKETS.length - 1))];
        String body = format >= 4 ? "x" + count : String.valueOf(count);
        return " " + brackets.charAt(0) + body + brackets.charAt(1);
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

    public static String formatTimestamp(String time) {
        int format = ChatTweaks.config.timestampsBrackets;
        if (format == 4) {
            return time;
        }
        if (format == 5) {
            return ChatTweaks.config.timestampsLeftBracket + time + ChatTweaks.config.timestampsRightBracket;
        }
        String brackets = TIMESTAMP_BRACKETS[Math.max(0, Math.min(format, TIMESTAMP_BRACKETS.length - 1))];
        return brackets.charAt(0) + time + brackets.charAt(1);
    }
}
