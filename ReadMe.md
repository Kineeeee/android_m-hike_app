# M-hike Application (Coursework)

M-hike is an Android application designed for hikers to manage their hiking adventures. It allows users to record hike details, track observations, and maintain a log of their outdoor experiences. This project is developed as part of a coursework assignment.

## Features

*   **Manage Hikes**:
    *   **Add Hike**: Enter details such as name, location, date, parking availability, length, difficulty, and description.
    *   **Edit Hike**: Update existing hike information.
    *   **View Details**: View comprehensive details of a specific hike.
    *   **Delete Hike**: Remove hikes from the database.
*   **Manage Observations**:
    *   **Add Observation**: Record observations for a specific hike, including time and comments.
    *   **Edit Observation**: Modify existing observations.
    *   **Delete Observation**: Remove observations from the database.
*   **Search**: Search for hikes by name.
*   **Filter**: Filter hikes.
*   **Database**: Uses SQLite (via `DatabaseHelper`) to store data locally.

## Tech Stack

*   **Language**: Java
*   **Minimum SDK**: API 34 (Android 14)
*   **Target SDK**: API 36 (Android 16 - Preview/Latest)
*   **Build System**: Gradle (Kotlin DSL)

## Prerequisites

Before you begin, ensure you have the following installed:

*   [Android Studio](https://developer.android.com/studio) (Recommended)
*   Java Development Kit (JDK) 11 or higher
*   Android SDK (API Level 34+)

## Compilation

To compile the project, you can use the Gradle wrapper included in the repository.

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd Coursework
    ```

2.  **Build the project**:
    Open a terminal in the project root and run:
    
    *   **Windows**:
        ```cmd
        gradlew.bat assembleDebug
        ```
    *   **macOS / Linux**:
        ```bash
        ./gradlew assembleDebug
        ```

    This will generate an APK file in `app/build/outputs/apk/debug/app-debug.apk`.

## Installation

### Option 1: Via Android Studio (Recommended)

1.  Open **Android Studio**.
2.  Select **Open** and navigate to the `Coursework` directory.
3.  Wait for Gradle sync to complete.
4.  Connect an Android device or start an Android Emulator.
5.  Click the **Run** button (green play icon) or press `Shift + F10`.

### Option 2: Via Command Line

If you have a device connected or an emulator running:

*   **Windows**:
    ```cmd
    gradlew.bat installDebug
    ```
*   **macOS / Linux**:
    ```bash
    ./gradlew installDebug
    ```

## Usage Instructions

1.  **Home Screen**: Displays a list of all recorded hikes. You can delete all data or search for specific hikes here.
2.  **Adding a Hike**: Click the "Add" button (usually a FAB or menu item) to open the form. Fill in the required details (Name, Location, Date, etc.) and save.
3.  **Viewing Details**: Tap on any hike in the list to view its full details.
4.  **Observations**: From the Hike Detail screen, you can view associated observations or add new ones.
5.  **Editing**: Use the "Edit" options within the detail views to modify information.

## Project Structure

*   `app/src/main/java/com/example/coursework/activities`: Contains the Activity classes (screens) of the app.
*   `app/src/main/java/com/example/coursework/data`: Likely contains the DatabaseHelper and model classes.
*   `app/src/main/java/com/example/coursework/adapters`: Contains adapters for RecyclerViews/Lists.
*   `app/src/main/res/layout`: XML layout files for the UI.

## Troubleshooting

*   **SDK Location**: If you encounter a "SDK location not found" error, create a `local.properties` file in the root directory with the path to your Android SDK:
    ```properties
    sdk.dir=/path/to/your/android/sdk
    ```
*   **Gradle Sync**: If dependencies fail to resolve, try File > Sync Project with Gradle Files in Android Studio.
