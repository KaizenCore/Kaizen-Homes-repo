# Gradient Text Features

KaizenHomes includes a comprehensive gradient text system that makes the plugin visually stunning with RGB color transitions.

## Gradient Utility (GradientUtil)

Located in `utils/GradientUtil.java`, this utility provides multiple ways to create beautiful gradient text.

### Basic Methods

#### Simple Two-Color Gradient
```java
GradientUtil.gradient(String text, String startHex, String endHex)
GradientUtil.gradient(String text, String startHex, String endHex, boolean bold)
```
Example: `GradientUtil.gradient("Welcome Home", "#FF69B4", "#4169E1")`

#### Rainbow Gradient
```java
GradientUtil.rainbow(String text)
GradientUtil.rainbow(String text, boolean bold)
```
Creates a smooth rainbow effect across the text using HSB color space.

#### Multi-Color Gradient
```java
GradientUtil.multiGradient(String text, String... hexColors)
```
Smoothly transitions through multiple colors.
Example: `GradientUtil.multiGradient("Fire", "#FF0000", "#FF7F00", "#FFFF00")`

### Preset Gradients

The `GradientUtil.Presets` class provides ready-to-use gradient styles:

| Preset | Colors | Description |
|--------|--------|-------------|
| `kaizen(text)` | #FF6B9D → #C44569 | Pink to dark pink (Kaizen brand) |
| `fire(text)` | Red → Orange → Yellow | Fiery gradient |
| `ocean(text)` | #00D4FF → #0066CC | Cyan to blue |
| `nature(text)` | #00FF00 → #006400 | Lime to dark green |
| `sunset(text)` | Red → Orange → Gold | Sunset colors |
| `purple(text)` | #9D50BB → #6E48AA | Purple gradient |
| `gold(text)` | #FFD700 → #FFA500 | Gold to orange |

## Where Gradients Are Used

### 1. Plugin Prefix
All messages use the Kaizen gradient:
```
Kaizen » Message text here
```

### 2. Home Names in GUI
- Home menu GUI: Gold to orange gradient
- Public homes: Cyan to blue gradient
- Default home marker with gradient

### 3. GUI Buttons
- Teleport button: Gold gradient
- Delete button: Red gradient
- Privacy button: Cyan gradient

### 4. Messages
- Home names in success messages use green gradients
- Teleport messages use cyan gradients
- Action bars use gold gradients

### 5. Titles
- Welcome titles use Kaizen gradient
- Subtitles can use gold gradient

## Testing Gradients

Use the `/gradienttest` command (aliases: `/gtest`, `/gradients`) to see all gradient examples in-game.

This command displays:
- All preset gradients
- Rainbow gradient
- Multi-color gradient examples
- Custom gradient examples

## Technical Details

### How It Works
1. The gradient is calculated by interpolating RGB values between colors
2. Each character gets a color based on its position in the text
3. Multi-color gradients divide the text into segments
4. Rainbow uses HSB color space for smooth color transitions

### Performance
- Gradients are calculated on-the-fly (no caching needed)
- Very lightweight - only string manipulation and color math
- No performance impact even with many gradient messages

### Compatibility
- Requires Paper API 1.21+ (uses Adventure API)
- Works on all modern Minecraft clients that support RGB colors
- Older clients will see the text but without gradients

## Usage in Your Code

```java
// Simple gradient
Component message = GradientUtil.gradient("Hello World", "#FF0000", "#0000FF");

// Using presets
Component title = GradientUtil.Presets.kaizen("Welcome!");

// Rainbow text
Component rainbow = GradientUtil.rainbow("Rainbow Text");

// Multi-color
Component multi = GradientUtil.multiGradient("Colorful", "#FF0000", "#00FF00", "#0000FF");
```

## Customization

You can create your own preset in `GradientUtil.Presets`:

```java
public static Component myCustomPreset(String text) {
    return gradient(text, "#START_HEX", "#END_HEX");
}
```

Or use multi-color for more complex gradients:
```java
return multiGradient(text, "#COLOR1", "#COLOR2", "#COLOR3");
```
