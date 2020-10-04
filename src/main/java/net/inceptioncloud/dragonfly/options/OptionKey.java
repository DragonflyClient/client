package net.inceptioncloud.dragonfly.options;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a value that is set in the options file for a specific key.
 *
 * @param <T> The type of the value
 */
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
     *
     * @param key          The key in String-Format under which the value is saved
     * @param validator    Validates whether the current value is acceptable
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
     * Returns a new {@link OptionKeyBuilder} to build a new instance of the {@link OptionKey} class.
     */
    public static <T> OptionKeyBuilder<T> newInstance (Class<T> typeClass)
    {
        return new OptionKeyBuilder<>(typeClass);
    }

    /**
     * @see Options#getValue(OptionKey) Documentation
     */
    public T get ()
    {
        return Options.getValue(this);
    }

    /**
     * @see Options#setValue(OptionKey, Object) Documentation
     */
    public boolean set (T value)
    {
        return Options.setValue(this, value);
    }

    public Class<T> getTypeClass ()
    {
        return typeClass;
    }

    public String getKey ()
    {
        return key;
    }

    public Predicate<T> getValidator ()
    {
        return validator;
    }

    public Supplier<T> getDefaultValue ()
    {
        return defaultValue;
    }
}