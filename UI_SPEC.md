# Time Fomo UI Specification

This document describes the visual design of the Time Fomo single page web application. Use this as a reference for implementing the same look and feel on other platforms.

## General Style

- **Font**: `Inter` from Google Fonts. Font weight varies from 300 to 700.
- **Theme**: Dark theme with subtle gradients.
- **Background**: A diagonal gradient from `#1A202C` (slate 900) to `#2D3748` (slate 800).
- **Text Color**: Primary text is `#F1F5F9` (slate-100). Secondary text is `#94A3B8` (slate-400).
- **Accent Colors**: Indigo and sky/turquoise hues are used for highlights and interaction feedback.
- **Corner Radius**: Components use rounded corners (`0.375rem`–`1rem`).

## Navigation Bar

- **Layout**: Full width bar fixed to the top, slightly translucent to allow the background gradient to show through.
- **Background**: `#1E293B` at 80% opacity with `backdrop-blur` for a glassy effect.
- **Buttons**:
  - Default text color `#94A3B8`.
  - Hover state: background `#334155`, text color `#E2E8F0`.
  - Active state: background `#4F46E5` (indigo-600), text color `#FFFFFF`.
- **Settings Icon**: Circular button with hover background `#1E293B` and focus ring `#64748B`.

## Countdown Cards

- **Container**: `timer-card` class with semi-transparent dark background `rgba(45,55,72,0.7)` and subtle border `rgba(255,255,255,0.1)`.
- **Shadow**: Soft drop shadow to lift the card from the background.
- **Content Alignment**: Centered text with generous padding (`1.5rem`–`2rem`).
- **Labels**: Hidden by default; fade in on hover or when pinned.
- **Main Countdown Numbers**: Large numeric type (`2.5rem` up to `8rem` depending on screen size) with `font-weight: 700`.
- **Year Countdown and Custom Event**: Use indigo accents (`#C7D2FE`) for small labels and icons.
- **Expired Event Banner**: Yellow highlight message on top of card background `#FDE047` at `10%` opacity.

## Custom Event Settings

- **Inputs**: Dark input fields (`#2D3748` background, `#4A5568` border, `#E2E8F0` text) with green focus ring (`#38A169`).
- **Primary Buttons**: `#38A169` background with hover `#2F855A`.
- **Secondary Buttons**: `#4A5568` background with hover `#2D3748`.

## Life Hourglass Visualization

- **Grid**: Responsive CSS grid (`year-grid`) with small spacing.
- **Year Item**: Centered column with small label below an SVG hourglass.
- **Past Year**: Empty hourglass icon, year label color `#64748B`.
- **Current Year**: Animated hourglass with falling sand, year label color `#F0ABFC` and drop shadow highlight `rgba(236,72,153,0.7)`.
- **Future Year**: Full hourglass icon, year label color `#A5F3FC`.
- **Hover Interaction**: Hourglass scales up slightly (1.1×) and year label turns `#67E8F9` when hovered.
- **Scrollbar**: Custom dark scrollbar track `#1E293B` and thumb `#38BDF8` (sky-400) with hover `#0EA5E9`.

## Animations

- Page transitions: fade and slide using `content-enter` and `content-exit` classes.
- Settings modal: fades and scales in/out.
- Hourglass sand grains: three keyframe animations for continuous sand drop effect.

## Responsiveness

- Layout adjusts from stacked blocks on small screens to centered grid on larger screens.
- Numeric sizes scale with viewport to maintain readability on both mobile and desktop.
