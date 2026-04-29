# Design System: Editorial Heritage

This design system is a comprehensive framework for junior designers to execute a high-end, editorial experience that honors Korean heritage through modern, sustainable craftsmanship. It moves beyond standard UI kits by prioritizing tonal depth, intentional white space, and a "paper-on-stone" physical layering logic.

---

## 1. Creative North Star: "The Modern Hanok"
The guiding philosophy of this system is **The Modern Hanok**. Much like traditional Korean architecture, the digital experience should feel open, breathable, and deeply connected to nature, yet structured with sophisticated, modern precision. 

**Signature Style:**
*   **Asymmetric Balance:** Avoid perfectly centered, "boxy" grids. Use off-center headlines and overlapping imagery to create a sense of movement.
*   **Breathing Room:** White space is not "empty"; it is a functional element that directs the eye to artisanal details.
*   **Tactile Sophistication:** Use subtle background shifts instead of lines to create a sense of luxury and calm.

---

## 2. Color & Tonal Architecture
The palette is rooted in Earth and Sea—anchored by deep teals and soft, limestone-inspired neutrals.

### The "No-Line" Rule
**Explicit Instruction:** Do not use 1px solid borders to separate sections. Boundaries must be defined solely through background color shifts. For example:
*   A **Surface Container Low** (`#f5f3f2`) section sitting atop a **Surface** (`#fbf9f8`) background.
*   This creates a "seamless" transition that feels organic rather than mechanical.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers. Use the surface-container tiers to define depth:
*   **Surface Container Lowest (`#ffffff`):** Reserved for primary content cards or floating elements to provide maximum "pop."
*   **Surface (`#fbf9f8`):** The primary canvas (the "floor").
*   **Surface Container High (`#e9e8e7`):** Use for persistent sidebars or utility areas to ground the layout.

### The "Glass & Soul" Rule
To avoid a flat, "template" look:
*   **Glassmorphism:** For floating navigation or modals, use `surface` at 80% opacity with a `backdrop-blur(12px)`.
*   **Signature Gradients:** For primary CTAs, use a subtle linear gradient from **Primary** (`#00504a`) to **Primary Container** (`#006a63`) at 135 degrees. This adds "soul" and depth that a flat fill lacks.

---

## 3. Typography: The Editorial Voice
The typography pairing reflects a dialogue between heritage and modernity.

| Level | Token | Font Family | Size | Character |
| :--- | :--- | :--- | :--- | :--- |
| **Display** | `display-lg` | Plus Jakarta Sans | 3.5rem | Bold, authoritative, wide tracking (-0.02em). |
| **Headline**| `headline-md` | Plus Jakarta Sans | 1.75rem | Used for section headers. Always `on-surface`. |
| **Title**   | `title-lg` | Work Sans | 1.375rem | Semi-bold. Used for product names and card titles. |
| **Body**    | `body-lg` | Work Sans | 1.0rem | High readability, generous line-height (1.6). |
| **Label**   | `label-md` | Work Sans | 0.75rem | All-caps for metadata/categories. |

**Editorial Strategy:** Use `display-lg` sparingly. When a headline is used, give it 64px–80px of top margin to ensure the "Modern Hanok" sense of openness is maintained.

---

## 4. Elevation & Depth
In this system, elevation is a property of light and material, not math.

*   **Tonal Layering:** Depth is achieved by "stacking." A `surface-container-lowest` card on a `surface-container-low` background creates a soft, natural lift without shadows.
*   **Ambient Shadows:** If a floating effect is required (e.g., a header), use an extra-diffused shadow: `0 20px 40px rgba(0, 80, 74, 0.06)`. Note the tint: we use a tiny fraction of the **Primary** color instead of black to keep the shadow "warm."
*   **The "Ghost Border" Fallback:** If accessibility requires a border, use `outline-variant` at **15% opacity**. Never use 100% opaque borders.

---

## 5. Components

### Buttons: The Artisanal Anchor
*   **Primary:** Fill with the Primary-to-Primary-Container gradient. Radius: `md` (0.375rem). Use `on-primary` text.
*   **Secondary:** No fill. Use a "Ghost Border" (15% opacity `outline`) and `primary` text.
*   **Hover State:** Increase the `surface-tint` overlay by 8% to create a subtle glow rather than a color change.

### Cards & Lists
*   **The Card Rule:** Forbid divider lines. Separate items using `surface-container` background shifts or 32px of vertical white space.
*   **Imagery:** All images must have a subtle `xl` (0.75rem) corner radius. Use high-quality artisanal photography with warm, natural lighting.

### Input Fields
*   **Style:** Minimalist. Only a bottom border using `outline-variant` (50% opacity). When focused, the border transitions to `primary` and thickens to 2px.
*   **Background:** Use `surface-container-low` to provide a clear hit-area without "boxing" the user in.

---

## 6. Do's and Don'ts

### Do:
*   **Do** lean into asymmetry. Place a small `label-md` category tag 40px above a `headline-lg` to create an editorial cadence.
*   **Do** use `tertiary` (#70361d) for "Craftsmanship" callouts—it evokes the warmth of wood and traditional earthenware.
*   **Do** prioritize high-quality imagery over icons. Let the product be the hero.

### Don't:
*   **Don't** use standard "Material Design" shadows. They feel too "tech" for a heritage-focused brand.
*   **Don't** use pure black (#000000). Use `on-background` (#1b1c1b) for all text to maintain the soft, premium feel.
*   **Don't** crowd the edges. If a component feels tight, double the padding. This system lives and breathes through its margins.