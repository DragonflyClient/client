package net.inceptioncloud.minecraftmod.options;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class OptionKeyBuilder<T>
{
    private String key;
    private Predicate<T> validator = obj -> true;
    private Supplier<T> defaultValue = () -> null;
    private Class<T> typeClass;

    OptionKeyBuilder (Class<T> typeClass)
    {
        this.typeClass = typeClass;
    }

    public OptionKeyBuilder<T> key (String key)
    {
        this.key = key;
        return this;
    }

    public OptionKeyBuilder<T> validator (Predicate<T> validator)
    {
        this.validator = validator;
        return this;
    }

    public OptionKeyBuilder<T> defaultValue (Supplier<T> defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    public OptionKeyBuilder<T> defaultValue (T defaultValue)
    {
        this.defaultValue = () -> defaultValue;
        return this;
    }

    public OptionKey<T> build ()
    {
        return new OptionKey<>(typeClass, key, validator, defaultValue);
    }
}
