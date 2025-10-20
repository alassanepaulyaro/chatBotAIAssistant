# ChatBot AI Assistant with OpenAI

## Description

This repository contains an Android application demonstrating how to use the OpenAI API, with features including:
- Chat conversation with OpenAI GPT models
- Speech recognition (Speech-to-Text)
- Text-to-Speech (TTS)
- Local message persistence with Room database
- Clean Architecture with MVVM pattern

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Minimum SDK: 24
- Target SDK: 35
- JDK 17

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/chatBotAIAssistant.git
   cd chatBotAIAssistant
   ```

2. **Get your OpenAI API Key**
   - Go to [OpenAI Platform](https://platform.openai.com/docs/overview)
   - Login with your account
   - Navigate to [API Keys](https://platform.openai.com/api-keys)
   - Create a new API key

3. **Configure API Keys (IMPORTANT - Security)**

   The project uses a secure method to handle API keys. Follow these steps:

   a. Copy the example configuration file:
      ```bash
      cp local.properties.example local.properties
      ```

   b. Open `local.properties` and replace `your_openai_api_key_here` with your actual API key:
      ```properties
      OPENAI_API_KEY=sk-proj-your-actual-api-key-here
      ```

   **Security Notes:**
   - `local.properties` is automatically git-ignored and will NOT be committed to version control
   - NEVER commit your API keys to git
   - NEVER share your `local.properties` file
   - Each developer must create their own `local.properties` file

4. **Build and Run**
   - Open the project in Android Studio
   - Sync Gradle
   - Run the app on an emulator or physical device

## Security Best Practices

### API Key Protection

This project implements the following security measures:

1. **BuildConfig Injection**: API keys are injected into `BuildConfig` at build time from `local.properties`
2. **Git Ignored**: The `local.properties` file is excluded from version control via `.gitignore`
3. **Example Template**: A `local.properties.example` file is provided as a template for new developers
4. **No Hardcoded Keys**: All sensitive information is removed from source code

### How It Works

```
local.properties (git-ignored)
      ↓
build.gradle.kts (reads properties)
      ↓
BuildConfig.OPENAI_API_KEY (generated at build time)
      ↓
Constants.kt (references BuildConfig)
      ↓
Repositories (use Constants.API_KEY)
```

### For Team Collaboration

When onboarding new developers:
1. Share the `local.properties.example` file (safe to commit)
2. Instruct them to create their own `local.properties`
3. Have them obtain their own OpenAI API key
4. Remind them NEVER to commit `local.properties`

## Architecture

The project follows Clean Architecture principles with three main layers:

- **Presentation Layer**: ViewModels, UI States, Composables
- **Domain Layer**: Use Cases, Repository Interfaces, Domain Models
- **Data Layer**: Repository Implementations, API Service, Database DAOs

### Tech Stack

- **UI**: Jetpack Compose, Material3
- **DI**: Hilt/Dagger
- **Networking**: Retrofit, OkHttp
- **Database**: Room
- **Async**: Kotlin Coroutines, Flow
- **Image Loading**: Coil

<img src="Screenshot_chatbotAi.png" alt="Alt Text" width="1024" height="1024">

[Watch the video](Screen_recording_chatbotAI.mp4)

<video width="640" height="480" controls>
  <source src="Screen_recording_chatbotAI.mp4" type="video/mp4">
</video>
