MyRuns3 – Documentation, References, and AI Use

Author: Navdeep Bilin
Course: CMPT 362 – Mobile Development
Project: MyRuns3
Instructor References: Week 8–9 Lecture Slides (Threads, Coroutines, SQLite Databases, MVVM Architecture)

File: data/ExerciseEntryEntity.kt

Lecture References:
Room Database lectures (Entity structure, annotations) and RoomDatabaseKotlin demo example.

Adapted Parts:
Defined all MyRuns-required attributes (input type, activity type, duration, distance, calories, heart rate, etc.) and stored them in meters/seconds for internal consistency.
Implemented @PrimaryKey(autoGenerate = true) and added nullable fields for future labs (e.g., MR4 GPS).

AI-Generated:
None – fully lecture-based Room entity following instructor examples.

File: data/ExerciseEntryDao.kt

Lecture References:
DAO interface setup from lecture slides and RoomDatabaseKotlin.zip example (Week 9).

Adapted Parts:
Added suspend functions for insert, delete, and query operations (getAllEntries(), getEntryById()), returning a Flow to auto-update UI.

AI-Generated:
None – pure adaptation of Room DAO pattern from class.

File: data/ExerciseRepository.kt

Lecture References:
Repository pattern shown in lecture (“extra abstraction layer” demo).

Adapted Parts:
Integrated coroutine dispatchers for database operations to keep UI responsive and added Flow → LiveData conversion for easy observation in ViewModels.

AI-Generated:
None – built from lecture’s repository model structure.

File: data/MyRunsDatabase.kt

Lecture References:
RoomDatabase singleton pattern and @Database annotation example (CommentDatabase from lecture).

Adapted Parts:
Implemented synchronized singleton using @Volatile and Room.databaseBuilder, referencing exercise_entry_table.

AI-Generated:
None – direct lecture example restructured for this entity type.

File: util/Formatters.kt

Lecture References:
Date/time formatting approach based on Week 3–4 resource formatting lecture.

Adapted Parts:
Created helper dateThenTime() for uniform display order (Date → Time) to match demo outputs.

AI-Generated:
None – formatting logic derived from lecture pattern.

File: util/Units.kt

Lecture References:
SharedPreferences lecture (Week 4) for detecting preference values; Room lecture for storing values in consistent base units (meters).

Adapted Parts:
Created unified helper for distance formatting and conversions between metric and imperial units. Added formatDurationFromSeconds() to display durations in minutes and seconds.

AI-Generated (Purpose & Justification):
Medium use – AI assisted in refining compatibility between different preference storage methods (boolean vs ListPreference “pref_units”).
this ensures the app dynamically adapts regardless of preference type, solving inconsistencies that occurred across Android versions.

File: viewmodel/DisplayEntryViewModel.kt

Lecture References:
MVVM demo (ThreadViewModelKotlin.zip and RoomDatabaseKotlin.zip).

Adapted Parts:
Fetches and exposes a single entry as LiveData. Added coroutine scope for delete operations.

AI-Generated:
None – standard ViewModel implementation from lecture.

File: viewmodel/HistoryViewModel.kt

Lecture References:
Same MVVM lecture reference.

Adapted Parts:
Converts DAO Flow to LiveData for automatic UI refresh in HistoryFragment.

AI-Generated:
None – minimal lecture-based adaptation.

File: ManualEntryActivity.kt

Lecture References:
Dialogs (DatePicker, TimePicker, AlertDialog) and state restoration from early MR2 lectures. Coroutines reference for database insertion (Week 8).

Adapted Parts:
Reused dialog structure from MR2 but extended for new project scope:

Added data persistence with RoomRepository.

Integrated user unit preference from Settings.

Converted decimal minutes to seconds (e.g., 55.25 → 55m 15s).

Displayed brief “#entry saved” confirmation and returned to Start tab.

AI-Generated (Purpose & Justification):
Medium use – Assisted in correct float-to-time conversion logic and defensive rounding for “mins/secs” output.
AI also helped with coroutine-safe Room insertion and confirmation Toast timing.

File: HistoryFragment.kt

Lecture References:
Fragment-ListView example from lecture; DAO observation with LiveData/Flow.

Adapted Parts:

Added SharedPreferences listener to re-render distances dynamically when units change.

Used ViewLifecycleOwner for safe LiveData observation (solves rotation edge case).

Customized list subtitle to show distance (km/mi) and formatted duration.

AI-Generated (Purpose & Justification):
Light use – AI helped implement a clean SharedPreferences listener that triggers adapter refresh automatically when unit preference changes, matching assignment demo behavior.

File: DisplayEntryActivity.kt

Lecture References:
Lecture examples on Activity-Fragment communication and SharedPreferences usage (Week 9).

Adapted Parts:

Displays individual entry details and handles deletion through ViewModel.

Added SharedPreference listener to instantly update displayed distance when unit preference changes.

Corrected Date/Time formatting order.

AI-Generated (Purpose & Justification):
Medium use – AI guided creation of a safe and efficient SharedPreferences.OnSharedPreferenceChangeListener that rebinds UI without restarting the activity.
Simplified binding logic through a reusable bindUi() helper for clear lifecycle handling.

Additional Notes

All XML files were adapted from MR2 layouts for design consistency, adding new components such as delete buttons, back buttons, and layout spacing.

No generative design or code replacement was performed; all additions align with lecture structure and Android best practices.

The project compiles and runs cleanly under Android API 30–34 emulators.

Summary of AI Assistance
File	        AI Use Level	  Purpose
Units.kt	Medium	Add robust preference detection to handle both ListPreference (“pref_units”) and boolean keys; fix cross-version issues.
ManualEntryActivity.kt	Medium	Convert decimal duration to minutes/seconds with safe rounding and help integrate coroutine-based DB insertion.
HistoryFragment.kt	Light	Implement SharedPreferences listener to refresh displayed units dynamically without reloading the Fragment.
DisplayEntryActivity.kt	Medium	Ensure instant UI re-render when user toggles km/mi preference; improve lifecycle-safe SharedPreferences handling.

All AI assistance was targeted, minimal, and fully reviewed — used solely for logic refinement and code safety, not as a design substitute.