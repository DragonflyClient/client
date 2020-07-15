package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent
{
    private final ClickEvent.Action action;
    private final String value;
    private final Runnable callback;

    public ClickEvent(ClickEvent.Action theAction, String theValue)
    {
        this.action = theAction;
        this.value = theValue;
        this.callback = null;
    }

    public ClickEvent(Runnable callback) {
        this.callback = callback;
        this.action = Action.RUN_CALLBACK;
        this.value = null;
    }

    /**
     * Gets the action to perform when this event is raised.
     */
    public ClickEvent.Action getAction()
    {
        return this.action;
    }

    /**
     * Gets the value to perform the action on when this event is raised.  For example, if the action is "open URL",
     * this would be the URL to open.
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Getter Method for {@link #callback}
     */
    public Runnable getCallback() {
        return callback;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            ClickEvent clickevent = (ClickEvent)p_equals_1_;

            if (this.action != clickevent.action)
            {
                return false;
            }
            else
            {
                if (this.value != null)
                {
                    return this.value.equals(clickevent.value);
                }
                else return clickevent.value == null;
            }
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode()
    {
        int i = this.action.hashCode();
        i = 31 * i + (this.value != null ? this.value.hashCode() : 0);
        return i;
    }

    public static enum Action
    {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        TWITCH_USER_INFO("twitch_user_info", false),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true),
        RUN_CALLBACK("run_callback", false);

        private static final Map<String, ClickEvent.Action> nameMapping = Maps.<String, ClickEvent.Action>newHashMap();
        private final boolean allowedInChat;
        private final String canonicalName;

        private Action(String canonicalNameIn, boolean allowedInChatIn)
        {
            this.canonicalName = canonicalNameIn;
            this.allowedInChat = allowedInChatIn;
        }

        public boolean shouldAllowInChat()
        {
            return this.allowedInChat;
        }

        public String getCanonicalName()
        {
            return this.canonicalName;
        }

        public static ClickEvent.Action getValueByCanonicalName(String canonicalNameIn)
        {
            return nameMapping.get(canonicalNameIn);
        }

        static {
            for (ClickEvent.Action clickevent$action : values())
            {
                nameMapping.put(clickevent$action.getCanonicalName(), clickevent$action);
            }
        }
    }
}
