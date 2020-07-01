package com.example.period.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.period.R
import com.example.period.informationList.InformationCardDataHolder
import com.example.period.informationList.InformationListViewHolder
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference


class HistoryAdapter (val refHistory : DatabaseReference, val historyInformationList: ArrayList<InformationCardDataHolder>) : RecyclerView.Adapter<InformationListViewHolder>() {
    val minformationChildEventListener : ChildEventListener

    val informationChildEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val historyInformation = snapshot.getValue(InformationCardDataHolder::class.java)
            if (historyInformation != null && !historyInformationList.contains(historyInformation)) {
                historyInformationList.add(historyInformation)
                notifyDataSetChanged()
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

    }

    init {
        minformationChildEventListener = informationChildEventListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationListViewHolder {
        return InformationListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.history_card_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return historyInformationList.size
    }

    override fun onBindViewHolder(holder: InformationListViewHolder, position: Int) {
        val historyInformation = historyInformationList[position]

        holder.itemView.findViewById<TextView>(R.id.history_title_text_view).setText(historyInformation.titleResource)
        holder.itemView.findViewById<TextView>(R.id.period_duration_text_view).setText(historyInformation.durationOfCycle)
    }

}