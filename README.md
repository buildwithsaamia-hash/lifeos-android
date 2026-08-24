# LifeOS AI 🚀

**LifeOS AI** is an all-in-one personal operating system and intelligent assistant built natively for Android using **Jetpack Compose**, **Material 3**, and **Gemini AI**. It unifies daily productivity, goals, finance, health, secure vaults, and emergency assistance into a single dashboard.

---

## 🌟 Key Features

### 1. 🤖 Smart AI Assistant (Gemini Integration)
- Context-aware life assistant capable of analyzing your schedule, tasks, and habits.
- Interactive multi-turn AI chat for brainstorming, planning, and task generation.
- Quick action shortcuts for instant daily summaries and productivity tips.

### 2. 📅 Planner & Routine Manager
- Daily timeblocking and priority task scheduling.
- Category-based task organization (Work, Personal, Fitness, Education).
- Real-time status chips and completion tracking.

### 3. 🎯 Goals & Habit Mastery
- Long-term vision and short-term milestone tracking.
- Progress visualization with animated percentage bars.
- Habit streak counters and accountability check-ins.

### 4. 💰 Personal Finance & Budget Tracker
- Daily expense and income recording.
- Category breakdowns (Food, Transport, Utilities, Entertainment, Savings).
- Real-time financial health summaries.

### 5. 🏃 Health & Wellness Hub
- Daily step tracking, hydration logs, and sleep monitoring.
- Activity metrics and custom health routines.
- Direct insights into physical well-being and recovery.

### 6. 📁 Secure Documents & Notes Vault
- Safe local repository for notes, critical documents, and certificates.
- Fast search, filtering, and tag-based categorization.

### 7. 🚨 Emergency Hub & SOS
- One-tap quick emergency calling and SOS broadcast.
- Critical health details & blood group reference card.
- Direct phone dialer integration with fallback safety protocols.

### 8. ⚙️ Admin & Customization
- Dynamic Material 3 Theming (Light & Dark mode support).
- Database backup and management controls.
- Secure API key configuration.

---

## 🛠️ Architecture & Tech Stack

- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Language:** [Kotlin](https://kotlinlang.org/) (100% Coroutines & Flow-driven)
- **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
- **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) via KSP
- **AI Engine:** Google Gemini API integration
- **Build System:** Gradle (Kotlin DSL - `.gradle.kts`) with Version Catalog (`libs.versions.toml`)
- **Compatibility:** Min SDK 24 (Android 7.0+) | Target SDK 36

---

## 📂 Project Structure

```text
├── app
│   ├── src/main
│   │   ├── java/com/example
│   │   │   ├── ai/            # Gemini API client & AI services
│   │   │   ├── data/          # Room DB, DAOs, Entities, Repository
│   │   │   ├── ui/
│   │   │   │   ├── components/# Reusable Compose UI components
│   │   │   │   ├── screens/   # Application feature screens
│   │   │   │   └── theme/     # Material 3 Color Schemes & Typography
│   │   │   ├── viewmodel/     # State management & ViewModels
│   │   │   └── MainActivity.kt
│   │   ├── res/               # Vector icons, strings, resources
│   │   └── AndroidManifest.xml
├── gradle/                    # Gradle wrapper and version catalog
├── build.gradle.kts           # Root build configuration
├── settings.gradle.kts        # Project settings & repositories
└── metadata.json              # Platform metadata
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Ladybug / Meerkat or newer recommended)
- **JDK 17** or **JDK 21**
- Android SDK with API Level 34+ installed

### Installation & Setup

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/your-username/LifeOS-AI.git
   cd LifeOS-AI
   ```

2. **Configure API Keys:**
   - Copy `.env.example` to `.env`:
     ```bash
     cp .env.example .env
     ```
   - Add your Gemini API Key in `.env`:
     ```properties
     GEMINI_API_KEY=your_gemini_api_key_here
     ```

3. **Open & Build in Android Studio:**
   - Open Android Studio and select **Open**, then choose the project folder.
   - Wait for Gradle sync to complete.
   - Run the app on an Android device or emulator (`Shift + F10`).

4. **Command Line Build:**
   - Build Debug APK:
     ```bash
     ./gradlew assembleDebug
     ```
   - Run Unit Tests:
     ```bash
     ./gradlew testDebugUnitTest
     ```

---

## 🔒 Permissions & Privacy
- **INTERNET:** Required for Gemini AI queries and cloud features.
- **CALL_PHONE:** Optional / Fallback for direct Emergency SOS dialing (`android.hardware.telephony` set to `required="false"` for tablet/Chromebook compatibility).
- **Offline First:** All personal notes, finances, and goals are stored locally in an encrypted Room SQLite database on your device.

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
