# Fireplace 4K — Android TV / Google TV

A minimal native Android app for one offline fireplace scene. Choose one video from local storage or a USB drive; the app remembers the selection and loops it continuously. Playback uses AndroidX Media3/ExoPlayer.

## Requirements
- Android Studio (current stable)
- Android SDK Platform 35 (install through SDK Manager if prompted)
- JDK 17 (Android Studio's bundled JBR is recommended)
- Internet on first Gradle sync to download Android Gradle Plugin and Media3 dependencies

## Build the APK
1. Extract this ZIP.
2. In Android Studio, choose **Open** and select the `Fireplace4K` project folder (the folder containing `settings.gradle`).
3. Allow Gradle sync to complete. Install Android SDK Platform 35 if prompted.
4. Select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. The debug APK should be created at `app/build/outputs/apk/debug/app-debug.apk`.
6. Copy the APK to your Android TV / Google TV and install it. You may need to enable installation from unknown apps in the TV settings.

To install with ADB instead, enable Developer options and USB/network debugging, connect to the TV, then run `adb install app/build/outputs/apk/debug/app-debug.apk`.

## Add your one fireplace video
1. Download a video that you are licensed to use. Confirm the actual downloaded file is 3840×2160 if you want genuine 4K.
2. Copy the MP4 to a USB drive or a location visible in the Android system file picker.
3. Open **Fireplace 4K** on the TV and select **4K Fireplace — Add video**.
4. Choose the video. It will start playing immediately and loop continuously. The selected file permission is saved for future launches.
5. Use the playback controls or the Scenes button to change the selected video.

## Suggested media
A broadly compatible starting point is MP4 with H.264 video and AAC audio. Some TVs support 4K HEVC/H.265 more efficiently, but codec/profile support varies by model. If a video fails to play, try an H.264 MP4. The app does not upscale HD footage to 4K; actual output depends on the source, device decoder, and display path.

## Notes
- Exactly one scene slot is included. No video is bundled in the APK, keeping the install small.
- This is the Android source project, not a precompiled APK. This environment does not have the Android SDK/Gradle build toolchain installed, so the APK must be compiled in Android Studio.
- App ID: `com.example.fireplace4k`. Change it before public release if desired.
