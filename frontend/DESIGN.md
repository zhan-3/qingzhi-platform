---
name: 青知共享平台
description: 开放共享 · 知识同行
colors:
  primary: "#409EFF"
  success: "#67C23A"
  warning: "#E6A23C"
  danger: "#F56C6C"
  sidebar-bg: "#304156"
  sidebar-active: "#263445"
  sidebar-text: "#bfcbd9"
  main-bg: "#f0f2f5"
  surface: "#ffffff"
typography:
  body:
    fontFamily: "system-ui, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif"
    fontSize: "14px"
    lineHeight: 1.5
  heading:
    fontFamily: "system-ui, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif"
    fontWeight: 600
rounded:
  sm: "4px"
  md: "8px"
  lg: "12px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "16px"
  lg: "20px"
  xl: "24px"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.surface}"
    rounded: "{rounded.sm}"
  sidebar-menu:
    backgroundColor: "{colors.sidebar-bg}"
    textColor: "{colors.sidebar-text}"
---

# Design System: 青知共享平台

## Overview

**Creative North Star: "Youth Knowledge"**

青知共享平台's visual identity is defined by restraint. The interface does not call attention to itself — it stays out of the way so students and teachers can focus on finding and sharing course materials. Clean, functional, and predictable, the system uses Element Plus's default component language as its foundation, overlaid with a dark navigation spine that anchors the layout and gives the app a quiet sense of structure.

The palette follows the semantic conventions users expect from an admin tool: blue for primary actions, green for success states, orange for warnings, red for destructive operations. Nothing is decorative; every color choice communicates state. The dark sidebar (`#304156`) is the defining architectural gesture — it frames the experience with a professional, library-like calm while the content area stays bright and airy (`#f0f2f5`).

**Key Characteristics:**
- Library-calm: quiet visual hierarchy, low contrast ratios, nothing flashing or competing
- Default-professional: Element Plus components used as-designed, no gratuitous customization
- Functional color: every hue maps to a semantic state, not an aesthetic preference
- Skeleton-and-surface: a dark navigation frame around white content cards on a light gray ground

## Colors

The palette is pragmatic and semantic. Blue leads; green, orange, and red follow as state indicators. The dark sidebar is the only non-semantic use of color — an architectural choice that grounds the navigation.

### Primary
- **Scholar Blue** (#409EFF): Primary actions, active navigation items, links. The single accent color — used sparingly for interactive elements only.

### Feedback
- **Proven Green** (#67C23A): Approved/success states, positive tags.
- **Alert Amber** (#E6A23C): Pending states, warnings, review-needed indicators.
- **Error Red** (#F56C6C): Rejected states, destructive actions, deletion warnings.

### Neutral
- **Ink Navy** (#304156): Sidebar background. The structural dark anchor of the layout.
- **Ink Deep** (#263445): Active/hover state of sidebar menu items. One step darker than Ink Navy.
- **Mist Silver** (#bfcbd9): Sidebar menu text at rest. Low contrast by design — recedes until needed.
- **Cloud Gray** (#f0f2f5): Main content area background. Provides visual separation from cards.
- **White** (#ffffff): Card surfaces, header background, dialog backgrounds.

### Named Rules

**The Functional Color Rule.** Every non-neutral color in the system communicates a product state. If a color isn't telling the user something (success, warning, error, actionable), it shouldn't be on screen. Decorative color is reserved for the sidebar architecture alone.

**The Skeleton Rule.** The app has two visual planes: a dark sidebar (navigation) and a light content area (tasks). This schism is the defining layout gesture. Never introduce a third background color zone.

## Typography

**Body Font:** system-ui, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif
**Display Font:** Inherits body stack (no dedicated display face)
**Label/Mono Font:** system-ui (no dedicated mono face)

**Character:** Utilitarian and familiar. The system font stack ensures native-feeling rendering on every platform with zero load cost. There is no typographic personality beyond clarity.

### Hierarchy
- **Title / Headline** (600 weight, 16–18px, 1.4 line-height): Section headers, card titles, dialog titles.
- **Body** (400 weight, 14px, 1.5 line-height): Tables, form labels, descriptions, general reading.
- **Label** (400 weight, 12px–13px, 1.4 line-height): Table cell content, helper text, tag text, metadata.

## Layout

The layout follows a fixed sidebar / fluid content model.

- **Sidebar:** Fixed 200px width, full viewport height. Navigation sits here permanently — no collapse toggle in the current implementation.
- **Header:** Full-width bar above the content area, 60px height. Contains breadcrumbs (left) and user dropdown (right).
- **Content:** Fluid area with 20px padding, `#f0f2f5` background. Cards and tables sit on white surfaces inside this zone.
- **Cards:** Standard Element Plus card with `shadow="hover"` — flat at rest, subtle shadow on hover. Internal padding is Element Plus default (~16–20px).

The content area uses Element Plus's grid (`el-row` / `el-col`) for dashboard stat cards (4-column layout at `span:6`). Tables fill their container width with no fixed maximum.

Responsive behavior is minimal: the layout is desktop-first and does not adapt to mobile viewports.

## Elevation & Depth

The system is essentially flat. Depth is conveyed through tonal layering (dark sidebar → gray content bg → white cards) rather than through shadows.

- **Cards at rest:** No shadow. Flat surface on the content background.
- **Cards on hover (`shadow="hover"`):** Element Plus default hover shadow (`0 2px 12px 0 rgba(0,0,0,0.1)`). A subtle lift to indicate interactivity.
- **Dialogs & Dropdowns:** Element Plus default overlay shadow. No custom elevation vocabulary.

### Named Rules

**The Flat-By-Default Rule.** Surfaces do not cast shadows at rest. Shadows appear only as a direct response to state (hover, open). This keeps the visual field clean and the information hierarchy driven by tonal contrast, not shadow depth.

## Shapes

- **Interactive controls (buttons, inputs, tags, chips):** 4px radius (`rounded.sm`). Crisp, unobtrusive corners.
- **Cards:** 4px radius (Element Plus default card shape). No border; separation comes from the `#f0f2f5` background.
- **Dialogs:** 12px radius (`rounded.lg`). The one deliberate softening — dialogs are modal interruptions, and the larger radius makes them feel less harsh.
- **Borders:** 1px solid `#e6e6e6` (header, table cells, dialog dividers). Fine and unobtrusive.

Form elements use Element Plus default border styling (1px solid `#dcdfe6` at rest, `#409EFF` on focus).

## Components

### Buttons
- **Shape:** 4px radius (`rounded.sm`). Standard Element Plus sizing.
- **Primary:** Scholar Blue (`#409EFF`) background, white text. Standard horizontal padding (~16–20px).
- **Hover / Focus:** Element Plus default — slightly darker blue on hover, focus ring behavior inherited from the library.
- **Plain / Link variants:** Stock Element Plus (outlined `#409EFF`, link-style `#409EFF` text).
- **Danger:** Error Red (`#F56C6C`) for destructive actions only.

### Cards / Containers
- **Corner Style:** 4px radius.
- **Background:** White (`#ffffff`).
- **Shadow Strategy:** None at rest; hover shadow per *Elevation & Depth* section. Cards that never lift (e.g., table containers) remain flat.
- **Border:** None.
- **Internal Padding:** Element Plus default (~16–20px).

### Tables
- **Style:** Full-width, striped rows (`stripe` prop), bordered cells (Element Plus default). Header row has bold text and a subtle bottom border.
- **Empty state:** Element Plus default empty text ("暂无资源", "暂无数据").

### Inputs / Fields
- **Style:** Stock Element Plus — 1px `#dcdfe6` border, 4px radius, white background.
- **Focus:** Scholar Blue (`#409EFF`) border, light blue box-shadow (Element Plus default).
- **Error:** Element Plus default red border treatment via form validation.
- **Disabled:** Element Plus default gray background.

### Navigation (Sidebar)
- **Style:** Full-height `el-menu` on Ink Navy (`#304156`) background, no right border.
- **Typography:** Mist Silver (`#bfcbd9`), 14px. Menu item icons precede the label with ~8px gap.
- **Active state:** Scholar Blue (`#409EFF`) text, Ink Deep (`#263445`) background.
- **Logo area:** 60px tall, white bold 18px text, centered, with a `1px solid #263445` bottom separator.

### Dialogs
- **Shape:** 12px radius (`rounded.lg`) — the softest corner in the system.
- **Header:** Centered title text, bottom border (`1px solid #f0f0f0`).
- **Footer:** Centered action buttons with 12px gap between them.
- **Overlay:** Element Plus default semi-transparent black backdrop.

## Do's and Don'ts

### Do:
- **Do** use Scholar Blue for all primary actions — there should be exactly one per view.
- **Do** use the semantic feedback colors (green/amber/red) only for their intended states.
- **Do** keep dialogs as the only element with rounded-lg corners — the contrast signals their modal nature.
- **Do** let white cards breathe on the gray content background — the tonal layer hierarchy is the depth system.

### Don't:
- **Don't** add decorative color. If it doesn't communicate state or signify an action, leave it neutral.
- **Don't** introduce a third background zone beyond dark sidebar / gray content / white cards.
- **Don't** override Element Plus component shapes beyond what's in this document — consistency with the library is a feature, not a limitation.
- **Don't** add shadows to cards at rest. Flat is the default; hover is the exception.
