# StepTracker Utility App – CP3406 / CP5307

StepTracker is a focused, "at-a-glance" Android utility application designed to help users track their daily physical activity. Built with **Jetpack Compose** and **Material Design 3**, it provides real-time step counting, goal progress visualization, and daily motivation.

---

## 🚀 Key Features

- **Real-time Step Counting**: Utilizes the device's hardware `TYPE_STEP_COUNTER` sensor for accurate tracking.
- **Goal Visualization**: A circular progress indicator showing the percentage of the daily goal achieved.
- **Distance Calculation**: Automatically calculates distance walked in Kilometers or Miles based on user-defined stride length.
- **Dynamic Settings**: Customizable daily goals, unit preferences (KM/Miles), and stride length.
- **Daily Motivation**: Fetches random motivational quotes from the **ZenQuotes Web API** using Retrofit.
- **Permission Handling**: Robust runtime permission requests for `ACTIVITY_RECOGNITION` on Android 10+.

---

## 🛠 Technical Implementation (Weeks 1-5)

| Week | Topic | Implementation in StepTracker |
|------|-------|-------------------------------|
| 1 | Kotlin & Android Studio | Built using Kotlin with a clean, modular project structure. |
| 2 | Jetpack Compose Layouts | Uses `Scaffold`, `Column`, `Row`, and `Box` for a responsive UI. |
| 3 | Material Design 3 | Implements `Card`, `CircularProgressIndicator`, `Switch`, and `Slider` components. |
| 4 | App Architecture | Follows MVVM with `StepViewModel`, `SettingsViewModel`, and a `StepRepository`. |
| 5 | Web APIs (Retrofit) | Integrates `Retrofit` to fetch and display JSON data from external APIs. |

---

## 🏗 Architecture & Design Patterns

- **MVVM Pattern**: Decouples UI (Composables) from business logic (ViewModels).
- **Repository Pattern**: `StepRepository` abstracts both hardware sensors and network API calls.
- **Manual Dependency Injection**: Dependencies are initialized in `MainActivity` and passed to ViewModels via Factories.
- **State Management**: Uses `StateFlow` and `collectAsState` to ensure the UI reacts instantly to sensor changes.

---

## 📋 How to Run
1. Clone the repository.
2. Open in Android Studio (Ladybug or newer).
3. Ensure your device/emulator supports the **Step Counter Sensor**.
4. Grant the "Physical Activity" permission when prompted.
5. Watch your steps update in real-time as you move!

---

## 📚 Acknowledgments
- **API Provider**: [ZenQuotes.io](https://zenquotes.io/)
- **Template**: JCU CP3406 Starter Template
