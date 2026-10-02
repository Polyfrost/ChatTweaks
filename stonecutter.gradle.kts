plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3" /* [SC] DO NOT EDIT */

stonecutter handlers {
    inherit("json5", "json")
}

stonecutter parameters {
    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }

        string(current.version == "1.8.9") {
            replace("import com.mojang.blaze3d.platform.NativeImage", "import org.polyfrost.chattweaks.compat.NativeImage")
            replace("import com.mojang.blaze3d.platform.Window", "import org.polyfrost.oneconfig.internal.legacy.Window")
            replace("import net.minecraft.client.gui.GuiGraphics", "import org.polyfrost.chattweaks.compat.GuiGraphics")
            replace("import net.minecraft.client.OptionInstance", "import org.polyfrost.chattweaks.compat.OptionInstance")
        }

        string(current.parsed >= "26.1") {
            replace("classTweaker v2 named", "classTweaker v2 official")
        }
    }
}

stonecutter tasks {
    order("publishModrinth")
}
