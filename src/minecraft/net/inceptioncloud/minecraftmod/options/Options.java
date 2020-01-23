package net.inceptioncloud.minecraftmod.options;

import net.inceptioncloud.minecraftmod.InceptionMod;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * This class manages the reading and writing of the options to the specific file.
 */
@SuppressWarnings ( "unchecked" )
public class Options
{
    /**
     * The file in which the options are saved.
     */
    public static final File OPTIONS_FILE = new File("inceptioncloud/options.json");

    /**
     * The last read content (via {@link #contentUpdate()}) in JSON-Format.
     */
    private JSONObject jsonObject;

    /**
     * Initial Constructor that updates the content when called.
     */
    public Options ()
    {
        InceptionMod.getInstance().getEventBus().register(new OptionSaveSubscriber());
        contentUpdate();
    }

    /**
     * Reads the content from the options file and stores it.
     */
    public void contentUpdate ()
    {
        try {

            System.out.println("Loading Settings...");

            if (!OPTIONS_FILE.exists()) jsonObject = new JSONObject();
            else jsonObject = ( JSONObject ) new JSONParser().parse(new FileReader(OPTIONS_FILE));

            System.out.println(jsonObject);

        } catch (IOException | ParseException e) {
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

            FileWriter file = new FileWriter(OPTIONS_FILE);
            file.write(jsonObject.toJSONString());
            file.flush();

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

        Object object = jsonObject.get(optionKey.getKey());
        T value;

        try {
            value = ( T ) object;

            if (value != null && optionKey.getValidator().test(value))
                return value;
        } catch (ClassCastException ignored) {
        }

        value = optionKey.getDefaultValue().get();
        jsonObject.put(optionKey.getKey(), value);
        LogManager.getLogger().warn("---> The default value for {} has been restored: {}", optionKey.getKey(), value);

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

        jsonObject.put(optionKey.getKey(), value);
        return true;
    }
}
