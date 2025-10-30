package com.example.navdeep_bilin_myruns3

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class ManualEntryActivity : AppCompatActivity() {

    private lateinit var rowDate: View
    private lateinit var rowTime: View
    private lateinit var rowDuration: View
    private lateinit var rowDistance: View
    private lateinit var rowCalories: View
    private lateinit var rowHeartRate: View
    private lateinit var rowComment: View   // adaption of my own, new comment row for project

    private lateinit var tvDateValue: TextView
    private lateinit var tvTimeValue: TextView
    private lateinit var tvDurationValue: TextView
    private lateinit var tvDistanceValue: TextView
    private lateinit var tvCaloriesValue: TextView
    private lateinit var tvHeartRateValue: TextView
    private lateinit var tvCommentValue: TextView   // adaptation to match new comment row

    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    //  Date dialog state
    private var dateDialog: DatePickerDialog? = null
    private var isDateDialogShowing = false
    private var dateYear = 0
    private var dateMonth = 0
    private var dateDay = 0

    //  Time dialog state
    private var timeDialog: TimePickerDialog? = null
    private var isTimeDialogShowing = false
    private var timeHour = 0
    private var timeMinute = 0

    companion object {
        private const val STATE_DATE_SHOWING = "state_date_showing"
        private const val STATE_DATE_YEAR = "state_date_year"
        private const val STATE_DATE_MONTH = "state_date_month"
        private const val STATE_DATE_DAY = "state_date_day"

        private const val STATE_TIME_SHOWING = "state_time_showing"
        private const val STATE_TIME_HOUR = "state_time_hour"
        private const val STATE_TIME_MINUTE = "state_time_minute"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)

        // lecture reference for findViewById
        supportActionBar?.title = getString(R.string.app_name)

        // Rows
        rowDate = findViewById(R.id.rowDate)
        rowTime = findViewById(R.id.rowTime)
        rowDuration = findViewById(R.id.rowDuration)
        rowDistance = findViewById(R.id.rowDistance)
        rowCalories = findViewById(R.id.rowCalories)
        rowHeartRate = findViewById(R.id.rowHeartRate)
        rowComment = findViewById(R.id.rowComment)  // previous adaptation

        // Value views
        tvDateValue = findViewById(R.id.tvDateValue)
        tvTimeValue = findViewById(R.id.tvTimeValue)
        tvDurationValue = findViewById(R.id.tvDurationValue)
        tvDistanceValue = findViewById(R.id.tvDistanceValue)
        tvCaloriesValue = findViewById(R.id.tvCaloriesValue)
        tvHeartRateValue = findViewById(R.id.tvHeartRateValue)
        tvCommentValue = findViewById(R.id.tvCommentValue)  // adapted as well from lectures

        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Restore state after rotation extended to dialogs and fields
        // referenced from lecture but written in my own implementation
        savedInstanceState?.let { b ->
            // Date
            isDateDialogShowing = b.getBoolean(STATE_DATE_SHOWING, false)
            dateYear = b.getInt(STATE_DATE_YEAR, 0)
            dateMonth = b.getInt(STATE_DATE_MONTH, 0)
            dateDay = b.getInt(STATE_DATE_DAY, 0)
            if (isDateDialogShowing) {
                showDateDialog(dateYear, dateMonth, dateDay)
            }

            // Time
            isTimeDialogShowing = b.getBoolean(STATE_TIME_SHOWING, false)
            timeHour = b.getInt(STATE_TIME_HOUR, 0)
            timeMinute = b.getInt(STATE_TIME_MINUTE, 0)
            if (isTimeDialogShowing) {
                showTimeDialog(timeHour, timeMinute)
            }
        }

        // Open Date picker dialog on a row click using the defaults of the calendar
        // adapted from lecture content
        rowDate.setOnClickListener {
            val cal = Calendar.getInstance()
            showDateDialog(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        // Open Time picker dialog (12-hour, with AM/PM)
        // also adapted from lecture content
        rowTime.setOnClickListener {
            val cal = Calendar.getInstance()
            showTimeDialog(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        }

        // adapted from lectures; reusable numerical input dialogs for several rows
        rowDuration.setOnClickListener {
            showIntegerDialog(title = "Enter the duration", hint = "Minutes")
        }
        rowDistance.setOnClickListener {
            showIntegerDialog(title = "Enter the distance", hint = "Meters")
        }
        rowCalories.setOnClickListener {
            showIntegerDialog(title = "Enter the calories", hint = "kcal")
        }
        rowHeartRate.setOnClickListener {
            showIntegerDialog(title = "Enter the heart rate", hint = "BPM")
        }

        // Comment dialog with multiline input
        rowComment.setOnClickListener {
            showCommentDialog()
        }

        // adapted from lecture content, to return to previous screen
        btnSave.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }
    }

    // lecture referenced for date picker dialog to use with listeners
    private fun showDateDialog(year: Int, month: Int, day: Int) {
        dateDialog?.dismiss()
        dateDialog = DatePickerDialog(
            this,
            { _, y, m, d ->
                dateYear = y
                dateMonth = m
                dateDay = d
            },
            year, month, day
        ).apply {
            setOnShowListener { isDateDialogShowing = true } // allows to track visibility during rotation
            setOnDismissListener { isDateDialogShowing = false } // also adapted from lecture
            show()
        }
    }

    private fun showTimeDialog(hour24: Int, minute: Int) {
        // adapted from lecture to fit my implementation
        // forces a 12 hour view for consistency
        timeDialog?.dismiss()
        timeDialog = TimePickerDialog(
            this,
            { _, hOfDay, m ->
                timeHour = hOfDay
                timeMinute = m
            },
            hour24, minute, false
        ).apply {
            setOnShowListener {
                isTimeDialogShowing = true
                try {
                    // use of AI here is heavy, used it to find andoird by the internal id and call
                    // to set false to the 24 hour view, not covered in lecture
                    val tpId = resources.getIdentifier("timePicker", "id", "android")
                    val tp = findViewById<android.widget.TimePicker?>(tpId)
                    tp?.setIs24HourView(false)
                } catch (_: Throwable) { }
            }
            setOnDismissListener { isTimeDialogShowing = false }
            show()
        }
    }

    private fun showIntegerDialog(title: String, hint: String) {

        // adapted to my implementation, referened from lecture, for a shared numerical
        // dialog helper that includes a max length cap
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setHint(hint)
            filters = arrayOf(InputFilter.LengthFilter(6))
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("OK") { dlg, _ -> dlg.dismiss() } // lecture reference for alert dialog pattern
            .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
            .show()
    }

    private fun showCommentDialog() {

        // adapted to my own implementation from lectures
        // multi-line text dialog for comments
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setHint("Enter your comment")
        }

        AlertDialog.Builder(this)
            .setTitle("Enter Comment")
            .setView(input)
            .setPositiveButton("OK") { dlg, _ -> dlg.dismiss() }
            .setNegativeButton("Cancel") { dlg, _ -> dlg.dismiss() }
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        // adapted from lecture but in my own implementation
        // Save current Date dialog values
        dateDialog?.datePicker?.let {
            dateYear = it.year
            dateMonth = it.month
            dateDay = it.dayOfMonth
        }
        outState.putBoolean(STATE_DATE_SHOWING, dateDialog?.isShowing == true || isDateDialogShowing)
        outState.putInt(STATE_DATE_YEAR, dateYear)
        outState.putInt(STATE_DATE_MONTH, dateMonth)
        outState.putInt(STATE_DATE_DAY, dateDay)

        // Save current Time dialog values, reads from picker when visiable
        timeDialog?.let { dlg ->
            if (dlg.isShowing) {
                try {
                    // minimal AI use here; reading hour and minute from picker
                    val tpId = resources.getIdentifier("timePicker", "id", "android")
                    val tp = dlg.findViewById<android.widget.TimePicker?>(tpId)
                    if (tp != null) {
                        timeHour = tp.hour
                        timeMinute = tp.minute
                    }
                } catch (_: Throwable) { }
            }
        }
        outState.putBoolean(STATE_TIME_SHOWING, timeDialog?.isShowing == true || isTimeDialogShowing)
        outState.putInt(STATE_TIME_HOUR, timeHour)
        outState.putInt(STATE_TIME_MINUTE, timeMinute)

        super.onSaveInstanceState(outState)
    }
}
