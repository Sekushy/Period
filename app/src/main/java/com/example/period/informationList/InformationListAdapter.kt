package com.example.period.informationList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.period.Day
import com.example.period.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.snapshot.ChildrenNode
import kotlin.math.min

class InformationListAdapter(val refMenstruatie: DatabaseReference, val refDays : DatabaseReference, val informationList: ArrayList<InformationCardDataHolder>,  val daysList : ArrayList<Day>  ) : RecyclerView.Adapter<InformationListViewHolder>() {
    val minformationListChildEventListener : ChildEventListener
    val mdaysListChildEventListener : ChildEventListener

    val informationListChildEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }
        override fun onChildChanged(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val information = p0.getValue(InformationCardDataHolder::class.java)
            if (information != null && !informationList.contains(information)) {
                informationList.add(0, information)
                notifyDataSetChanged()
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

    }

    val daysListChildEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }
        override fun onChildChanged(p0: DataSnapshot, p1: String?) {


        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val day = p0.getValue(Day::class.java)
            if (day != null && !daysList.contains(day)) {
                daysList.add(0, day)
                notifyDataSetChanged()
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }
    }

    init {
        refMenstruatie.addChildEventListener(informationListChildEventListener)
        refDays.addChildEventListener(daysListChildEventListener)
        // Copy of informationListChildEventListener to be used in the CleanUp function
        minformationListChildEventListener = informationListChildEventListener
        mdaysListChildEventListener = daysListChildEventListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationListViewHolder {
        return InformationListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.information_card_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return informationList.size
    }

    override fun onBindViewHolder(holder: InformationListViewHolder, position: Int) {
        val information = informationList[position]

        holder.itemView.findViewById<TextView>(R.id.title_text_view).setText(information.titleResource)
        holder.itemView.findViewById<TextView>(R.id.description_text_view).setText(information.descriptionResource)
    }

    fun cleanUpListener() {
        if (minformationListChildEventListener != null) {
            refMenstruatie.removeEventListener(minformationListChildEventListener)
        }

        if (mdaysListChildEventListener != null) {
            refDays.removeEventListener(mdaysListChildEventListener)
        }
    }


}