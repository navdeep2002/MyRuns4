BELOW IS A README FOR ALL MY FILES IN BRIEF REGARDING (REFERENCES ADAPTATIONS, AND AI USE)

File: MainActivity.kt
    lecture references: toolbar setup from week 3 and findViewById usage (viewPager lec)
    Adapted parts: frag array list was customized for this project's three tabs instead of sample
                    fragments from lecture
    AI-generated: minimally used to help me refine the MyFragmentStateAdapter constructor and 
                    TabLayoutMediator with string array for titles in MR2
    note: AI use here is very minimal, mostly lecture based references

File: HistoryFragment.kt
    Lecture references: based on lecture fragment examples
    adapted parts: layout of fragment_history is custom to this file

File: ManualEntryActivity.kt
    lecture references:  
                        Action bar title with string resources
                        basic dialog patterns for datepicker, timepicker, alerts
                        view look ups with findViewById
                        use of onSaveInstanceState for simple state persistence
    Adapted parts;
                        added comments row and dialog to math needs of the project
                        centralized numerical input using resuable helpers
                        rotation handling by checking if dialogs are visible and restoring when needed
                        just like the demo, showed a 12 hour clock instead of the default 24 hour, 
                            set 24 hour option to false to force 12 hour view
    
    AI - generated:     
                        heavy usage - internal lookup of timepicker platformv view and setonshowlistener
                        to enfore 12 hour mode

                        minimal usage - reading hours and minutes from the picker, minor cleanup
                        in builder usage and helper structure

File: MapActivity.kt
    Lecture references: activity setup with setContentView and button listeners
    Adapted parts:      created own map layout
    AI - generation:    None
    notes:              this was a blank page since we didn't implement any logic here. 

File: MyFragmentStateAdapter.kt
    lecture references: based on lecture 5 material for state adapters on fragments
                        overriding getItemCount and createFragment
    Adapted Parts:      instead of using conditional statements like when / switch
                            to return to fragments, our implementation accepts a list of fragments
                            and returns by index, avoids boilerplate code
    AI-generation:      very light - was suggested to use a list based constructure for cleaner
                        fragment management instead of when branches

File: ProfileActivity.kt
    Lecture references: 
                        camera capture / file provider / activity results contracts (lec 3)
                        shared preferences load and save patterns (lec 3)
                        alertdialog choice for camera or gallery (lec 3-5)
                        using app specific external files for temporary camera file (lec 3)
    Adapted parts:
                        version aware perms request that uses READ, MEDIA, IMAGES, and READ EXTERNAL
                            STORAGE on older versions
                        rotation support for pending image Uri so that the preview survives configuration
                            changes
                        input validation for numerical fields
                        gallery selection with ACTION PICK for a simple user flow
    AI - Generated: 
                        heavy: mediastore persistence that copies ikmage to pictures with relative path. 
                            adds user visible copy in gallery and is not found in lectures
                        light: digit scrub textwatcher and some defensive checks around pickers and 
                            restoration
    notes:             
                        I went with component activity rather than appcompat activity since we haven't
                        needed the action bar in the layout. in the future appcompat may be needed

File: SettingsFragment.kt
    Lecture Preferences: 
                        preferenceFragmentCompat with setPreferencesFromResource
                        preference click listener to start an activity
    Adapted parts: 
                        dynamic summary updates for listPreference
                        summary provider for comment editTextPreference
                        external link preference using Intent.ACTION_VIEW
    AI-generation:
                        light to minimal - summary handling and click listeners

File: StartFragment.kt
    Lecture references: 
                        standard onCreateView inflation
                        binding views with findViewById
                        setting an OnClickListener on a button
    Adapted parts: 
                        spinner logic that checks the selected index whether to open manualentryactivity
                            mapactivity, or settings tab activity
    AI generated:   
                        None
    
note: All xml files were created based on previous MR1 xml's, using the same idea


    
