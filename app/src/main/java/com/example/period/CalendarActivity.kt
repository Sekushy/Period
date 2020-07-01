package com.example.period

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.period.informationList.InformationCardDataHolder
import com.example.period.informationList.InformationListAdapter
import com.example.period.informationList.InformationListViewModel
import com.example.period.informationList.InformationListViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class CalendarActivity : AppCompatActivity() {
    lateinit var calendar : CalendarView
    lateinit var informationListAdapter : InformationListAdapter
    lateinit var informationViewModel : InformationListViewModel
    lateinit var alertDialog: AlertDialog
    lateinit var daysListener: ValueEventListener
    val currentUserUID = FirebaseAuth.getInstance().currentUser!!.uid
    var isOnPeriod = false
    var periodStartDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        calendar = findViewById(R.id.main_calendar)


        val viewModelFactory = InformationListViewModelFactory(application)

        informationViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(InformationListViewModel::class.java)
        setBindingsForUIElement()
        displayDialogWindow(informationViewModel.daysList)
    }

    fun setBindingsForUIElement () {
        findViewById<RecyclerView>(R.id.main_recycler_view).layoutManager = LinearLayoutManager(applicationContext)

        informationListAdapter = InformationListAdapter(
            FirebaseDatabase.getInstance().getReference(currentUserUID).child("MENSTRUATIE"),
            FirebaseDatabase.getInstance().getReference(currentUserUID).child("DAYS"),
            informationViewModel.informationList,
            informationViewModel.daysList
        )
        findViewById<RecyclerView>(R.id.main_recycler_view).adapter = informationListAdapter
        setUpCalendarEvents()
    }

    fun setUpCalendarEvents() {
        calendar.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->
            checkIfUserIsOnPeriod(dayOfMonth, month, year)
        })
    }

    fun addEndOfPeriod(dayOfMonth: Int, month: Int, year: Int) {
        val startDateListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() > 0) {
                    val date = snapshot.children.elementAt(0)
                        .getValue(InformationCardDataHolder::class.java)

                    if (date != null) {
                        val pathToChild = date.startDate
                        periodStartDay = parseDateToInt(date.startDate)
                        if (periodStartDay > dayOfMonth) {
                            Toast.makeText( this@CalendarActivity, "End of period cannot be set before its start", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            if (date.endDate.equals("")) {
                                date.endDate = "$dayOfMonth-$month-$year"
                                FirebaseDatabase.getInstance().getReference(currentUserUID).child("HISTORY").child(pathToChild).setValue(date)
                                FirebaseDatabase.getInstance().getReference(currentUserUID).child("MENSTRUATIE").child(pathToChild).removeValue()
                                FirebaseDatabase.getInstance().getReference(currentUserUID).child("USER").child("onPeriod").setValue(false)
                                Toast.makeText(applicationContext, "New period started", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }

        FirebaseDatabase.getInstance().getReference(currentUserUID).child("MENSTRUATIE").addValueEventListener(startDateListener)
    }

    fun checkIfUserIsOnPeriod(dayOfMonth: Int, month: Int, year: Int) {
        val isOnPeriodListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                isOnPeriod = snapshot.value as Boolean
                if (isOnPeriod) {
                    addEndOfPeriod(dayOfMonth, month, year)
                } else {
                    addNewPeriod(dayOfMonth, month, year)
                }
            }
        }
        FirebaseDatabase.getInstance().getReference(currentUserUID).child("USER").child("onPeriod").addListenerForSingleValueEvent(isOnPeriodListener)
    }

    fun addNewPeriod(dayOfMonth: Int, month: Int, year: Int) {
        val tempDataHolder = InformationCardDataHolder("$dayOfMonth-$month-$year", "", 6, "Cycle started on: $dayOfMonth/$month", "Cycle should end on: ${dayOfMonth + 5}", 1)
        FirebaseDatabase.getInstance().getReference(currentUserUID).child("MENSTRUATIE").child("$dayOfMonth-$month-$year").setValue(tempDataHolder)
        FirebaseDatabase.getInstance().getReference(currentUserUID).child("USER").child("onPeriod").setValue(true)
        Toast.makeText(this, "New period started", Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        informationListAdapter.cleanUpListener()
    }

    fun displayDialogWindow (daysList : ArrayList<Day>) {
        daysListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {

                    val day = child.getValue(Day::class.java)!!
                    if (day != null && !daysList.contains(day)) {
                        daysList.add(day)
                    }
                }

                if (daysList.count() == 0) {
                    showDialogWindow()
                } else {
                    var maxVal: Long = 0
                    for (day in daysList) {
                        if (maxVal < day.date.toLong()) {
                            maxVal = day.date.toLong()
                        }
                    }

                    if (calendar.date.toLong() - maxVal >= 50000) {
                        showDialogWindow()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        FirebaseDatabase.getInstance().getReference(currentUserUID).child("DAYS").addValueEventListener(daysListener)

    }

    fun showDialogWindow () {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_pop_up_layout, null)
        dialogBuilder.setView(dialogView)

        FirebaseDatabase.getInstance().getReference(currentUserUID).child("DAYS").removeEventListener(daysListener)

        var day = Day()
        day.date = calendar.date.toString()

        // Previously selected mood, pain, bleed
        var previousMood = R.id.mood_negative_image_view
        var previousBleeding = R.id.bleeding_negative_image_view
        var previousPain = R.id.pain_negative_image_view


        // MOOD ONCLICK LISTENER EVENTS
        dialogView.findViewById<ImageView>(R.id.mood_negative_image_view).setOnClickListener(){
            previousMood = setBackgroundColorForSelectedImage(day, 1, previousMood, R.id.mood_negative_image_view)
        }
        dialogView.findViewById<ImageView>(R.id.mood_average_image_view).setOnClickListener(){
            previousMood = setBackgroundColorForSelectedImage(day, 2, previousMood, R.id.mood_average_image_view)
        }
        dialogView.findViewById<ImageView>(R.id.mood_positive_image_view).setOnClickListener(){
            previousMood = setBackgroundColorForSelectedImage(day, 3, previousMood, R.id.mood_positive_image_view)
        }

        // BLEEDING ONCLICK LISTENER EVENTS
        dialogView.findViewById<ImageView>(R.id.bleeding_negative_image_view).setOnClickListener(){
            previousBleeding = setPreviousBleedingState(day, 1, previousBleeding, R.id.bleeding_negative_image_view)
        }
        dialogView.findViewById<ImageView>(R.id.bleeding_average_image_view).setOnClickListener(){
            previousBleeding = setPreviousBleedingState(day, 2, previousBleeding, R.id.bleeding_average_image_view)
        }
        dialogView.findViewById<ImageView>(R.id.bleeding_positive_image_view).setOnClickListener(){
            previousBleeding = setPreviousBleedingState(day, 3, previousBleeding, R.id.bleeding_positive_image_view)
        }

        // NAUSEA ONCLICK LISTENER EVENTS
        dialogView.findViewById<ImageView>(R.id.pain_negative_image_view).setOnClickListener(){
            previousPain = setPreviousPainState(day, 1, previousPain, R.id.pain_negative_image_view)
        }
        dialogView.findViewById<ImageView>(R.id.pain_average_image_view).setOnClickListener(){
            previousPain = setPreviousPainState(day, 2, previousPain, R.id.pain_average_image_view)
        }
        dialogView.findViewById<ImageView>(R.id.pain_positive_image_view).setOnClickListener(){
            previousPain = setPreviousPainState(day, 3, previousPain, R.id.pain_positive_image_view)
        }

        // Done button is pressed
        dialogView.findViewById<Button>(R.id.done_btn).setOnClickListener() {
            FirebaseDatabase.getInstance().getReference(currentUserUID).child("DAYS").child(day.date).setValue(day)
            alertDialog.dismiss()
        }

        alertDialog = dialogBuilder.create()
        alertDialog.show()

    }

    fun setBackgroundColorForSelectedImage(day: Day, value: Int, previousState: Int, id: Int): Int{
        day.mood = value
        var temp = previousState
        temp = id
        return temp
    }

    fun setPreviousBleedingState(day : Day, value: Int, previousState: Int, id:Int) : Int {
        day.bleeding = value
        var temp = previousState
        temp = id
        return temp
    }

    fun setPreviousPainState(day : Day, value: Int, previousState: Int, id:Int) : Int {
        day.pain = value
        var temp = previousState
        temp = id
        return temp
    }

    // HELPER METHODS
    fun parseDateToInt(date : String) : Int {
        var dayOfMonth = 0

        val splitDate = date.split("-")
        dayOfMonth = splitDate[0].toInt()

        return dayOfMonth
    }

}
