# Project Discovery

The interview the AI runs with the developer once the setup in [`SETUP.md`](SETUP.md) is
finished, before the first feature is built. Its answers become three documents that guide the
rest of the project:

| Output | What it is |
|---|---|
| `docsagent/APP_PLAN.md` | What the app does, for whom, its scope, platforms, backend, and how it grows over time. |
| `docsagent/DESIGN.md` | The design system: colors, typography, shapes, components, layout, motion, tone. |
| `docsagent/design.html` | A single-page visual version of `DESIGN.md`: swatches, components, and screen mockups. |

None of these files exist in the template. The AI writes them from the developer's answers.

This file is the question list and the rules for writing the outputs. Developers may add, remove,
or reword questions to fit their way of working.

---

## 1. How to Run the Interview

### Order

1. **Part A — App Plan** (questions A1–A14). Then write `docsagent/APP_PLAN.md` and get the
   developer's OK.
2. **Part B — Design Criteria** (questions B1–B12). The design follows from the plan, so it comes
   second. Then write `docsagent/DESIGN.md` and `docsagent/design.html` and get the developer's OK.
3. **Wrap-up** (section 5): update `ROADMAP.md`, `DEVLOG.md`, and the Project Settings in
   `AGENTS.md`.

### Rules for the AI

- **Ask in the developer's language**, whatever language this file is written in.
- **Ask one group at a time** (A1, then A2, ...), three to six questions per message. Never paste the
  whole list at once.
- **Skip what is already known.** If the developer has already explained the idea, shared a
  document, a website, screenshots, a Figma file, or a competitor app, read it first and only ask
  what is still missing. Repeat back what you understood so the developer can correct it.
- **Every question may be answered with "you decide", "skip", or "not sure yet".**
  - "You decide": choose the option that fits the rest of the answers, explain the choice in one
    sentence, and record it in the decision log as *decided by AI*.
  - "Skip" / "not sure yet": record it under *Open questions* in the plan. Do not invent an answer.
- **Offer options for technical questions.** The developer may not be technical. List two to four
  realistic options with one line of trade-off each, and say which one you recommend and why.
- **Follow up on vague answers** ("a social app", "modern design") with one or two concrete
  questions or examples to choose from.
- **Do not invent features or requirements** the developer did not ask for. Suggestions are welcome,
  but mark them as suggestions and let the developer accept or drop them.
- **Summarize before writing.** At the end of each part, give a short summary of the answers and
  ask for a confirmation before you write the files.
- **Pausing is fine.** If the developer stops halfway, write what you have with the unanswered
  groups listed as open, and continue from there in a later session.

### Question 0 — before starting

> Which language should the plan and design documents be written in?

The default is the language set in `AGENTS.md` Project Settings.

---

## 2. Part A — App Plan

### A1. Vision and purpose

- What is the app's name, or a working name?
- Describe the app in one sentence: what does it do and for whom?
- Which problem does it solve? How do people deal with that problem today?
- What makes it different from what already exists?
- Is this a personal project, a startup product, a client project, or an internal company tool?
- What does success look like in 6 months? And in 2 years?
  (number of users, revenue, a finished portfolio piece, solving the team's own problem, ...)

### A2. Users

- Who are the main users? Describe one or two typical users (age range, job, daily situation).
- Is the app for consumers (B2C), businesses (B2B), or both?
- How comfortable are the users with technology?
- In which countries and regions will it be used?
- Are there users with special needs: older users, children, low vision, one-handed use, poor
  network, low-end devices?
- Are there different kinds of users with different rights (for example customer and seller,
  student and teacher, user and admin)?

### A3. Scope and features

- Walk through the most important thing a user does in the app, step by step, from opening it to
  reaching their goal.
- Which features **must** be in the first release (MVP)? Keep it to the smallest useful set.
- Which features are nice to have soon after the first release?
- Which features are ideas for later?
- What is explicitly **not** part of the app, now or later?
- Which screens can you already imagine? (home, list, detail, profile, settings, search, ...)
- Do users create content (posts, photos, reviews, messages)? Can other users see it?
- Does the app need an admin panel or dashboard to manage content and users? Who uses it?
- Should there be onboarding or a tutorial on first launch?

### A4. Platforms and devices

- The template targets Android, iOS, and the web (in the browser) with one shared UI. Which of
  them are needed for the first release, and which later? Should an unused target be removed?
  Is a desktop version (Windows, macOS, Linux) wanted at some point?
- Which platform matters most (most users, launched first)? The design and testing focus on it.
- Should the app look the same on every platform, or follow each platform's conventions where they
  differ (navigation gestures, dialogs, fonts)?
- Phones only, or also tablets, foldables, and iPads? On the web: phone browsers, desktop
  browsers, or both? Android TV, Wear OS, Apple Watch, Android Auto, CarPlay?
- Portrait only, or landscape too?
- Minimum versions: the template supports Android 8.0 (API 26), iOS 16, and current versions of
  Chrome, Edge, Firefox, and Safari. Is that fine?
- On the web: a full app, or a lighter companion (for example read only, or a landing page with a
  link to the stores)? Does it need to be found by search engines (SEO)? A Compose web app draws on
  a canvas, so search engines see little of its content.
- Must the app work offline? Fully, partly (read only), or not at all?
- Is there a Mac available for iOS builds, or should iOS be built only in CI?

### A5. Accounts and data

- Do users need an account? Is the app usable without signing in (guest mode)?
- Which sign-in methods: email and password, Google, Apple, phone number with SMS, magic link,
  company SSO?
- What data does the app store? (profiles, posts, orders, health data, location, files, ...)
- Where does the data live: only on the device, in the cloud, or both with sync?
- Should data sync between a user's devices?
- Is any of the data sensitive: health, finance, children, exact location, documents?
- Which privacy laws apply? (GDPR in the EU, KVKK in Türkiye, COPPA for children, ...)
- Can users export or delete their account and data from inside the app? (Google Play and the
  App Store both require account deletion for apps that create accounts.)

### A6. Backend and infrastructure

- Does the app need a backend at all, or is everything on the device?
- Is there an existing backend or API? If so: its documentation, base URL, and auth method.
- If a backend is needed, which approach?
  - **Backend as a service:** Firebase, Supabase, Appwrite, PocketBase. Fast to start, less control.
  - **Own backend:** Ktor (Kotlin, shares models with the app), Spring Boot, Node.js (NestJS,
    Express), .NET, Go, Python (FastAPI, Django). Full control, more work.
- Which database? (PostgreSQL, MySQL, MongoDB, Firestore, ...) — or "you decide".
- Where will it be hosted? (Google Cloud, AWS, Azure, Hetzner, DigitalOcean, Railway, Fly.io,
  Render, own server, ...) Is there a monthly budget?
- Who builds and maintains the backend: you, a teammate, another company, or the AI?
- API style: REST, GraphQL, gRPC, or realtime (WebSocket, Firestore listeners)?
- Does anything need to be realtime (chat, live location, live scores, collaborative editing)?
- File uploads (photos, videos, documents)? Where are they stored (S3, Cloud Storage, R2) and is a
  CDN needed?
- Push notifications? For what events? (usually Firebase Cloud Messaging)
- Background work: scheduled jobs, sync, reminders, emails?
- How many environments: only production, or also development and staging?

### A7. Integrations and device features

Which of these does the app need? For each one, which provider (or "you decide")?

- Payments: Google Play Billing, Stripe, iyzico, PayPal, ...
- Maps and location: Google Maps, Mapbox, OpenStreetMap; foreground or background location?
- Camera, photo picker, QR or barcode scanning, document scanning
- Bluetooth, NFC, sensors, Health Connect
- Analytics: Firebase Analytics, PostHog, Mixpanel, none
- Crash reporting: Firebase Crashlytics, Sentry, none
- Ads: AdMob or other networks
- AI features: chat, summaries, image recognition, recommendations; on-device or cloud?
- Deep links and app links, sharing to other apps, home screen widgets
- Calendar, contacts, email, SMS
- Any other third-party API (weather, news, social networks, company systems, ...)

### A8. Monetization

- How will the app make money, if at all? Free, ads, one-time purchase, subscription, freemium,
  in-app purchases, commission per transaction, part of a paid service?
- If paid: rough price range, free trial, which features are behind the paywall?

### A9. Languages and content

- Which languages at launch? Which later? Any right-to-left language (Arabic, Hebrew)?
- Currency, date, number, and unit formats?
- Where does the content come from: users, the team (editorial), an API, or AI?
- Does user content need moderation or reporting?

### A10. Quality targets

- How many users do you expect at launch, and after one year? (rough guesses are fine)
- Anything that must be especially fast or smooth (start time, scrolling feeds, maps, video)?
- Security needs: biometric unlock, screenshot blocking, root detection, certificate pinning,
  encryption of local data?
- Accessibility target: basic, or full screen reader (TalkBack, VoiceOver) and large-font support
  (WCAG AA)?
- Limits on app size, battery, or mobile data use?

### A11. Release and team

- Is there a target date for the first release? Any fixed milestones (a demo, an event, a client
  deadline)?
- How will it be distributed? Android: Google Play (public, closed testing), direct APK, Firebase
  App Distribution. iOS: App Store, TestFlight. Web: which domain and host (GitHub Pages, Firebase
  Hosting, Netlify, Vercel, own server)?
- Is there an Apple Developer account (99 USD per year, needed for TestFlight and the App Store)
  and a Google Play developer account?
- Are there beta testers?
- Who works on the project: only you with the AI, or a team? Which roles (design, backend, QA)?
- Should releases be automated (build signing, uploading to Play, TestFlight, or the web host from
  CI)?
- Is the repository public or private? (On private repositories the iOS CI job uses paid macOS
  minutes; see `docsagent/SETUP.md`.)
- Is there a budget limit for services and tools?

### A12. Legal and store

- Is there a privacy policy and terms of use, or should drafting them be a task?
- Target age group and expected content rating?
- Anything that affects the Google Play Data safety form or the App Store privacy labels
  (collected data, shared data, tracking)?
- Web: is a cookie or consent banner needed (analytics, ads, EU users)?
- Brand name and trademark: checked, or still open?

### A13. References and competitors

- Which apps are similar to yours? What do you like and dislike about them?
- Any apps (in any category) whose experience you would like to match?

### A14. Risks and open points

- What worries you most about this project (time, cost, technology, finding users, ...)?
- Is anything still undecided that we should note now?

---

## 3. Part B — Design Criteria

Ask these after `APP_PLAN.md` is confirmed. Use the plan to suggest answers: an app for older users
suggests larger text and high contrast; a finance app suggests a calm, trustworthy palette.

### B1. Brand and personality

- Describe the app's character in three to five words (for example calm, playful, premium,
  minimal, bold, warm, technical, friendly).
- Is there an existing brand: logo, colors, fonts, brand guide? Please share them.
- Which apps or websites have a look you like? What exactly do you like about them?
- Is there a look or feeling that must be avoided?

### B2. Colors

- Any colors you want, or colors that must not be used?
- Light theme, dark theme, or both? Should it follow the system setting?
- Should the app use **dynamic color** on Android (Material You: colors taken from the user's
  wallpaper on Android 12+), or always its own brand colors? iOS and the web always use the brand
  colors, so dynamic color makes Android look different from the other platforms.
- How colorful should the interface be: mostly neutral with one accent color, or rich and colorful?
- Background style: plain white or near-black, tinted neutrals, gradients, images?
- Are there colors with fixed meaning in the domain (green for income and red for expense, category
  colors, status colors)?

### B3. Typography

- Which font style fits: platform default (differs per platform; on the web, emoji and non-Latin
  scripts need a bundled font), clean geometric sans, humanist sans, rounded,
  serif for a classic feel, or monospace accents?
- A specific font, or "you decide" from Google Fonts? (The font must support every launch language;
  for Turkish check ç ğ ı İ ö ş ü.)
- How dense is the text: large and airy, or compact to show more information?

### B4. Shapes and surfaces

- Corners: sharp (0 dp), slightly rounded (4–8 dp), rounded (12–16 dp), very round (24 dp+), or
  pill-shaped buttons?
- Depth: flat with borders, soft shadows (elevation), or tonal surfaces (Material 3 style)?
- Any special effects: blur or glass, gradients, outlines, neumorphism? (Use with care: they cost
  performance and contrast.)

### B5. Icons, images, illustrations

- Icon style: Material Symbols outlined, rounded, sharp, or filled? Thin or bold strokes?
- Imagery: photos, flat illustrations, 3D illustrations, emoji, or none?
- Empty states and onboarding: illustrations, icons, or text only?
- Any ideas for the app icon and the splash screen?

### B6. Components

- **Buttons:** filled, tonal, outlined, text-only; which one is the main action style? Full-width
  main buttons on forms? Preferred height (40, 48, 56 dp)?
- **Text fields:** outlined or filled? Labels inside or above the field?
- **Cards and lists:** cards with shadows, outlined cards, or plain lists with dividers?
- **Selection:** chips, segmented buttons, switches, checkboxes, radio buttons; any preference?
- **Dialogs vs. bottom sheets** for choices and confirmations?
- **Feedback:** snackbars, toasts, banners, inline messages?
- **Loading:** spinners, progress bars, or skeleton placeholders?
- A floating action button for the main action, or not?

### B7. Layout and navigation

- Navigation pattern: bottom navigation bar, navigation drawer, top tabs, or one screen stack? How
  many top-level destinations?
- Top app bar style: small, centered title, or large title that collapses on scroll?
- Spacing: compact (more on screen) or comfortable (more white space)?
- Tablets and foldables: just stretched, or real adaptive layouts (list and detail side by side,
  navigation rail)?
- Edge-to-edge content behind the status and navigation bars (the template already enables it)?

### B8. Motion and feedback

- How much animation: minimal, moderate, or expressive?
- Screen transitions: slide, fade, shared element (an image that flows from list to detail)?
- Micro-interactions and haptic feedback (vibration on actions)?
- Should animations be reduced when the system "remove animations" setting is on? (Recommended:
  yes.)

### B9. Accessibility

- Minimum text contrast: WCAG AA (recommended) or AAA?
- Support for system font scaling up to 200 % without broken layouts?
- Touch targets of at least 48 × 48 dp (Material and Play guideline)?
- Colorblind-safe use of color (never color as the only signal)?

### B10. Voice and content

- Tone of the text: formal, neutral, friendly, or playful? (In Turkish: "sen" or "siz"?)
- Emoji in the interface: yes, sometimes, never?
- How should errors speak to the user (technical, plain and helpful, light humor)?

### B11. Screens to show in the design page

- Which screens should `design.html` mock up? The default is the main screens from the plan, for
  example onboarding, sign-in, home, list, detail, a form, settings, and an empty and an error
  state.
- Which screen matters most? It gets the most detailed mockup.

### B12. Design tools

- Is there a designer or a Figma file? If yes, Figma is the source and `DESIGN.md` documents it.
- If not, `DESIGN.md` is the source of truth, and the Compose theme follows it.

---

## 4. Writing the Outputs

Write the files from the answers only. Mark every value that came from the AI rather than the
developer, so it can be reviewed later. Use real content from the plan (real screen names, feature
names, example data), never lorem ipsum.

### `docsagent/APP_PLAN.md`

Sections, in this order (leave a section out only if it truly does not apply, and say so):

1. **Overview** — name, one-sentence pitch, problem, solution, project type.
2. **Goals and success metrics** — 6-month and 2-year goals, measurable where possible.
3. **Users** — personas, user roles and their rights, regions and languages.
4. **Scope** — MVP, next release(s), later, and out of scope, as separate lists.
5. **Features** — table with feature, short description, user role, priority (must / should /
   could), and target release.
6. **User journeys** — the main flows step by step.
7. **Screen map** — all screens and how they connect, as a Mermaid flowchart.
8. **Platforms and devices** — which of Android, iOS, and web are used now and later, minimum
   versions, devices and screen sizes, orientation, offline level, platform-specific differences.
9. **Architecture overview** — app, backend, and third-party services as a Mermaid diagram; which
   template parts are used (Network, Room, Settings) and which are removed; a first list of
   feature modules (`:presentation:features:<name>`).
10. **Data model draft** — main entities, their important fields, and relations (Mermaid ER diagram).
11. **Backend and infrastructure** — stack, database, hosting, environments, API style, auth,
    storage, push, background jobs, and estimated monthly cost.
12. **Integrations** — each service, its purpose, and the chosen provider.
13. **Monetization**
14. **Localization and content**
15. **Non-functional requirements** — performance, offline, security, accessibility, scale.
16. **Privacy and legal** — sensitive data, applicable laws, account deletion, policies, store
    forms.
17. **Release plan** — milestones with rough dates, distribution, beta testing, release automation.
18. **Future vision** — where the app could go in 2+ years (new platforms, features, markets) and
    what to keep in mind today so that growth stays possible.
19. **Risks** — each risk with its likelihood, impact, and mitigation.
20. **Open questions**
21. **Decision log** — table with date, decision, chosen by (developer / AI), and reason.

### `docsagent/DESIGN.md`

The written design system. Each value maps to a place in the code (`presentation/ui/theme/`), so
the theme can be built from it directly.

1. **Design principles** — three to five short rules derived from B1 (for example "one clear
   action per screen").
2. **Color palette** — light and dark values as hex, mapped to the Material 3 color roles
   (`primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`, `secondary`, `tertiary`,
   `error`, `background`, `surface`, `surfaceVariant`, the `surfaceContainer` levels, `outline`,
   `outlineVariant`, ...), plus domain colors (success, warning, categories). State the dynamic
   color decision. List the contrast ratio of every text and background pair and confirm it meets
   the chosen WCAG level.
3. **Typography** — font families and the full Material 3 type scale (display, headline, title,
   body, label; large, medium, small) with size, line height, weight, and letter spacing.
4. **Spacing** — the spacing scale on a 4 dp grid (for example 4, 8, 12, 16, 24, 32, 48) and the
   standard screen padding.
5. **Shapes** — the Material 3 shape scale (extra small to extra large) in dp, and which component
   uses which shape.
6. **Elevation and surfaces** — how depth is shown (tonal, shadow, border) and the levels used.
7. **Iconography and imagery** — icon set and style, sizes, illustration and photo rules, app icon
   and splash direction.
8. **Components** — for each component in B6: variants, sizes, when to use which, and states
   (default, pressed, focused, disabled, loading, error).
9. **Layout and navigation** — navigation pattern, top bar style, grid, window size classes
   (compact, medium, expanded) and how each screen type adapts.
10. **Screen templates** — the standard structure of list, detail, form, and settings screens, and
    of the empty, loading, and error states.
11. **Motion** — durations, easing, transition types, haptics, reduced-motion behavior.
12. **Accessibility** — contrast, touch targets, font scaling, screen reader labels (TalkBack,
    VoiceOver, browser screen readers), color use.
13. **Voice and tone** — writing rules and examples of good and bad microcopy, error messages.
14. **Do and don't** — short list of examples.
15. **Implementation map** — which file in `presentation/ui/theme/` holds which part (`Color.kt`,
    `Type.kt`, `Theme.kt`, and new files such as `Shape.kt` or `Spacing.kt` if needed), and the
    font files to add to `presentation/ui/src/commonMain/composeResources/font/`.

### `docsagent/design.html`

A single, self-contained HTML page that shows `DESIGN.md` visually. Open it in any browser.

- **One file:** all CSS and JavaScript inline. The only allowed external resource is Google Fonts
  for the chosen font; the page must still be readable without it.
- **Same values as `DESIGN.md`.** `DESIGN.md` is the source; when one changes, update the other in
  the same change.
- **Light / dark toggle** that switches every swatch, component, and mockup.
- **Sections:**
  1. Header with the app name, the design principles, and the personality words.
  2. Color palette: swatches for every role in light and dark, with hex value and contrast ratio.
  3. Typography specimen: every type scale role with a real sample sentence from the app.
  4. Spacing and shape scales, drawn to size.
  5. Component gallery: all button variants and states, text fields (empty, filled, error,
     disabled), chips, switches, cards, list items, dialog, bottom sheet, snackbar, top app bar,
     navigation bar, loading and empty states.
  6. Screen mockups: the screens from B11 inside phone frames (360 × 800 dp scaled), built with the
     real tokens and real example content, the most important screen first.
  7. Navigation topology: the screen map from `APP_PLAN.md` as an inline SVG diagram, showing
     top-level destinations and the flows between screens.
  8. Adaptive layouts: the most important screen in compact and expanded width side by side, and
     how it looks on each target platform (Android phone, iPhone, desktop browser) where they differ.
  9. Do and don't examples side by side.
- **Responsive:** the page itself works on a phone screen and a desktop monitor.

---

## 5. Wrap-up

After the developer has approved all three files:

1. **`ROADMAP.md`:** replace the placeholder phases with phases built from the release plan and the
   feature list in `APP_PLAN.md`. Add the tasks to apply `DESIGN.md` to `presentation/ui/theme/`
   and to remove the template parts the plan does not use.
2. **`AGENTS.md`:** set "Project discovery" in Project Settings to `DONE`.
3. **`DEVLOG.md`:** add an entry summarizing the key decisions and linking the three files.
4. Do not start building the theme or features in the same step; they are roadmap tasks now.

### Keeping the documents alive

- When a decision changes later (a new feature, a different backend, a new color), update
  `APP_PLAN.md` or `DESIGN.md` and `design.html` in the same piece of work, and add a row to the
  decision log.
- To redo the interview, or one part of it, ask the AI to run this file again; it updates the
  existing documents instead of starting from zero.
