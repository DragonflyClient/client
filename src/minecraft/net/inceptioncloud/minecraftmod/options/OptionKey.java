package net.inceptioncloud.minecraftmod.options;

import lombok.Getter;
import net.inceptioncloud.minecraftmod.InceptionMod;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a value that is set in the options file for a specific key.
 * @param <T> The type of the value
 */
@Getter
public class OptionKey<T>
{
    /**
     * The class of the value's type.
     */
    private final Class<T> typeClass;

    /**
     * The key that is set for the option in the file.
     */
    private final String key;

    /**
     * The predicate that checks if the value for this option is valid.
     */
    private final Predicate<T> validator;

    /**
     * The fallback value for the option.
     */
    private final Supplier<T> defaultValue;

    /**
     * Initialize a new Options Key.
     * @param key The key in String-Format under which the value is saved
     * @param validator Validates whether the current value is acceptable
     * @param defaultValue Supplies the default value
     */
    public OptionKey (final Class<T> typeClass, final String key, final Predicate<T> validator, final Supplier<T> defaultValue)
    {
        this.typeClass = typeClass;
        this.key = key;
        this.validator = validator;
        this.defaultValue = defaultValue;
    }

    /**
     * @see Options#getValue(OptionKey) Documentation
     */
    public T get ()
    {
        return InceptionMod.getInstance().getOptions().getValue(this);
    }

    /**
     * @see Options#setValue(OptionKey, Object) Documentation
     */
    public boolean set (T value)
    {
        return InceptionMod.getInstance().getOptions().setValue(this, value);
    }

    /**
     * Returns a new {@link OptionKeyBuilder} to build a new instance of the {@link OptionKey} class.
     */
    public static <T> OptionKeyBuilder<T> newInstance (Class<T> typeClass)
    {
        return new OptionKeyBuilder<>(typeClass);
    }
}
