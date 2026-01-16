# KUET Academic Portal

**KUET Academic Portal** is a comprehensive Android application designed to streamline academic management for Khulna University of Engineering & Technology (KUET). It bridges the gap between administration, faculty, and students by providing a centralized platform for academic activities.

## Features

### Admin Features
- **Dashboard:** A central hub for managing the entire system.
- **Student Management:** Add, update, or remove student profiles.
- **Attendance Analytics:** Visualize attendance data using interactive charts.
- **Result Management:** Publish and manage academic results.
- **Routine Management:** specific class routines dynamically.
- **Notice Board:** Post important announcements and notices instantly.
- **Assignment Management:** Create and track assignments.

### Student Features (Planned/Inferred)
- **View Routine:** Access daily class schedules.
- **Check Results:** View academic performance and grades.
- **Attendance Status:** Track personal attendance records.
- **Digital Notice Board:** Stay updated with university announcements.
- **Assignments:** View and submit assignments.

### General Features
- **Secure Authentication:** Integrated with Firebase Authentication for secure login.
- **Real-time Updates:** Powered by Cloud Firestore for instant data synchronization.
- **Contact Directory:** Easy access to faculty and staff contact information.
- **Interactive UI:** Built with Material Design components and intuitive layouts.

## Tech Stack

- **Language:** Java (Native Android)
- **Minimum SDK:** API 24 (Android 7.0 Nougat)
- **Compile SDK:** API 36
- **Architecture:** MVVM / MVC (based on implementation)
- **Database:** Firebase Cloud Firestore (NoSQL)
- **Authentication:** Firebase Authentication
- **Build Tool:** Gradle

## Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/KUET_academic_portal.git
    ```
2.  **Open in Android Studio:**
    - Open Android Studio.
    - Select "Open an existing Android Studio project".
    - Navigate to the cloned directory.
3.  **Firebase Setup:**
    - Go to the [Firebase Console](https://console.firebase.google.com/).
    - Create a new project.
    - Add an Android app with the package name `com.example.kuet_academic_portal`.
    - Download the `google-services.json` file.
    - Place `google-services.json` inside the `app/` folder of your project.
    - Enable **Authentication** (Email/Password) and **Cloud Firestore** in the Firebase Console.
4.  **Sync Gradle:**
    - Let Android Studio sync the project dependencies.
5.  **Run the App:**
    - Connect an Android device or start an emulator.
    - Click the "Run" button (Shift+F10).

## Contributing

Contributions are welcome! If you have suggestions for improvements or bug fixes:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature-branch`).
3.  Make your changes.
4.  Commit your changes (`git commit -m "Add some feature"`).
5.  Push to the branch (`git push origin feature-branch`).
6.  Open a Pull Request.




