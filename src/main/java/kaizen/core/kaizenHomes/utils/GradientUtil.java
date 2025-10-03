package kaizen.core.kaizenHomes.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GradientUtil {

    /**
     * Create a gradient text component
     */
    public static Component gradient(String text, String startHex, String endHex) {
        return gradient(text, startHex, endHex, false);
    }

    /**
     * Create a gradient text component with optional bold
     */
    public static Component gradient(String text, String startHex, String endHex, boolean bold) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        Color startColor = Color.decode(startHex);
        Color endColor = Color.decode(endHex);

        Component result = Component.empty();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            // Calculate interpolation factor
            float factor = (float) i / (float) (length - 1);
            if (length == 1) factor = 0;

            // Interpolate RGB values
            int red = (int) (startColor.getRed() + factor * (endColor.getRed() - startColor.getRed()));
            int green = (int) (startColor.getGreen() + factor * (endColor.getGreen() - startColor.getGreen()));
            int blue = (int) (startColor.getBlue() + factor * (endColor.getBlue() - startColor.getBlue()));

            TextColor color = TextColor.color(red, green, blue);
            Component charComponent = Component.text(c).color(color);

            if (bold) {
                charComponent = charComponent.decoration(TextDecoration.BOLD, true);
            }

            result = result.append(charComponent);
        }

        return result;
    }

    /**
     * Create a rainbow gradient text
     */
    public static Component rainbow(String text) {
        return rainbow(text, false);
    }

    /**
     * Create a rainbow gradient text with optional bold
     */
    public static Component rainbow(String text, boolean bold) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        Component result = Component.empty();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            // Calculate hue based on position (0-360 degrees)
            float hue = (float) i / (float) length;
            Color color = Color.getHSBColor(hue, 0.8f, 1.0f);

            TextColor textColor = TextColor.color(color.getRed(), color.getGreen(), color.getBlue());
            Component charComponent = Component.text(c).color(textColor);

            if (bold) {
                charComponent = charComponent.decoration(TextDecoration.BOLD, true);
            }

            result = result.append(charComponent);
        }

        return result;
    }

    /**
     * Create a multi-color gradient
     */
    public static Component multiGradient(String text, String... hexColors) {
        if (text == null || text.isEmpty() || hexColors.length < 2) {
            return Component.text(text);
        }

        List<Color> colors = new ArrayList<>();
        for (String hex : hexColors) {
            colors.add(Color.decode(hex));
        }

        Component result = Component.empty();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            float position = (float) i / (float) (length - 1);
            if (length == 1) position = 0;

            // Determine which two colors to interpolate between
            float segmentSize = 1.0f / (colors.size() - 1);
            int segmentIndex = Math.min((int) (position / segmentSize), colors.size() - 2);
            float segmentPosition = (position - (segmentIndex * segmentSize)) / segmentSize;

            Color startColor = colors.get(segmentIndex);
            Color endColor = colors.get(segmentIndex + 1);

            // Interpolate
            int red = (int) (startColor.getRed() + segmentPosition * (endColor.getRed() - startColor.getRed()));
            int green = (int) (startColor.getGreen() + segmentPosition * (endColor.getGreen() - startColor.getGreen()));
            int blue = (int) (startColor.getBlue() + segmentPosition * (endColor.getBlue() - startColor.getBlue()));

            TextColor color = TextColor.color(red, green, blue);
            result = result.append(Component.text(c).color(color));
        }

        return result;
    }

    // Preset gradients for common use cases
    public static class Presets {
        public static Component kaizen(String text) {
            return gradient(text, "#FF6B9D", "#C44569", true); // Pink to dark pink
        }

        public static Component fire(String text) {
            return multiGradient(text, "#FF0000", "#FF7F00", "#FFFF00"); // Red -> Orange -> Yellow
        }

        public static Component ocean(String text) {
            return gradient(text, "#00D4FF", "#0066CC"); // Cyan to blue
        }

        public static Component nature(String text) {
            return gradient(text, "#00FF00", "#006400"); // Lime to dark green
        }

        public static Component sunset(String text) {
            return multiGradient(text, "#FF6B6B", "#FFA500", "#FFD700"); // Red -> Orange -> Gold
        }

        public static Component purple(String text) {
            return gradient(text, "#9D50BB", "#6E48AA"); // Purple gradient
        }

        public static Component gold(String text) {
            return gradient(text, "#FFD700", "#FFA500"); // Gold to orange
        }
    }
}
