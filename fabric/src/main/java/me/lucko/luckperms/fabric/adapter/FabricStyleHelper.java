package me.lucko.luckperms.fabric.adapter;

import net.kyori.text.Component;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FabricStyleHelper {
    public static void stylize(Text node, net.kyori.text.format.Style style) {
        node.styled(s -> {
            if (style.isEmpty()) {
                return;
            }

            if (style.clickEvent() != null) {
                ClickEvent.Action mcAction = getFabricAction(style.clickEvent().action());

                if (mcAction != null) {
                    String value = style.clickEvent().value();
                    ClickEvent event = new ClickEvent(mcAction, value);
                    s.setClickEvent(event);
                }
            }

            if (style.hoverEvent() != null) {
                net.kyori.text.event.HoverEvent hoverEvent = style.hoverEvent();
                HoverEvent.Action action = getFabricAction(hoverEvent.action());

                if (action != null) {
                    Component value = hoverEvent.value();
                    HoverEvent mcHoverEvent = new HoverEvent(action, FabricTextAdapter.adapt(value));
                    s.setHoverEvent(mcHoverEvent);
                }
            }

            if (style.color() != null) {
                TextColor color = style.color();
                Formatting formatting = getFabricFormatting(color);
                s.setColor(formatting);
            }

            if (!style.decorations().isEmpty()) {
                for (TextDecoration decoration : style.decorations()) {
                    applyDecoration(s, decoration);
                }
            }
        });
    }

    public static HoverEvent.Action getFabricAction(net.kyori.text.event.HoverEvent.Action action) {
        switch (action) {
            case SHOW_TEXT:
                return HoverEvent.Action.SHOW_TEXT;
            case SHOW_ITEM:
                return HoverEvent.Action.SHOW_ITEM;
            case SHOW_ENTITY:
                return HoverEvent.Action.SHOW_ENTITY;
        }

        return null;
    }

    public static void applyDecoration(net.minecraft.text.Style style, TextDecoration decoration) {
        switch (decoration) {
            case OBFUSCATED:
                style.setObfuscated(true);
            case BOLD:
                style.setBold(true);
            case STRIKETHROUGH:
                style.setStrikethrough(true);
            case UNDERLINED:
                style.setUnderline(true);
            case ITALIC:
                style.setItalic(true);
        }
    }

    public static Formatting getFabricFormatting(TextColor color) {
        switch (color) {
            case BLACK:
                return Formatting.BLACK;
            case DARK_BLUE:
                return Formatting.DARK_BLUE;
            case DARK_GREEN:
                return Formatting.DARK_GREEN;
            case DARK_AQUA:
                return Formatting.DARK_AQUA;
            case DARK_RED:
                return Formatting.DARK_RED;
            case DARK_PURPLE:
                return Formatting.DARK_PURPLE;
            case GOLD:
                return Formatting.GOLD;
            case GRAY:
                return Formatting.GRAY;
            case DARK_GRAY:
                return Formatting.DARK_GRAY;
            case BLUE:
                return Formatting.BLUE;
            case GREEN:
                return Formatting.GREEN;
            case AQUA:
                return Formatting.AQUA;
            case RED:
                return Formatting.RED;
            case LIGHT_PURPLE:
                return Formatting.LIGHT_PURPLE;
            case YELLOW:
                return Formatting.YELLOW;
            case WHITE:
                return Formatting.WHITE;
        }

        return Formatting.WHITE; // TODO: Default to white or exception or null.
    }

    public static ClickEvent.Action getFabricAction(net.kyori.text.event.ClickEvent.Action action) {
        switch (action) {
            case OPEN_URL:
                return ClickEvent.Action.OPEN_URL;
            case OPEN_FILE:
                return ClickEvent.Action.OPEN_FILE;
            case RUN_COMMAND:
                return ClickEvent.Action.RUN_COMMAND;
            case SUGGEST_COMMAND:
                return ClickEvent.Action.SUGGEST_COMMAND;
            case CHANGE_PAGE:
                return ClickEvent.Action.CHANGE_PAGE;
        }

        // 1.15 has another new ClickAction, COPY_TO_CLIPBOARD. Though this will break 1.14 builds do to a nonexistent enumaration, so we will have to abstract it based on MC version or ignore it till Kyori's text api updates to handle it.
        return null;
    }
}
