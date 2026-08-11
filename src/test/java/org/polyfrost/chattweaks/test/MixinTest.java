package org.polyfrost.chattweaks.test;

import net.minecraft.SharedConstants;
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
        SharedConstants.tryDetectVersion();
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
