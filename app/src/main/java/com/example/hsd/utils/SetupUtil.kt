package com.example.hsd.utils

import android.text.Html
import android.text.Spanned
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hsd.model.Ride
import com.example.hsd.ui.adapter.RidesAdapter
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

object SetupUtil {

    fun convertToDate(dateString:String) : Date{
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return format.parse(dateString)
    }

    fun getMonthAndDay(date:Date): String {
        val calender = Calendar.getInstance()
        calender.time = date
        return "${DateFormatSymbols().shortWeekdays[calender.get(Calendar.DAY_OF_WEEK)]} " +
                "${calender.get(Calendar.MONTH)+1}/${calender.get(Calendar.DAY_OF_MONTH)}"
    }

    fun getTimeFromDate(date: Date): String {
        val formatter = SimpleDateFormat("h:mma")
        val dateFormatted = formatter.format(date)
        return "${dateFormatted.lowercase().subSequence(0,dateFormatted.length-1)}"
    }

    fun getSpannedText(text: String): Spanned {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    }

    fun setupRv(recyclerView: RecyclerView, recyclerViewAdapter: RidesAdapter) {

        recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(recyclerView.context)
        }

    }


    fun countRiders(ride: Ride): Int {

        val set = mutableSetOf<Int>()
        ride.orderedWaypoints.forEach {
            it.passengers.forEach { passenger ->
                if(!set.contains(passenger.id)){
                    set.add(passenger.id)
                }
            }
        }
        return set.size
    }

    fun countBooster(ride: Ride): Int {

        val set = mutableSetOf<Int>()
        ride.orderedWaypoints.forEach {

            it.passengers.forEach { passenger ->

                if(passenger.boosterSeat && passenger.boosterSeat){
                    set.add(passenger.id)
                }

            }

        }
        return set.size
    }

    fun getTotalFare(currentDayRides: List<Ride>?): Double {
        var sum = 0.0
        currentDayRides?.forEach {
            sum += it.estimatedEarningsCents
        }
        return sum
    }


}