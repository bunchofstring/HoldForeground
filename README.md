This repo contains an Android app implementation that satisfies some relatively uncommon requirements. Although the app behavior would be dangerous or disruptive on a personal device, it is useful in the context of manufacturing, retail, etc. Think of it as a proof of concept.

# Features
1. GUI fills the primary display completely
2. GUI remains in the foreground and blocks other apps from becoming visible
3. Starts automatically on boot*
4. Recovers from a crash automatically**

\* Due to Android-security restrictions, the app must be manually started initially after installation.
<br>\*\* Android OS includes countermeasures against against crash loops (i.e. backoff delay).

# Requirements
**Non-Functional Requirements**
1. Compatible with Android 12L, 13, and 14
2. Integrated as a userspace app (as opposed to a privileged system app which is typically build into the Android-based platform itself)
3. Discourage the OS from killing the app in the event of memory and/or CPU pressure*
4. Implemented in Kotlin
5. GUI implemented Jetpack Compose

\* Note that 100% availability would require integration as a privileged system app

**Functional Requirements**
1. Fill all available screen real estate on the primary display
2. Prevent other apps from becoming visible (even if the other app is launched programmatically)
3. Initialize when the system starts
4. In the event of a crash, the GUI should reappear within a moment

# Build and Install
Recommendation is to use Android Studio. Details of how to do this are not the focus of this documentation.

# Operations
Start the service
```
adb shell am start-foreground-service com.example.holdforeground/.DaemonService
```
Show the GUI
```
adb shell am start-foreground-service -a ACTION_SHOW com.example.holdforeground/.DaemonService
```
Dismiss the GUI
```
adb shell am start-foreground-service -a ACTION_DISMISS com.example.holdforeground/.DaemonService
```
# Testing
Simulate a crash
```
adb shell am crash com.example.holdforeground
```
