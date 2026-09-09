# Speed Test App for Android

A modern Android application for testing internet speed using the Ookla Speedtest API.

## Features

✅ Real-time speed testing (Download, Upload, Ping)
✅ Integration with Ookla Speedtest API
✅ Historical test results storage with Room Database
✅ Server selection capability
✅ Average speed statistics
✅ Beautiful Material Design UI
✅ Coroutines for non-blocking operations
✅ MVVM Architecture

## Project Structure

```
app/src/main/
├── kotlin/com/speedtest/app/
│   ├── network/
│   │   ├── SpeedtestApiService.kt      # Retrofit API interface
│   │   └── RetrofitClient.kt           # Retrofit configuration
│   ├── database/
│   │   ├── SpeedTestEntity.kt          # Room entity
│   │   ├── SpeedTestDao.kt             # Room DAO
│   │   └── SpeedTestDatabase.kt        # Room database
│   ├── repository/
│   │   └── SpeedTestRepository.kt      # Data layer
│   ├── viewmodel/
│   │   └── SpeedTestViewModel.kt       # MVVM ViewModel
│   └── ui/
│       └── MainActivity.kt             # Main activity
├── res/
│   ├── layout/
│   │   └── activity_main.xml
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
└── AndroidManifest.xml
```

## Dependencies

- **Retrofit 2.9.0** - HTTP client
- **OkHttp 4.11.0** - HTTP interceptors and logging
- **Kotlin Coroutines 1.7.1** - Async operations
- **Room Database 2.5.2** - Local data persistence
- **AndroidX Lifecycle** - ViewModel & LiveData
- **Material Design Components**

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/hkhamza336-jpg/Speed-test-app-for-Android.git
cd Speed-test-app-for-Android
```

### 2. Open in Android Studio
- Open Android Studio
- Select "Open an existing Android Studio project"
- Navigate to the cloned repository

### 3. Build the Project
```bash
./gradlew build
```

### 4. Run on Emulator or Device
```bash
./gradlew installDebug
```

## Usage

1. Launch the app on your Android device
2. Tap "Start Speed Test" button
3. Wait for the test to complete
4. View your Download/Upload speeds and Ping
5. Results are automatically saved to local database

## API Endpoints Used

- **Server List**: `https://www.speedtest.net/api/js/servers`
- **Configuration**: `https://www.speedtest.net/api/js/config`

## Permissions Required

- `INTERNET` - To fetch speed test data
- `ACCESS_NETWORK_STATE` - To check network connectivity
- `ACCESS_FINE_LOCATION` - Optional, for server selection based on location
- `ACCESS_COARSE_LOCATION` - Optional, for server selection based on location

## Architecture

This app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Model**: Room database entities and Retrofit models
- **View**: Activities and layouts (MainActivity, activity_main.xml)
- **ViewModel**: SpeedTestViewModel with LiveData observables
- **Repository**: Single source of truth for data operations

## Future Enhancements

- [ ] Detailed statistics and charts
- [ ] Test history visualization
- [ ] Server map with geolocation
- [ ] Background testing with notifications
- [ ] Test scheduling
- [ ] Share results functionality
- [ ] Dark mode support
- [ ] Multi-threading optimization

## License

MIT License - Feel free to use and modify

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Support

If you encounter any issues or have suggestions, please open an issue in the GitHub repository.

## Acknowledgments

- Ookla for providing the Speedtest API
- Google for Android and Material Design
- JetBrains for Kotlin language
