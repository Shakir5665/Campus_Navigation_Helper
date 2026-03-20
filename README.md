# Campus Navigation Helper

Welcome to the **Campus Navigation Helper** project! This is an Android mobile application designed to help students and visitors find campus locations and view step-by-step navigation instructions offline using SQLite.

## Features
- **User Authentication**: Secure login and registration.
- **Hierarchical Role-Based Access Control**: Supports Master Admin, Admin, and Normal User roles.
- **Location Management**: Admins can manage campus locations, blocks, and step-by-step navigation steps.
- **Offline Navigation**: View step-by-step directions without the need for Google Maps or an internet connection.
- **Favorites**: Users can save frequently visited locations to their favorites.
- **Categorized Display**: Locations are displayed by category using RecyclerView.
- **Modern UI**: Material Design, university-style color theme, smooth animations, and support for both dark and light modes.

## Team Members

| Name | Registration Number |
| :--- | :--- |
| MNM.Sakir | ICT/2022/059 |
| NM.Baahir | ICT/2022/045 |
| MSF.Farsana | ICT/2022/062 |

## Technologies Used
- Java
- Android SDK
- SQLite Database
- Material Components for Android

## Setup Instructions

1. **Open in Android Studio:**
   - Launch Android Studio.
   - Select **Open an existing Android Studio project**.
   - Navigate to the project directory and select it.
2. **Build the Project:**
   - Allow Gradle to sync and build the project.
3. **Run the App:**
   - Connect an Android device or start an emulator.
   - Click the **Run** button (green play icon) in Android Studio to install and run the app.

## Architecture & Database
The application uses SQLite to manage offline data which includes user accounts, admin secret codes, locations, navigation steps, and favorite places. The architecture follows modern Android development principles ensuring a clean and manageable codebase.
