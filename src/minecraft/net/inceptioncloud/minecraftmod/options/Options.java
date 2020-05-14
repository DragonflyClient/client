package net.inceptioncloud.minecraftmod.options;

import com.google.gson.*;
import net.inceptioncloud.minecraftmod.Dragonfly;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

import java.io.*;

/**
 * This class manages the reading and writing of the options to the specific file.
 */
public class Options
{
    /**
     * The file in which the options are saved.
     */
    public static final File OPTIONS_FILE = new File("inceptioncloud/options.json");

    /**
     * The Gson instance that allows the (de-)serialization of objects.
     */
    private final Gson gson;

    /**
     * The last read content (via {@link #contentUpdate()}) in JSON-Format.
     */
    private JsonObject jsonObject;

    /**
     * Initial Constructor that updates the content when called.
     */
    public Options ()
    {
        Dragonfly.getEventBus().register(new OptionSaveSubscriber());
        gson = new GsonBuilder().setPrettyPrinting().create();
        contentUpdate();
    }

    /**
     * Reads the content from the options file and stores it.
     */
    public void contentUpdate ()
    {
        try {

            LogManager.getLogger().info("Loading Settings...");

            if (!OPTIONS_FILE.exists()) jsonObject = new JsonObject();
            else jsonObject = new JsonParser().parse(new FileReader(OPTIONS_FILE)).getAsJsonObject();

            LogManager.getLogger().info(jsonObject);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the current (and potential modified) {@link #jsonObject} to the {@link #OPTIONS_FILE}.
     */
    public void contentSave ()
    {
        try {

            if (!OPTIONS_FILE.exists() && !OPTIONS_FILE.createNewFile())
                throw new IOException("Unable to create options.json file!");

            FileWriter fw = new FileWriter(OPTIONS_FILE);
            fw.write(gson.toJson(jsonObject));
            fw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the value for the given Option Key.
     * If the value isn't from the right type or if the validation fails, the default value will be
     * used and also saved in the {@link #jsonObject}.
     *
     * @param optionKey The key for which the value is requested
     * @param <T>       The type of the value
     *
     * @return The saved value or the default value
     */
    public <T> T getValue (OptionKey<T> optionKey)
    {
        Validate.notNull(optionKey, "The key for the options value cannot be null!");

        if (jsonObject.has(optionKey.getKey())) {

            try {
                JsonElement jsonElement = jsonObject.get(optionKey.getKey());
                T value = gson.fromJson(jsonElement, optionKey.getTypeClass());

                if (optionKey.getValidator().test(value))
                    return value;
            } catch (JsonSyntaxException exception) {
                if (exception.getCause().getClass().getSimpleName().equals("IllegalStateException")
                    || exception.getCause().getClass().getSimpleName().equals("NumberFormatException")) {
                    LogManager.getLogger().info("Noticed migrated value type for " + optionKey.getKey() + ". Default Value of " + optionKey.getDefaultValue().get() + " restored!");
                } else {
                    exception.printStackTrace();
                }
            }
        }

        T value = optionKey.getDefaultValue().get();
        setValue(optionKey, value);
        return value;
    }

    /**
     * Puts the value for the option key in the {@link #jsonObject}.
     *
     * @param optionKey The key for which the value is set
     * @param value     The value to be set
     * @param <T>       The type of the value
     *
     * @return False if the value is not valid, otherwise true.
     */
    public <T> boolean setValue (OptionKey<T> optionKey, T value)
    {
        if (!optionKey.getValidator().test(value)) {
            LogManager.getLogger().error("Failed to set option value {} for key {} (validation failed!)", value, optionKey.getKey());
            return false;
        }

        jsonObject.add(optionKey.getKey(), gson.toJsonTree(value));
        return true;
    }

    /**
     * Gson-Getter
     */
    public Gson getGson ()
    {
        return this.gson;
    }
}
