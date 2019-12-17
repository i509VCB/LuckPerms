package me.lucko.luckperms.fabric.adapter;

import net.kyori.text.*;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

/**
 * There is no implementation of Kyori's text to convert to Fabric's types. So we take it to our own hands.
 */
public class FabricTextAdapter {
    /**
     * Converts a Component into Text.
     * @param component The component to convert.
     * @return Text of the same type as the component, or an empty Text object if not present.
     */
    public static Text adapt(Component component) {
        final Text root = new LiteralText("");
        if (component instanceof TextComponent) {
            return adaptLiteral((TextComponent) component);
        } else if (component instanceof TranslatableComponent) {
            return adaptTranslatable((TranslatableComponent) component);
        } else if (component instanceof SelectorComponent) {
            return adaptSelector((SelectorComponent) component);
        } else if (component instanceof NbtComponent) {
            return adaptNbt((NbtComponent) component);
        } else if (component instanceof KeybindComponent) {
            return adaptKeybind((KeybindComponent) component);
        } else if (component instanceof ScoreComponent) {
            return adaptScore((ScoreComponent) component);
        }

        return root;
    }

    private static Text adaptScore(ScoreComponent component) {
        List<Text> children = new ArrayList<>();
        if (!component.children().isEmpty()) {
            for (Component comp : component.children()) {
                children.add(adapt(comp));
            }
        }

        ScoreText node = new ScoreText(component.name(), component.objective());

        if (component.value() != null) {
            node.setScore(component.value());
        }

        if (component.hasStyling()) {
            FabricStyleHelper.stylize(node, component.style());
        }

        appendChildren(node, children);
        return node;
    }

    private static Text adaptKeybind(KeybindComponent component) {
        List<Text> children = new ArrayList<>();
        if (!component.children().isEmpty()) {
            for (Component comp : component.children()) {
                children.add(adapt(comp));
            }
        }

        KeybindText node = new KeybindText(component.keybind());

        if (component.hasStyling()) {
            FabricStyleHelper.stylize(node, component.style());
        }

        appendChildren(node, children);
        return node;
    }

    private static Text adaptNbt(NbtComponent component) {
        List<Text> children = new ArrayList<>();
        if (!component.children().isEmpty()) {
            for (Component comp : component.children()) {
                children.add(adapt(comp));
            }
        }

        NbtText node;

        if (component instanceof BlockNbtComponent) {
            BlockNbtComponent block = (BlockNbtComponent) component;
            node = new NbtText.BlockNbtText(block.nbtPath(), block.interpret(), posAsString(block.pos()));
        } else {
            EntityNbtComponent entity = (EntityNbtComponent) component;
            node = new NbtText.EntityNbtText(entity.nbtPath(), entity.interpret(), entity.selector());
        }

        if (component.hasStyling()) {
            FabricStyleHelper.stylize(node, component.style());
        }

        appendChildren(node, children);
        return node;
    }

    private static Text adaptSelector(SelectorComponent component) {
        List<Text> children = new ArrayList<>();
        if (!component.children().isEmpty()) {
            for (Component comp : component.children()) {
                children.add(adapt(comp));
            }
        }

        String pattern = component.pattern();

        SelectorText node = new SelectorText(pattern);

        if (component.hasStyling()) {
            FabricStyleHelper.stylize(node, component.style());
        }

        appendChildren(node, children);
        return node;
    }

    private static TranslatableText adaptTranslatable(TranslatableComponent component) {
        List<Text> children = new ArrayList<>();
        if (!component.children().isEmpty()) {
            for (Component comp : component.children()) {
                children.add(adapt(comp));
            }
        }

        String translationKey = component.key();
        List<Component> args = component.args();

        // TODO: Process args

        TranslatableText node = new TranslatableText(translationKey);

        if (component.hasStyling()) {
            FabricStyleHelper.stylize(node, component.style());
        }

        appendChildren(node, children);
        return node;
    }

    private static LiteralText adaptLiteral(TextComponent component) {
        List<Text> children = new ArrayList<>();
        if (!component.children().isEmpty()) {
            for (Component comp : component.children()) {
                children.add(adapt(comp));
            }
        }

        LiteralText node = new LiteralText(component.content());

        if (component.hasStyling()) {
            FabricStyleHelper.stylize(node, component.style());
        }

        appendChildren(node, children);
        return node;
    }

    public static Text adaptString(String s) {
        return new LiteralText(s);
    }

    private static void appendChildren(Text node, List<Text> children) {
        for (Text child : children) {
            node.append(child);
        }
    }

    private static boolean isRelative(BlockNbtComponent.WorldPos.Coordinate coordinate) {
        return coordinate.type() == BlockNbtComponent.WorldPos.Coordinate.Type.RELATIVE;
    }

    private static String posAsString(BlockNbtComponent.Pos pos) {
        if (pos instanceof BlockNbtComponent.LocalPos) { // Basically the ^ ^ ^ position argument
            StringBuilder builder = new StringBuilder();

            builder.append("^");
            builder.append(((BlockNbtComponent.LocalPos) pos).left());
            builder.append(" ^");
            builder.append(((BlockNbtComponent.LocalPos) pos).up());
            builder.append(" ^").append(((BlockNbtComponent.LocalPos) pos).forwards());

            return builder.toString();
        } else {
            BlockNbtComponent.WorldPos.Coordinate x = ((BlockNbtComponent.WorldPos) pos).x();
            BlockNbtComponent.WorldPos.Coordinate y = ((BlockNbtComponent.WorldPos) pos).y();
            BlockNbtComponent.WorldPos.Coordinate z = ((BlockNbtComponent.WorldPos) pos).z();

            StringBuilder builder = new StringBuilder();

            if (isRelative(x)) { // For example this is ~ or ~-1, ~1000
                builder.append("~").append(x.value());
            } else {
                builder.append(x.value());
            }
            builder.append(" ");

            if (isRelative(y)) { // For example this is ~ or ~-1, ~1000
                builder.append("~").append(y.value());
            } else {
                builder.append(y.value());
            }
            builder.append(" ");

            if (isRelative(z)) { // For example this is ~ or ~-1, ~1000
                builder.append("~").append(z.value());
            } else {
                builder.append(z.value());
            }

            return builder.toString();
        }
    }
}
