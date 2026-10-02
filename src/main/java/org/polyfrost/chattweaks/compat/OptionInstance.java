package org.polyfrost.chattweaks.compat;

//? if = 1.8.9 {
/*import java.util.function.Consumer;
import java.util.function.Supplier;

public record OptionInstance<T>(Supplier<T> getter, Consumer<T> setter) {
    public T get() {
        return getter.get();
    }

    public void set(T value) {
        setter.accept(value);
    }
}
*///?}
