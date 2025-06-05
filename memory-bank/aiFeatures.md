# AI Features - On-Device Intelligence

## Overview
Time Fomo integrates Google AI Edge Gallery capabilities to provide on-device AI functionality without compromising user privacy or requiring internet connectivity.

## AI Gallery Integration

### Architecture
- **Source**: Google AI Edge Gallery (https://github.com/google-ai-edge/gallery)
- **Integration**: Selective component copying with package name updates
- **Location**: `app/src/main/java/com/jahi/pipelinetest/gallery/`
- **Navigation**: Added as 3rd tab in bottom navigation (index 2)

### UI Components
- **GalleryScreen.kt**: Main showcase interface with feature cards
- **Common Components**: 8 core UI components from Gallery project
- **Package Structure**: All components use `com.jahi.pipelinetest.gallery` namespace
- **Resource Integration**: Gallery fonts, drawables, and UI resources

### Gallery Features Showcase
1. **LLM Chat** - ✅ Available (integrated with widget Generate button)
2. **Image Classification** - 🔄 Coming Soon
3. **Text Classification** - 🔄 Coming Soon  
4. **Image Generation** - 🔄 Coming Soon

## AI-Powered Widget Generation

### Implementation
- **Use Case**: `GenerateMotivationalTextUseCase`
- **Model Support**: MediaPipe LLM (.task format models)
- **Discovery**: Automatic model detection in `__imports` directory
- **Integration**: All three widgets (Daily, Circular, Event) support AI generation

### Technical Details
```kotlin
// Model Discovery
- Searches: /Android/data/com.jahi.pipelinetest/files/__imports/
- Supports: .task and .bin files (>10MB)
- Creates: Model objects with imported=true flag
- Path: "__imports/modelname.task"

// AI Generation Flow
1. Find downloaded LLM model
2. Initialize MediaPipe LLM (2 min timeout)
3. Run inference with few-shot prompts (30 sec timeout)
4. Clean response (remove prefixes/suffixes)
5. Fallback to hardcoded sentences if failed
```

### Prompt Engineering
- **Few-shot prompting** with examples for better responses
- **Response cleaning** to extract pure motivational quotes
- **Fallback strategy** ensures functionality without AI models

Example prompt:
```
"Create an inspiring quote about perseverance. Example: Every setback is a setup for a comeback. Your response:"
```

### Error Handling
- **Initialization timeout**: 2 minutes for model loading
- **Generation timeout**: 30 seconds for text creation  
- **JSON escaping**: Handles quotes in AI responses for TTS
- **Graceful fallback**: Uses hardcoded sentences when AI fails
- **Comprehensive logging**: DEBUG_FLOW tagged for debugging

## Text-to-Speech Integration

### ElevenLabs Integration
- **API**: ElevenLabs text-to-speech service
- **Security**: Encrypted API key storage using EncryptedSharedPreferences
- **Voice**: Pre-configured voice ID (EXAVITQu4vr4xnSDxMaL)
- **Format**: MP3 audio generation and playback

### JSON Safety
- **Escaping**: Proper quote and newline escaping for JSON payload
- **Error Handling**: HTTP response code checking and exception handling
- **Cleanup**: Automatic temporary file deletion after playback

## Model Management

### Supported Models
- **Primary**: Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task (528MB)
- **Format**: MediaPipe .task files
- **Location**: External files directory `__imports` folder
- **Size**: Models >10MB automatically detected

### Model Lifecycle
1. **Discovery**: Scan `__imports` and versioned directories
2. **Initialization**: Create Model objects with proper paths
3. **Loading**: MediaPipe LLM initialization with GPU/CPU backend
4. **Inference**: Text generation with streaming responses
5. **Cleanup**: Automatic resource management

## Privacy & Performance

### On-Device Processing
- **No Internet Required**: All AI inference runs locally
- **Privacy First**: No data sent to external servers
- **Model Storage**: Local file system storage
- **Offline Capable**: Full functionality without network

### Performance Optimization
- **Model Reuse**: Initialize once, use multiple times
- **Timeouts**: Prevent hanging operations
- **Memory Management**: Proper cleanup and resource release
- **Background Threading**: Non-blocking UI operations

## Integration Points

### Widget Integration
- **Generate Button**: All widgets support AI text generation
- **Audio Output**: TTS integration for voice feedback
- **Fallback**: Hardcoded sentences ensure reliability

### Gallery Showcase
- **Feature Display**: Interactive cards showing AI capabilities
- **Status Indicators**: Clear available/coming soon states
- **Navigation**: Seamless integration with main app flow

## Future Enhancements

### Planned Features
1. **Image Classification**: Photo analysis and categorization
2. **Text Classification**: Document and message categorization
3. **Image Generation**: AI-powered image creation
4. **Voice Input**: Speech-to-text for hands-free interaction

### Technical Roadmap
- **Model Variety**: Support for multiple AI model types
- **Performance**: Optimization for larger models
- **UI Enhancement**: Better model management interface
- **Batch Processing**: Multiple AI operations support

## Development Guidelines

### AI Code Standards
- **Use Cases**: All AI operations must go through Use Case pattern
- **Error Handling**: Comprehensive try-catch with fallbacks
- **Logging**: DEBUG_FLOW tagged logging for all AI operations
- **Resource Management**: Proper model initialization and cleanup
- **Testing**: Verify both AI success and fallback scenarios

### Model Integration Process
1. **Discovery**: Implement model finding logic
2. **Initialization**: MediaPipe setup with proper configuration
3. **Inference**: Streaming response handling
4. **Cleanup**: Response processing and resource cleanup
5. **Fallback**: Ensure graceful degradation

## Security Considerations

### API Key Management
- **Encryption**: EncryptedSharedPreferences for sensitive data
- **Fallback**: Regular SharedPreferences if encryption fails
- **Validation**: Check API key before making requests
- **Error Handling**: Graceful failure without exposing keys

### Model Security
- **Local Storage**: Models stored in app-specific directories
- **Access Control**: No external access to model files
- **Validation**: File size and format checks before loading
- **Sandboxing**: MediaPipe provides model execution isolation