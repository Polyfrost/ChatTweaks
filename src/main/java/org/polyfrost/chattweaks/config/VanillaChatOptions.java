package org.polyfrost.chattweaks.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.polyfrost.oneconfig.api.config.v1.Properties;
import org.polyfrost.oneconfig.api.config.v1.Property;
import org.polyfrost.oneconfig.api.config.v1.Tree;
import org.polyfrost.oneconfig.api.config.v1.Visualizer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class VanillaChatOptions {
    private static final String CATEGORY = "General";
    private static final String BEHAVIOR = "Behavior";

    private static final AtomicBoolean saveQueued = new AtomicBoolean();
    private static volatile boolean broadcastQueued;

    private VanillaChatOptions() {
    }

    public static void attach(Tree tree) {
        tree.put(dropdown("vanilla_chat_visibility", "Chat Visibility",
                "Which messages are shown in chat.",
                new String[]{"Shown", "Commands Only", "Hidden"},
                Options::chatVisibility, true));

        tree.put(toggle("vanilla_chat_colors", "Colors",
                "Show colored text in chat.", BEHAVIOR, Options::chatColors, true));
        tree.put(toggle("vanilla_chat_links", "Web Links",
                "Make links in chat clickable.", BEHAVIOR, Options::chatLinks, false));
        tree.put(toggle("vanilla_chat_links_prompt", "Prompt on Links",
                "Ask for confirmation before opening a link from chat.", BEHAVIOR, Options::chatLinksPrompt, false));
        tree.put(toggle("vanilla_auto_suggestions", "Command Suggestions",
                "Show command suggestions while typing.", BEHAVIOR, Options::autoSuggestions, false));
        tree.put(toggle("vanilla_hide_matched_names", "Hide Matched Names",
                "Hide messages from players hidden by the social interactions screen, even when the server changes their name.",
                BEHAVIOR, Options::hideMatchedNames, false));
        tree.put(toggle("vanilla_only_show_secure_chat", "Only Show Secure Chat",
                "Only show messages that are signed by their sender.", BEHAVIOR, Options::onlyShowSecureChat, false));
        //? if >=1.21.10 {
        tree.put(toggle("vanilla_save_chat_drafts", "Save Chat Drafts",
                "Keep an unsent message when the chat is closed.", BEHAVIOR, Options::saveChatDrafts, false));
        //?}

        tree.put(slider("vanilla_chat_delay", "Chat Delay",
                "Delay before received messages are shown, in seconds.", BEHAVIOR,
                Options::chatDelay, 1f, 0f, 6f, 0.1f));
    }

    private static Property<Boolean> toggle(String id, String title, String description, String subcategory,
                                            Function<Options, OptionInstance<Boolean>> accessor, boolean broadcast) {
        Property<Boolean> property = Properties.functional(
                () -> {
                    Options options = options();
                    return options == null ? Boolean.FALSE : accessor.apply(options).get();
                },
                value -> {
                    Options options = options();
                    if (options == null || value == null) return;
                    accessor.apply(options).set(value);
                    flush(broadcast);
                },
                id, title, description, Boolean.class
        );
        return decorate(property, Visualizer.SwitchVisualizer.class, title, description, subcategory);
    }

    private static Property<Float> slider(String id, String title, String description, String subcategory,
                                          Function<Options, OptionInstance<Double>> accessor,
                                          float scale, float min, float max, float step) {
        Property<Float> property = Properties.functional(
                () -> {
                    Options options = options();
                    if (options == null) return min;
                    Double value = accessor.apply(options).get();
                    return value == null ? min : (float) (value * scale);
                },
                value -> {
                    Options options = options();
                    if (options == null || value == null) return;
                    accessor.apply(options).set((double) (value / scale));
                    flush(false);
                },
                id, title, description, Float.class
        );
        decorate(property, Visualizer.SliderVisualizer.class, title, description, subcategory);
        property.addMetadata("min", min);
        property.addMetadata("max", max);
        property.addMetadata("step", step);
        return property;
    }

    private static Property<Integer> dropdown(String id, String title, String description, String[] labels,
                                              Function<Options, OptionInstance<?>> accessor, boolean broadcast) {
        Property<Integer> property = Properties.functional(
                () -> {
                    Options options = options();
                    if (options == null) return 0;
                    Object value = accessor.apply(options).get();
                    return value instanceof Enum ? ((Enum<?>) value).ordinal() : 0;
                },
                index -> {
                    Options options = options();
                    if (options == null || index == null) return;
                    OptionInstance instance = accessor.apply(options);
                    Object[] constants = enumConstants(instance.get());
                    if (constants == null || index < 0 || index >= constants.length) return;
                    instance.set(constants[index]);
                    flush(broadcast);
                },
                id, title, description, Integer.class
        );
        decorate(property, Visualizer.DropdownVisualizer.class, title, description, BEHAVIOR);
        property.addMetadata("options", labels);
        return property;
    }

    private static Object[] enumConstants(Object value) {
        if (value == null) return null;
        Class<?> type = value.getClass();
        if (!type.isEnum()) type = type.getSuperclass();
        return type == null ? null : type.getEnumConstants();
    }

    private static <T> Property<T> decorate(Property<T> property, Class<? extends Visualizer> visualizer,
                                            String title, String description, String subcategory) {
        property.addMetadata("visualizer", visualizer);
        property.addMetadata("title", title);
        property.addMetadata("description", description);
        property.addMetadata("category", CATEGORY);
        property.addMetadata("subcategory", subcategory);
        return property;
    }

    private static Options options() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options;
    }

    private static void flush(boolean broadcast) {
        if (broadcast) broadcastQueued = true;
        Minecraft minecraft = Minecraft.getInstance();
        if (!saveQueued.compareAndSet(false, true)) return;
        minecraft.execute(() -> {
            saveQueued.set(false);
            minecraft.options.save();
            if (broadcastQueued) {
                broadcastQueued = false;
                minecraft.options.broadcastOptions();
            }
        });
    }
}
