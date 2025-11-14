# ChatBot AI Assistant with OpenAI

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-orange.svg)](https://developer.android.com/about/versions)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern Android chatbot application powered by OpenAI's GPT models, featuring real-time conversations, speech recognition, text-to-speech capabilities, and AI image generation.

[Features](#features) • [Architecture](#architecture) • [Installation](#installation) • [Usage](#usage) • [Contributing](#contributing)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots & Demo](#screenshots--demo)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Security Best Practices](#security-best-practices)
- [Usage](#usage)
- [OpenAI API Integration](#openai-api-integration)
- [Testing](#testing)
- [Build & Deployment](#build--deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Roadmap](#roadmap)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Contact & Support](#contact--support)

---

## Overview

ChatBot AI Assistant is a feature-rich Android application that demonstrates modern Android development practices while integrating OpenAI's powerful AI capabilities. The application provides an intuitive chat interface where users can interact with GPT models through text or voice, generate images from descriptions, and maintain conversation history locally.

### Key Highlights

- **Modern Android Development**: Built with Jetpack Compose, Material 3, and the latest Android best practices
- **Clean Architecture**: Follows SOLID principles with clear separation of concerns (Data, Domain, Presentation layers)
- **AI-Powered Conversations**: Integrates OpenAI GPT models for intelligent responses
- **Speech Integration**: Bidirectional voice communication with Speech-to-Text and Text-to-Speech
- **Image Generation**: Create images using DALL-E through natural language prompts
- **Offline Support**: Local message persistence with Room database
- **Network Awareness**: Real-time connectivity monitoring and error handling
- **Comprehensive Testing**: Unit and integration tests with high coverage

---

## Features

### Core Features

- **Real-time Chat Interface**
  - Seamless conversation with OpenAI GPT models
  - Message bubbles with sender/receiver distinction
  - Timestamp tracking for all messages
  - Typing indicators for better UX
  - Auto-scroll to latest messages

- **Voice Interaction**
  - Speech-to-Text: Convert spoken words to text input
  - Text-to-Speech: AI responses read aloud automatically
  - Voice input button with visual feedback
  - Toggle TTS on/off for user preference

- **AI Image Generation**
  - Generate images from text descriptions using DALL-E
  - In-chat image display with Coil image loading
  - Image URL persistence in chat history

- **Local Data Persistence**
  - All conversations saved locally with Room database
  - Message history survives app restarts
  - Clear conversation history option
  - User and AI message differentiation

- **Network & Connectivity**
  - Real-time network status monitoring
  - Graceful offline mode handling
  - Connectivity indicators in UI
  - Automatic retry mechanisms

- **Modern UI/UX**
  - Material 3 Design implementation
  - Dark/Light theme support
  - Smooth animations and transitions
  - Responsive layouts for different screen sizes
  - Intuitive user interface

### Technical Features

- **Clean Architecture**: Separation into Data, Domain, and Presentation layers
- **Dependency Injection**: Hilt for compile-time DI
- **Reactive Programming**: Kotlin Coroutines and Flow for async operations
- **Type-Safe Navigation**: Compose navigation
- **State Management**: MVVM pattern with StateFlow
- **Error Handling**: Comprehensive Result wrapper pattern
- **Security**: Secure API key management (never committed to VCS)

---

## Screenshots & Demo

### Application Screenshot

<img src="Screenshot_chatbotAi.png" alt="ChatBot AI Assistant Screenshot" width="400">

### Video Demo

[Watch the video demonstration](Screen_recording_chatbotAI.mp4)

<video width="640" height="480" controls>
  <source src="Screen_recording_chatbotAI.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>

---

## Architecture

This project follows **Clean Architecture** principles with three distinct layers:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Composables │  │  ViewModels  │  │   UI States  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Use Cases   │  │ Repositories │  │Domain Models │     │
│  │              │  │ (Interfaces) │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │RepositoryImpl│  │  API Service │  │  Room DAO    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │Data Models   │  │   Mappers    │  │  Database    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

#### Presentation Layer (`presentation/`, `ui/`)
- **ViewModels**: Manage UI state and handle user interactions
- **UI States**: Define immutable state models for the UI
- **Composables**: Jetpack Compose UI components
- **Theme**: Material 3 theming and styling

#### Domain Layer (`domain/`)
- **Use Cases**: Business logic and application-specific operations
  - `SendMessageUseCase`: Send chat messages to API
  - `GetMessagesUseCase`: Retrieve message history
  - `ClearMessagesUseCase`: Clear conversation history
  - `GenerateImageUseCase`: Generate images from prompts
  - `ObserveConnectivityUseCase`: Monitor network status
- **Repository Interfaces**: Abstract data operations
- **Domain Models**: Business entities (e.g., `ChatMessage`)
- **Result Wrapper**: Type-safe error handling

#### Data Layer (`data/`)
- **Repository Implementations**: Concrete data operations
- **API Service**: Retrofit interface for OpenAI API
- **Room Database**: Local persistence
  - `MessageDao`: Database access object
  - `AppDatabase`: Database configuration
- **Data Models**: API request/response models
- **Mappers**: Convert between data and domain models

### Design Patterns

- **MVVM (Model-View-ViewModel)**: Separates UI logic from business logic
- **Repository Pattern**: Abstracts data sources
- **Dependency Injection**: Hilt provides dependencies
- **Use Case Pattern**: Encapsulates business operations
- **Observer Pattern**: Flow/StateFlow for reactive data streams
- **Mapper Pattern**: Transforms between layer-specific models
- **Result Pattern**: Type-safe error handling

---

## Project Structure

```
app/src/main/java/com/yaropaul/chatbotaiassistant/
├── data/
│   ├── apiRemote/
│   │   └── ApiService.kt              # Retrofit API interface
│   ├── local/
│   │   ├── AppDatabase.kt             # Room database configuration
│   │   └── MessageDao.kt              # Database access object
│   ├── mapper/
│   │   └── MessageMapper.kt           # Data ↔ Domain model conversion
│   ├── model/
│   │   ├── ChatRequest.kt             # API request models
│   │   ├── ChatResponse.kt            # API response models
│   │   ├── ImageRequest.kt
│   │   ├── ImageResponse.kt
│   │   ├── Message.kt                 # Database entity
│   │   └── User.kt
│   └── repository/
│       ├── ChatRepositoryImpl.kt      # Chat repository implementation
│       └── ConnectivityRepositoryImpl.kt
│
├── di/
│   └── AppModule.kt                   # Hilt dependency injection setup
│
├── domain/
│   ├── model/
│   │   └── ChatMessage.kt             # Domain model
│   ├── repository/
│   │   ├── IChatRepository.kt         # Repository interface
│   │   └── IConnectivityRepository.kt
│   ├── usecase/
│   │   ├── ClearMessagesUseCase.kt
│   │   ├── GenerateImageUseCase.kt
│   │   ├── GetMessagesUseCase.kt
│   │   ├── ObserveConnectivityUseCase.kt
│   │   └── SendMessageUseCase.kt
│   └── util/
│       └── Result.kt                  # Result wrapper for error handling
│
├── presentation/
│   └── chat/
│       ├── ChatUiState.kt             # UI state model
│       └── ChatViewModelRefactored.kt # ViewModel
│
├── ui/
│   ├── chat/
│   │   └── ChatScreen.kt              # Main chat composable
│   ├── components/
│   │   └── TypingIndicator.kt        # Reusable UI components
│   └── theme/
│       ├── ColorEnhanced.kt           # Color palette
│       ├── Theme.kt                   # Material 3 theme
│       └── Type.kt                    # Typography
│
├── utils/
│   ├── Constants.kt                   # App constants (API keys, URLs)
│   ├── Converters.kt                  # Type converters for Room
│   └── Event.kt                       # One-time event wrapper
│
├── ChatApplication.kt                 # Application class
└── MainActivity.kt                    # Main activity

app/src/test/                          # Unit tests
app/src/androidTest/                   # Instrumentation tests
```

---

## Tech Stack

### Core Technologies

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Language** | Kotlin | Primary development language |
| **UI Framework** | Jetpack Compose | Modern declarative UI toolkit |
| **Design System** | Material 3 | Google's latest design system |
| **Min SDK** | 24 (Android 7.0) | Minimum Android version |
| **Target SDK** | 35 (Android 15) | Target Android version |
| **JDK** | 17 | Java Development Kit |

### Architecture Components

| Library | Version | Purpose |
|---------|---------|---------|
| **Hilt** | Latest | Dependency injection framework |
| **Room** | Latest | Local database persistence |
| **ViewModel** | Latest | Lifecycle-aware state management |
| **Lifecycle** | Latest | Lifecycle management components |
| **Navigation Compose** | Latest | Type-safe navigation |

### Networking & Serialization

| Library | Purpose |
|---------|---------|
| **Retrofit** | HTTP client for API calls |
| **OkHttp** | HTTP/HTTP2 client with interceptors |
| **Gson Converter** | JSON serialization/deserialization |
| **Logging Interceptor** | Network request/response logging |

### Asynchronous Programming

| Library | Purpose |
|---------|---------|
| **Kotlin Coroutines** | Asynchronous programming |
| **Flow** | Reactive streams |
| **StateFlow** | State holder observable flow |

### Image Loading

| Library | Purpose |
|---------|---------|
| **Coil** | Image loading and caching for Compose |

### Speech & Audio

| Component | Purpose |
|-----------|---------|
| **SpeechRecognizer** | Android speech-to-text API |
| **TextToSpeech** | Android text-to-speech API |

### Testing

| Library | Purpose |
|---------|---------|
| **JUnit** | Unit testing framework |
| **MockK** | Mocking library for Kotlin |
| **Turbine** | Flow testing utilities |
| **Truth** | Assertion library |
| **Coroutines Test** | Testing coroutines |
| **Espresso** | UI testing |
| **Compose UI Test** | Compose UI testing |
| **Hilt Testing** | DI testing support |

### Build Tools

| Tool | Purpose |
|------|---------|
| **Gradle** | Build automation |
| **KSP** | Kotlin Symbol Processing for code generation |

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio**: Hedgehog (2023.1.1) or later
  - Download from [developer.android.com](https://developer.android.com/studio)
- **JDK 17**: Java Development Kit
  - Usually bundled with Android Studio
- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 35 (Android 15)
- **OpenAI API Key**: Required for AI features
  - Get one from [platform.openai.com](https://platform.openai.com/)

### System Requirements

- **OS**: Windows 10/11, macOS 10.14+, or Linux
- **RAM**: 8 GB minimum (16 GB recommended)
- **Disk Space**: 4 GB minimum for Android Studio + SDK

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/chatBotAIAssistant.git
cd chatBotAIAssistant
```

### 2. Obtain OpenAI API Key

1. Visit [OpenAI Platform](https://platform.openai.com/)
2. Sign in or create an account
3. Navigate to [API Keys](https://platform.openai.com/api-keys)
4. Click "Create new secret key"
5. Copy the generated API key (you won't be able to see it again!)

### 3. Configure API Key (IMPORTANT)

The project uses a secure method to handle API keys:

**Step A**: Copy the example configuration file:
```bash
cp local.properties.example local.properties
```

**Step B**: Open `local.properties` and add your API key:
```properties
OPENAI_API_KEY=sk-proj-your-actual-openai-api-key-here
```

**Step C**: Verify the file is git-ignored:
```bash
git check-ignore local.properties
# Should output: local.properties
```

### 4. Open in Android Studio

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository folder
4. Click "OK"

### 5. Sync Gradle

Android Studio will automatically start syncing Gradle. If not:
1. Click "File" → "Sync Project with Gradle Files"
2. Wait for the sync to complete

### 6. Build the Project

```bash
# Command line
./gradlew build

# Or in Android Studio: Build → Make Project (Ctrl+F9 / Cmd+F9)
```

### 7. Run the Application

**Option 1: Using Android Studio**
1. Connect an Android device or start an emulator
2. Click the "Run" button (green play icon) or press Shift+F10
3. Select your target device

**Option 2: Using Command Line**
```bash
# Install on connected device
./gradlew installDebug

# Run on specific device
./gradlew installDebug
adb shell am start -n com.yaropaul.chatbotaiassistant/.MainActivity
```

---

## Security Best Practices

### API Key Protection

This project implements multiple layers of security for API key management:

#### 1. BuildConfig Injection
- API keys are injected into `BuildConfig` at build time
- Keys are read from `local.properties` (git-ignored)
- Never hardcoded in source code

#### 2. Version Control Protection
- `local.properties` is in `.gitignore`
- Only `local.properties.example` (template) is committed
- Actual keys never appear in git history

#### 3. Secure Access Flow

```
┌──────────────────────┐
│ local.properties     │  ← Git ignored, never committed
│ OPENAI_API_KEY=...   │
└──────────────────────┘
          ↓
┌──────────────────────┐
│ build.gradle.kts     │  ← Reads properties at build time
│ buildConfigField()   │
└──────────────────────┘
          ↓
┌──────────────────────┐
│ BuildConfig.kt       │  ← Generated at compile time
│ (auto-generated)     │
└──────────────────────┘
          ↓
┌──────────────────────┐
│ Constants.kt         │  ← References BuildConfig
│ API_KEY = BuildCon...│
└──────────────────────┘
          ↓
┌──────────────────────┐
│ Repository Layer     │  ← Uses Constants.API_KEY
│ API calls with auth  │
└──────────────────────┘
```

### Security Checklist

- [ ] Never commit `local.properties` to version control
- [ ] Never hardcode API keys in source code
- [ ] Never log API keys in production
- [ ] Never share your `local.properties` file
- [ ] Rotate API keys if accidentally exposed
- [ ] Use environment variables for CI/CD pipelines
- [ ] Enable ProGuard/R8 obfuscation for release builds
- [ ] Restrict API key permissions on OpenAI dashboard

### Team Collaboration

When onboarding new developers:

1. **Share** the `local.properties.example` file (safe to commit)
2. **Instruct** them to create their own `local.properties`
3. **Have them** obtain their own OpenAI API key
4. **Remind** them never to commit `local.properties`
5. **Verify** `.gitignore` includes `local.properties`

### Production Considerations

For production deployments:

- Use environment variables in CI/CD (GitHub Actions, Jenkins, etc.)
- Consider using Android Keystore for additional security
- Implement certificate pinning for API calls
- Enable code obfuscation with ProGuard/R8
- Monitor API key usage on OpenAI dashboard
- Set up usage limits and alerts

---

## Usage

### Basic Chat

1. **Start the app**: Launch ChatBot AI Assistant
2. **Type a message**: Enter text in the input field at the bottom
3. **Send**: Tap the send button or press Enter
4. **Receive response**: AI response appears in the chat

### Voice Input

1. **Tap microphone icon**: Located next to the text input field
2. **Grant permission**: Allow microphone access if prompted
3. **Speak**: Say your message clearly
4. **Automatic transcription**: Speech converts to text automatically
5. **Send**: Message is sent to AI

### Text-to-Speech

1. **Toggle TTS**: Tap the speaker icon in the toolbar
2. **Enable**: Icon indicates TTS is active
3. **Listen**: AI responses are read aloud automatically
4. **Disable**: Tap again to turn off voice output

### Image Generation

1. **Type prompt**: Enter an image description
   - Example: "A futuristic city with flying cars at sunset"
2. **Send**: Submit the prompt
3. **Wait**: DALL-E processes the request (may take 10-30 seconds)
4. **View**: Generated image appears in the chat

### Clear History

1. **Tap clear icon**: Located in the toolbar
2. **Confirm**: Dialog asks for confirmation
3. **Cleared**: All messages removed from local database

### Network Status

- **Green indicator**: Connected to internet, API calls will work
- **Red indicator**: Offline mode, API calls will fail gracefully

---

## OpenAI API Integration

### Endpoints Used

#### 1. Chat Completions API

**Endpoint**: `POST /v1/chat/completions`

**Purpose**: Generate conversational responses

**Request**:
```json
{
  "model": "gpt-3.5-turbo",
  "messages": [
    {"role": "user", "content": "Hello, how are you?"}
  ]
}
```

**Response**:
```json
{
  "choices": [
    {
      "message": {
        "role": "assistant",
        "content": "I'm doing well, thank you! How can I assist you today?"
      }
    }
  ]
}
```

**Implementation**: `ChatRepositoryImpl.kt:39`

#### 2. Image Generation API (DALL-E)

**Endpoint**: `POST /v1/images/generations`

**Purpose**: Generate images from text descriptions

**Request**:
```json
{
  "prompt": "A futuristic city with flying cars",
  "n": 1,
  "size": "1024x1024"
}
```

**Response**:
```json
{
  "data": [
    {"url": "https://...generated-image-url..."}
  ]
}
```

**Implementation**: `ChatRepositoryImpl.kt:71`

### Rate Limits & Costs

Be aware of OpenAI's rate limits and pricing:

- **Free tier**: Limited requests per minute
- **Pay-as-you-go**: Charged per token/image
- **Rate limits**: Vary by model and account tier
- **Best practices**: Implement caching, debouncing, and error handling

Check current pricing: [openai.com/pricing](https://openai.com/pricing)

### Supported Models

The app can be configured to use different OpenAI models:

- `gpt-3.5-turbo`: Fast, cost-effective
- `gpt-4`: More capable, higher cost
- `gpt-4-turbo`: Balanced performance and cost
- `dall-e-2`: Image generation
- `dall-e-3`: Advanced image generation

To change the model, update the request in `ChatRequest.kt`.

---

## Testing

### Running Tests

#### Unit Tests

```bash
# Run all unit tests
./gradlew test

# Run tests for specific module
./gradlew app:testDebugUnitTest

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

#### Instrumentation Tests

```bash
# Run all Android tests
./gradlew connectedAndroidTest

# Run on specific device
./gradlew connectedDebugAndroidTest
```

#### In Android Studio

1. Right-click on test file/folder
2. Select "Run Tests"
3. View results in Test Results panel

### Test Coverage

The project includes comprehensive tests:

- **Use Cases**: `domain/usecase/*Test.kt`
  - `SendMessageUseCaseTest`
  - `GetMessagesUseCaseTest`
  - `ClearMessagesUseCaseTest`
  - `GenerateImageUseCaseTest`

- **Repositories**: `data/repository/*Test.kt`
  - `ChatRepositoryImplTest`

- **ViewModels**: `presentation/chat/*Test.kt`
  - `ChatViewModelRefactoredTest`

### Test Technologies

- **JUnit 4**: Test framework
- **MockK**: Mocking library for Kotlin
- **Turbine**: Testing Kotlin Flows
- **Truth**: Fluent assertions
- **Coroutines Test**: Testing coroutines and suspending functions

### Example Test

```kotlin
@Test
fun `sendMessage returns success when API call succeeds`() = runTest {
    // Arrange
    val message = "Hello"
    val expectedResponse = "Hi there!"
    coEvery { apiService.sendMessage(any(), any()) } returns mockResponse

    // Act
    val result = repository.sendMessage(message)

    // Assert
    assertThat(result).isInstanceOf(Result.Success::class.java)
    assertThat((result as Result.Success).data).isEqualTo(expectedResponse)
}
```

---

## Build & Deployment

### Debug Build

```bash
# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build

#### 1. Create Keystore (first time only)

```bash
keytool -genkey -v -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

#### 2. Configure Signing

Add to `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("path/to/my-release-key.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = "my-key-alias"
        keyPassword = System.getenv("KEY_PASSWORD")
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

#### 3. Build Release APK

```bash
# Build signed release APK
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release.apk
```

#### 4. Build Android App Bundle (AAB)

```bash
# For Google Play Store
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

### ProGuard/R8 Configuration

Enable code shrinking and obfuscation in release builds:

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### CI/CD Pipeline

Example GitHub Actions workflow:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Create local.properties
        run: echo "OPENAI_API_KEY=${{ secrets.OPENAI_API_KEY }}" > local.properties

      - name: Build with Gradle
        run: ./gradlew build

      - name: Run tests
        run: ./gradlew test
```

---

## Troubleshooting

### Common Issues

#### 1. API Key Not Found

**Error**: `BuildConfig.OPENAI_API_KEY is empty`

**Solution**:
- Ensure `local.properties` exists in project root
- Verify the format: `OPENAI_API_KEY=sk-...`
- Sync Gradle files: File → Sync Project with Gradle Files
- Clean and rebuild: Build → Clean Project, then Build → Rebuild Project

#### 2. Gradle Sync Failed

**Error**: Various Gradle sync errors

**Solution**:
```bash
# Invalidate caches
# In Android Studio: File → Invalidate Caches / Restart

# Or command line:
./gradlew clean
./gradlew --stop
./gradlew build --refresh-dependencies
```

#### 3. Room Schema Export Error

**Error**: `Schema export directory not set`

**Solution**: Add to `app/build.gradle.kts`:
```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

#### 4. Network Timeout

**Error**: API calls timing out

**Solution**:
- Check internet connection
- Verify API key is valid
- Check OpenAI service status: [status.openai.com](https://status.openai.com)
- Increase timeout in `Constants.kt`: `const val TIMEOUT = 120L`

#### 5. Speech Recognition Not Working

**Error**: Microphone not responding

**Solution**:
- Grant microphone permission in app settings
- Check device microphone is working
- Ensure Speech Services are installed (Google app)
- Test on physical device (emulator may have issues)

#### 6. Images Not Loading

**Error**: Generated images not displaying

**Solution**:
- Check internet permission in manifest
- Verify Coil dependency is included
- Clear app cache: Settings → Apps → ChatBot → Clear Cache
- Check image URL is valid

### Debugging Tips

1. **Enable Logging**: Check Logcat for detailed error messages
2. **Network Inspector**: Use Android Studio's Network Inspector
3. **Database Inspector**: Inspect Room database content
4. **Breakpoints**: Set breakpoints in ViewModel and Repository
5. **API Testing**: Test OpenAI API directly with curl or Postman

---

## Contributing

Contributions are welcome! Please follow these guidelines:

### How to Contribute

1. **Fork the repository**
   ```bash
   git clone https://github.com/yourusername/chatBotAIAssistant.git
   cd chatBotAIAssistant
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow Kotlin coding conventions
   - Write unit tests for new features
   - Update documentation as needed

4. **Commit your changes**
   ```bash
   git commit -m "Add: Brief description of your changes"
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Provide a clear description of changes
   - Link related issues
   - Ensure all tests pass

### Coding Standards

- **Language**: Kotlin with idiomatic patterns
- **Style**: Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Architecture**: Maintain Clean Architecture separation
- **Testing**: Write tests for new features (aim for >80% coverage)
- **Documentation**: Add KDoc comments for public APIs
- **Commits**: Use conventional commits (feat:, fix:, docs:, etc.)

### Pull Request Checklist

- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] New tests added for new features
- [ ] Documentation updated
- [ ] No API keys or secrets committed
- [ ] Follows project coding standards
- [ ] PR description clearly explains changes

---

## Roadmap

### Version 1.1 (Planned)

- [ ] Conversation history management (multiple chats)
- [ ] Export chat history (JSON, TXT)
- [ ] Message editing and regeneration
- [ ] User profile and settings page

### Version 1.2 (Future)

- [ ] Markdown rendering in chat messages
- [ ] Code syntax highlighting
- [ ] Image upload for vision models (GPT-4 Vision)
- [ ] Voice message recording (send audio)

### Version 1.3 (Future)

- [ ] Multi-language support (i18n)
- [ ] Custom AI model selection
- [ ] System prompt customization
- [ ] Token usage tracking and analytics

### Version 2.0 (Future)

- [ ] Cloud sync across devices
- [ ] User authentication (Firebase/Supabase)
- [ ] Shared conversations
- [ ] Advanced image editing with AI

**Suggestions?** Open an issue with the `enhancement` label!

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 ChatBot AI Assistant

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Acknowledgments

### Technologies & Libraries

- [OpenAI](https://openai.com/) - AI models and APIs
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Hilt](https://dagger.dev/hilt/) - Dependency injection
- [Retrofit](https://square.github.io/retrofit/) - HTTP client
- [Room](https://developer.android.com/training/data-storage/room) - Local database
- [Coil](https://coil-kt.github.io/coil/) - Image loading
- [Material Design 3](https://m3.material.io/) - Design system

### Inspiration

- Clean Architecture principles by Robert C. Martin
- Android Architecture Components best practices
- Modern Android development guidelines

### Contributors

Thank you to all contributors who have helped improve this project!

---

## Contact & Support

### Get Help

- **Issues**: [GitHub Issues](https://github.com/yourusername/chatBotAIAssistant/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/chatBotAIAssistant/discussions)
- **Email**: your.email@example.com

### Report a Bug

Found a bug? Please [open an issue](https://github.com/yourusername/chatBotAIAssistant/issues/new) with:

1. **Description**: Clear description of the bug
2. **Steps to Reproduce**: How to trigger the issue
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Screenshots**: If applicable
6. **Environment**: Android version, device model, app version

### Request a Feature

Have an idea? [Open a feature request](https://github.com/yourusername/chatBotAIAssistant/issues/new) with:

1. **Use Case**: Why this feature is needed
2. **Proposed Solution**: How it should work
3. **Alternatives**: Other solutions you've considered

---

<div align="center">

**Made with ❤️ using Kotlin and Jetpack Compose**

⭐ Star this repo if you find it helpful!

[Report Bug](https://github.com/yourusername/chatBotAIAssistant/issues) · [Request Feature](https://github.com/yourusername/chatBotAIAssistant/issues) · [Documentation](https://github.com/yourusername/chatBotAIAssistant/wiki)

</div>
