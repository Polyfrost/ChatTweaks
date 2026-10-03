package org.polyfrost.chattweaks.test;

//? if > 1.8.9 {
import net.minecraft.SharedConstants;
//?} else {
/*import net.fabricmc.loader.api.FabricLoader;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
*///?}
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Option;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

/**
 * Audits mixins for validity without launching a full Minecraft client
 * Inspired by <a href="https://github.com/SkyblockerMod/Skyblocker">Skyblocker</a>
 */
public class MixinTest {

    @BeforeAll
    public static void setupEnvironment() {
        //? if > 1.8.9 {
        SharedConstants.tryDetectVersion();
        //?} else {
        /*FabricLoader.getInstance().invokeEntrypoints(ModInitializer.ENTRYPOINT_KEY, ModInitializer.class, ModInitializer::init);
        *///?}
        Bootstrap.bootStrap();
    }

    @Test
    @DisplayName("mixins load successfully")
    public void auditMixins() {
        MixinEnvironment environment = MixinEnvironment.getCurrentEnvironment();
        Assertions.assertInstanceOf(
                IMixinTransformer.class,
                environment.getActiveTransformer()
        );
        // in dev Fabric Loader retries failed selectors with the descriptor stripped
        // production does not so disable it or the audit passes mixins that cannot apply
        environment.setOption(Option.REFMAP_REMAP, false);
        environment.audit();
    }
}
