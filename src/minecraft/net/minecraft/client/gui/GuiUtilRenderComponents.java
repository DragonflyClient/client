package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;

import net.inceptioncloud.minecraftmod.render.font.IFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GuiUtilRenderComponents
{
    /**
     * Remove the formatting colors from the text if it was configured in the chat settings and it
     * the color isn't forced.
     *
     * @param text The text
     * @param forceColor True to always keep the color, false to use the value from the chat settings
     * @return The result
     */
    public static String removeTextColorsIfConfigured (String text, boolean forceColor)
    {
        return !forceColor && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(text) : text;
    }

    public static List<IChatComponent> splitText (IChatComponent original, int maxTextLenght, IFontRenderer ignored /* NOTE: This parameter is unnecessary because the Minecraft Font Renderer is used to split the text */,
                                                  boolean trimSpace, boolean forceTextColor)
    {
        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        int i = 0;
        IChatComponent currentComponent = new ChatComponentText("");
        List<IChatComponent> newList = Lists.newArrayList();
        List<IChatComponent> listWithOriginal = Lists.newArrayList(original);

        for (int j = 0 ; j < listWithOriginal.size(); ++j)
        {
            IChatComponent ichatcomponent1 = listWithOriginal.get(j);
            String s = ichatcomponent1.getUnformattedTextForChat();
            boolean flag = false;

            if (s.contains("\n"))
            {
                int k = s.indexOf(10);
                String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                ChatComponentText chatcomponenttext = new ChatComponentText(s1);
                chatcomponenttext.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                listWithOriginal.add(j + 1, chatcomponenttext);
                flag = true;
            }

            String s4 = removeTextColorsIfConfigured(ichatcomponent1.getChatStyle().getFormattingCode() + s, forceTextColor);
            String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = fontRenderer.getStringWidth(s5);
            ChatComponentText chatcomponenttext1 = new ChatComponentText(s5);
            chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());

            if (i + i1 > maxTextLenght)
            {
                String s2 = fontRenderer.trimStringToWidth(s4, maxTextLenght - i);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null && s3.length() > 0)
                {
                    int l = s2.lastIndexOf(" ");

                    if (l >= 0 && fontRenderer.getStringWidth(s4.substring(0, l)) > 0)
                    {
                        s2 = s4.substring(0, l);

                        if (trimSpace)
                        {
                            ++l;
                        }

                        s3 = s4.substring(l);
                    }
                    else if (i > 0 && !s4.contains(" "))
                    {
                        s2 = "";
                        s3 = s4;
                    }

                    ChatComponentText chatcomponenttext2 = new ChatComponentText(s3);
                    chatcomponenttext2.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                    listWithOriginal.add(j + 1, chatcomponenttext2);
                }

                i1 = fontRenderer.getStringWidth(s2);
                chatcomponenttext1 = new ChatComponentText(s2);
                chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                flag = true;
            }

            if (i + i1 <= maxTextLenght)
            {
                i += i1;
                currentComponent.appendSibling(chatcomponenttext1);
            }
            else
            {
                flag = true;
            }

            if (flag)
            {
                newList.add(currentComponent);
                i = 0;
                currentComponent = new ChatComponentText("");
            }
        }

        newList.add(currentComponent);
        return newList;
    }
}
