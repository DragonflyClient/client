package net.inceptioncloud.dragonfly.options;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Used to convert the {@link JsonElement} to the given type.
 *
 * @param <T> The target type
 */
public interface TypeConverter<T>
{
    /**
     * The gson object that can be used to convert an object ot a string.
     */
    Gson gson = Options.getGson();

    /**
     * The method that is used to convert the input Json-Element to the type.
     */
    T fromJson (JsonElement jsonElement);

    /**
     * The method that is used to convert the input type to a Json-Element.
     */
    JsonElement toJson (T type);
}
