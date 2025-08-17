# 📱 WinksyCall – WebRTC Based Video/Audio Calling App

WinksyCall is a real-time peer-to-peer video/audio calling app built with **Kotlin**, **Jetpack Compose**, **Firebase** and **WebRTC**.

It provides smooth one-to-one communication with presence detection, in-app notifications, and essential call controls.

- 📇 Smart Contact System – Add contacts via email with real-time presence (online/offline) status.
- 🔔 In-App Call Notifications – Instant incoming call alerts inside the app.
- 🎥 Peer-to-Peer Calling – Secure WebRTC video & audio calls with Firebase signaling.
- 🎛️ Essential Call Controls – Mute/unmute, flip camera, and end call.
- 🗑️ Swipe-to-Delete Contacts – Manage contacts easily with swipe actions synced to Firebase.

---

## 🚀 Features

| Feature | Description |
|--------|-------------|
| 🧱 **Authentication**| Firebase Authentication with Email/Password. Secure user management via Firebase.|
| 🧭 **Contacts** | Each user can create and manage contacts. Contacts stored as email → UID mapping in Firebase Realtime Database. |
| 💾 **Presence System** | Real-time online/offline presence detection for contacts. Built using Firebase Realtime Database and Kotlin Flow for reactive updates. Presence status shown on contact cards (green = online, red = offline).|
| 🔎 **Calling System** | Incoming Call Notifications: Users see in-app notifications for incoming calls. Signaling via Firebase Realtime Database: { Offer/Answer exchange handled using Firebase, ICE candidates shared in real-time for WebRTC connections }|
| 🌐 **WebRTC Integration** | Direct peer-to-peer video/audio communication using WebRTC. |
| 🧹 **UI / UX** | Built entirely with Jetpack Compose. LazyColumn for displaying contacts. ContactCard with call/video buttons and status indicator. Swipe-to-delete for removing contacts from Firebase. Uses LaunchedEffect and DisposableEffect for lifecycle-aware state management.|
| ⚙️ **MVVM Architecture** | Clean separation of logic with ViewModel & Repository |

---

## 📸 Screenshots

| Login / Signup | Home Screen | Incoming Call Screen | Video Call Screen | Firebase Realtime DB Structure |
|----------|-------------|-------------|-------------|-------------|
| <img width="830" height="872" alt="image" src="https://github.com/user-attachments/assets/177d2923-c8ff-4e80-ae24-5f41db2e65a5" />|<img width="830" height="872" alt="image" src="https://github.com/user-attachments/assets/47f229c3-6d78-4d4e-9741-7430a24ab7d9" />|<img width="830" height="872" alt="image" src="https://github.com/user-attachments/assets/2fa9265a-3934-4c60-af9f-159a595e04ce" />|<img width="830" height="872" alt="image" src="https://github.com/user-attachments/assets/6eee170f-6ac3-4dcf-b357-50618cfe553e" />|<img width="1113" height="700" alt="image" src="https://github.com/user-attachments/assets/c49dc3d2-1f9e-4af9-878b-081e924527ef" />|

  




---

## 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose(Material 3)**
- **AndroidX Lifecycle + ViewModel + Kotlin Coroutines + Flow**
- **Jetpack Navigation Compose**
- **Firebase Authentication + Firebase Realtime Database**
- **WebRTC Library: com.mesibo.api:webrtc**
- **PermissionX + Accompanist Permissions**

---




## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://akash-portfolio-max07.web.app/)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/asthetix07/)
[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://x.com/asthetix__07/)
[![Google Drive](https://img.shields.io/badge/Google%20Drive-4285F4?logo=googledrive&logoColor=fff)](https://drive.google.com/file/d/13sQchL2_PeTCFS5jsCZKyiCAk8nUJpsH/view?usp=drive_link/)


![Logo](https://www.gstatic.com/devrel-devsite/prod/vfbd11e784c22f4aaa184963b528a22b42504e1842229d6f6f6b59838a38023ff/android/images/lockup.png)

