package org.polyfrost.chattweaks.util;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.polyfrost.chattweaks.ChatTweaks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatUtils {
    public static final int VANILLA_CHAT_LIMIT = 256;

    public static final int COMMAND_LIMIT = 32500;

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final int MIN_FUZZY_LENGTH = 3;
    private static final Pattern TIMESTAMP = Pattern.compile("(?i)^\\S{0,3}?\\d\\d?:\\d\\d(?::\\d\\d)?(?: ?[AP]M)?\\S{0,3}? ");
    private static final Pattern COUNTER = Pattern.compile(" (?:[(\\[{<]x?(\\d+)x?[)\\]}>]|x(\\d+)|(\\d+)x)\\s*$");
    private static final String[] COUNTER_BRACKETS = {"()", "[]", "{}", "<>", "()", "[]", "{}", "<>"};
    private static final String[] TIMESTAMP_BRACKETS = {"[]", "()", "{}", "<>"};
    private static final char COLOR_CHAR = '§';
    private static final String SCREENSHOT_KEY = "screenshot";
    private static final String[] IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".webp", ".gif"};
    private static final int MAX_SIGNATURE = 128;
    private static final char SIGNATURE_SEPARATOR = '\u0000';

    private static String timeCache;
    private static String timeCachePattern;
    private static long timeCacheSecond = Long.MIN_VALUE;
    private static String formatterPattern;
    private static DateTimeFormatter formatter;

    public static boolean isCommand(String message) {
        return !message.isEmpty() && message.charAt(0) == '/';
    }

    public static String normalizeUntrimmed(String message) {
        return WHITESPACE.matcher(message.trim()).replaceAll(" ").trim();
    }

    public static boolean fuzzyMatches(String candidate, String typed) {
        if (typed.isEmpty() || candidate.contains(typed)) {
            return true;
        }
        if (typed.length() < MIN_FUZZY_LENGTH) {
            return false;
        }
        int matched = 0;
        for (int i = 0; i < candidate.length() && matched < typed.length(); i++) {
            if (candidate.charAt(i) == typed.charAt(matched)) {
                matched++;
            }
        }
        return matched == typed.length();
    }

    public static String cleanColor(String in) {
        int first = in.indexOf(COLOR_CHAR);
        if (first < 0) {
            return in;
        }
        StringBuilder out = new StringBuilder(in.length()).append(in, 0, first);
        for (int i = first; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == COLOR_CHAR && i + 1 < in.length() && !isLineTerminator(in.charAt(i + 1))) {
                i++;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }

    private static boolean isLineTerminator(char c) {
        return c == '\n' || c == '\r' || c == '\u0085' || c == '\u2028' || c == '\u2029';
    }

    public static boolean isBlank(String in) {
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == COLOR_CHAR && i + 1 < in.length() && !isLineTerminator(in.charAt(i + 1))) {
                i++;
                continue;
            }
            if (c > ' ') {
                return false;
            }
        }
        return true;
    }

    public static boolean isScreenshot(Component component, String raw) {
        return hasImagePath(raw) || hasScreenshotMarker(component);
    }

    private static boolean hasScreenshotMarker(Component component) {
        if (component.getContents() instanceof TranslatableContents translatable
                && containsIgnoreCase(translatable.getKey(), SCREENSHOT_KEY)) {
            return true;
        }
        ClickEvent click = component.getStyle().getClickEvent();
        if (click != null) {
            String value = ChatCompat.safeClickValue(click);
            if (value != null && (containsIgnoreCase(value, SCREENSHOT_KEY) || isImagePath(value))) {
                return true;
            }
        }
        for (Component sibling : component.getSiblings()) {
            if (hasScreenshotMarker(sibling)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isImagePath(String value) {
        int end = value.length();
        for (int i = 0; i < end; i++) {
            char c = value.charAt(i);
            if (c == '?' || c == '#') {
                end = i;
                break;
            }
        }
        for (String extension : IMAGE_EXTENSIONS) {
            int start = end - extension.length();
            if (start > 0 && value.regionMatches(true, start, extension, 0, extension.length())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasImagePath(String raw) {
        for (String extension : IMAGE_EXTENSIONS) {
            int from = 0;
            while (true) {
                int at = indexOfIgnoreCase(raw, extension, from);
                if (at < 0) {
                    break;
                }
                int after = at + extension.length();
                if (at > 0 && (after == raw.length() || !Character.isLetterOrDigit(raw.charAt(after)))) {
                    return true;
                }
                from = at + 1;
            }
        }
        return false;
    }

    private static boolean containsIgnoreCase(String value, String needle) {
        return indexOfIgnoreCase(value, needle, 0) >= 0;
    }

    private static int indexOfIgnoreCase(String value, String needle, int from) {
        int limit = value.length() - needle.length();
        for (int i = Math.max(from, 0); i <= limit; i++) {
            if (value.regionMatches(true, i, needle, 0, needle.length())) {
                return i;
            }
        }
        return -1;
    }

    public static String compactKey(Component component, String raw) {
        String key = compactKey(raw);
        if (key.isEmpty()) {
            return key;
        }
        StringBuilder events = new StringBuilder();
        appendEvents(component, events);
        if (events.isEmpty()) {
            return key;
        }
        String signature = events.length() > MAX_SIGNATURE
                ? Integer.toHexString(events.toString().hashCode())
                : events.toString();
        return key + SIGNATURE_SEPARATOR + signature;
    }

    private static void appendEvents(Component component, StringBuilder out) {
        Style style = component.getStyle();
        ClickEvent click = style.getClickEvent();
        if (click != null) {
            out.append(SIGNATURE_SEPARATOR).append(click);
        }
        HoverEvent hover = style.getHoverEvent();
        if (hover != null) {
            out.append(SIGNATURE_SEPARATOR).append(hover);
        }
        String insertion = style.getInsertion();
        if (insertion != null) {
            out.append(SIGNATURE_SEPARATOR).append(insertion);
        }
        for (Component sibling : component.getSiblings()) {
            appendEvents(sibling, out);
        }
    }

    public static String compactKey(String raw) {
        String clean = Spacing.strip(cleanColor(raw));
        clean = TIMESTAMP.matcher(clean).replaceAll("");
        if (endsLikeCounter(clean)) {
            clean = COUNTER.matcher(clean).replaceAll("");
        }
        return clean.trim();
    }

    private static boolean endsLikeCounter(String clean) {
        for (int i = clean.length() - 1; i >= 0; i--) {
            char c = clean.charAt(i);
            if (Character.isWhitespace(c) || isLineTerminator(c)) {
                continue;
            }
            return c == ')' || c == ']' || c == '}' || c == '>' || c == 'x' || c == 'X'
                    || (c >= '0' && c <= '9');
        }
        return false;
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

        long second = System.currentTimeMillis() / 1000L;
        if (second == timeCacheSecond && pattern.equals(timeCachePattern)) {
            return timeCache;
        }
        if (!pattern.equals(formatterPattern)) {
            formatterPattern = pattern;
            formatter = DateTimeFormatter.ofPattern(pattern);
        }

        timeCacheSecond = second;
        timeCachePattern = pattern;
        timeCache = LocalTime.now().format(formatter);
        return timeCache;
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
