# Kritam

MyNotes is a modern Android notes and to-do application built with Kotlin, Jetpack Compose, Room Database, MVVM architecture, and WorkManager-based reminders.

## App Name
MyNotes

## Overview
Kritam is a clean, minimal productivity app designed to help users manage daily tasks efficiently. It supports creating, viewing, editing, searching, filtering, deleting, and marking notes as done. The app also includes reminder scheduling so important tasks can notify the user at a chosen time.

## Features
- Create notes with title, description, and date/time reminder selection
- Auto-save creation timestamp
- View all notes in a modern card-based LazyColumn layout
- Sort notes by date descending
- Mark notes as done with a checkbox and completion timestamp
- Filter notes by All, Pending, and Done tabs
- Show pending count badge in the top bar
- Search notes by title and description
- Edit notes with a tap
- Swipe to delete notes
- WorkManager reminders with local notifications
- Light theme with a clean teal aesthetic

## Tech Stack
- Kotlin
- Jetpack Compose
- Material 3
- MVVM Architecture
- Room Database
- StateFlow + Coroutines
- Navigation Compose
- WorkManager

## Architecture
The project follows a clean MVVM structure:
- ViewModel handles UI state and user actions
- Repository exposes data access to the database
- Room DAO manages persistent note storage
- Compose UI renders the screens and interactions

## Project Structure
- `app/src/main/java/com/kritam/mynotes/MainActivity.kt` – app entry point and navigation setup
- `app/src/main/java/com/kritam/mynotes/data/Note.kt` – Room entity model
- `app/src/main/java/com/kritam/mynotes/data/NoteDao.kt` – database access methods
- `app/src/main/java/com/kritam/mynotes/data/NoteDatabase.kt` – Room database configuration
- `app/src/main/java/com/kritam/mynotes/repository/NoteRepository.kt` – repository layer
- `app/src/main/java/com/kritam/mynotes/ui/viewmodel/NoteViewModel.kt` – state and business logic
- `app/src/main/java/com/kritam/mynotes/ui/screen/NoteListScreen.kt` – list and filter UI
- `app/src/main/java/com/kritam/mynotes/ui/screen/AddEditNoteScreen.kt` – add/edit task screen
- `app/src/main/java/com/kritam/mynotes/ui/component/NoteItem.kt` – reusable note card UI
- `app/src/main/java/com/kritam/mynotes/work/ReminderWorker.kt` – reminder notification worker

## Getting Started
1. Open the project in Android Studio.
2. Let Gradle sync the dependencies.
3. Connect an Android device or start an emulator.
4. Run the app.

## Notes
This project is designed as a lightweight, production-style mobile task manager for personal productivity and learning. It demonstrates proper Compose UI practices, local persistence, and background reminder scheduling.

## License
This project is open source and available for educational and personal use.
