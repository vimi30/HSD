package com.example.hsd.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hsd.R
import com.example.hsd.databinding.LocationListItemBinding
import com.example.hsd.databinding.RideDayHeaderBinding
import com.example.hsd.databinding.RideLayoutBinding
import com.example.hsd.model.OrderedWaypoint
import com.example.hsd.model.Ride
import com.example.hsd.utils.Constant
import com.example.hsd.utils.SetupUtil
import com.example.hsd.utils.SetupUtil.countBooster
import com.example.hsd.utils.SetupUtil.countRiders
import com.example.hsd.utils.SetupUtil.getSpannedText
import com.example.hsd.utils.SetupUtil.getTotalFare
import com.example.hsd.utils.SetupUtil.setupRv


class RidesAdapter(private val listener: OnRideClickListener?, private val elementType: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var map :  Map<CharSequence, List<Ride>>? = null

    var listOfDays : List<CharSequence>? = null

    var listOfRides:List<Ride>? = null

    var locationList : List<OrderedWaypoint>? =  null

    inner class TripHeaderViewHolder(private val binding: RideDayHeaderBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentDay:CharSequence?, currentDayRides: List<Ride>?, totalFare:Double){

            binding.apply {

                date.text = root.context.getString(R.string.date,currentDay)

                val firstRideStartDate = SetupUtil.convertToDate(currentDayRides?.get(0)?.startsAt!!)
                val lastRideEndDate = SetupUtil.convertToDate(currentDayRides[currentDayRides.size-1].endsAt)

                val fromToTimeText = root.context.getString(
                    R.string.startEndTime,
                    SetupUtil.getTimeFromDate(firstRideStartDate),
                    SetupUtil.getTimeFromDate(lastRideEndDate)
                )

                FromToTime.text = getSpannedText(fromToTimeText)

                dayFare.text = root.context.getString(R.string.fare,totalFare)

                val rideAdapter = RidesAdapter(listener,Constant.RIDE_CARD)

                setupRv(rvRides,rideAdapter)

                rideAdapter.listOfRides = currentDayRides
            }

        }


    }


    inner class RideCardViewHolder(private val binding: RideLayoutBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener{

        fun bind(ride : Ride){
            binding.apply {

                val riderCount:Int = countRiders(ride)
                val boosterCount:Int = countBooster(ride)

                val riderString  = getRiderString(riderCount)
                val boosterString = getBoosterString(boosterCount)

                riderAndBoosters.text =  if(boosterString.isEmpty()){
                    root.context.getString(
                        R.string.onlyRiders
                        ,riderString
                    )
                }else{
                    root.context.getString(
                        R.string.riderAndBoosters,
                        riderString,
                        boosterString
                    )
                }

                val rideStartDate = SetupUtil.convertToDate(ride.startsAt)
                val rideEndDate = SetupUtil.convertToDate(ride.endsAt)

                val timeText = root.context.getString(
                    R.string.startEndTime,
                    SetupUtil.getTimeFromDate(rideStartDate),
                    SetupUtil.getTimeFromDate(rideEndDate)
                )

                timeTv.text = getSpannedText(timeText)

                val rideFare:Double = ride.estimatedEarningsCents/100.toDouble()

                val rideFareText:String = root.context.getString(R.string.estimatedFare,rideFare)
                estPrice.text = getSpannedText(rideFareText)

                val locationListAdapter = RidesAdapter(listener,Constant.LOCATION_VIEW)

                setupRv(LocationRecyclerView,locationListAdapter)

                locationListAdapter.locationList = ride.orderedWaypoints
            }
        }


        override fun onClick(p0: View?) {
            listOfRides?.get(adapterPosition)?.let { listener?.onRideClick(it) }
        }
        init {
            itemView.setOnClickListener(this)
        }

        private fun getRiderString(riderCount:Int):String{
            return if(riderCount>1){
                binding.root.context.getString(R.string.riders,riderCount)
            }else{
                binding.root.context.getString(R.string.rider,riderCount)
            }
        }

        private fun getBoosterString(boosterCount:Int):String{
            return when(boosterCount){
                0 -> ""
                1 -> binding.root.context.getString(R.string.booster,boosterCount)
                else -> binding.root.context.getString(R.string.boosters,boosterCount)
            }
        }

    }


    inner class LocationViewHolder(private val binding: LocationListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(currentLocation : OrderedWaypoint, index:Int ){
            binding.apply {

                if(elementType==Constant.LOCATION_VIEW){
                    locationListItem.text = root.context.getString(R.string.location,index,currentLocation.location.address)
                    waypointCard.visibility = View.GONE

                }else{
                    locationListItem.visibility = View.GONE
                    waypointCard.apply {
                        visibility = View.VISIBLE

                        setAnchorIcon(currentLocation)

                        setPickupOrDropText(index)

                        waypointAddress.text = currentLocation.location.address
                    }
                }

            }
        }

        private fun setAnchorIcon(currentLocation : OrderedWaypoint){
            if(currentLocation.anchor){
                binding.waypointImage.setImageResource(R.drawable.diamond_icon)
            }else{
               binding.waypointImage.setImageResource(R.drawable.circle_icon)
            }
        }

        private fun setPickupOrDropText(index: Int){
            if(index != locationList?.size){
                binding.waypointText.text = binding.root.context.getString(R.string.pickup)
            }else{
                binding.waypointText.text = binding.root.context.getString(R.string.drop_off)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (elementType){
            Constant.RIDE_HEADER -> TripHeaderViewHolder(RideDayHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            Constant.RIDE_CARD -> RideCardViewHolder(RideLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> LocationViewHolder(LocationListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(elementType){
            Constant.RIDE_HEADER -> {
                holder as TripHeaderViewHolder
                val currentDay = listOfDays?.get(position)
                val currentDayRides = map?.get(currentDay)
                val totalFair = getTotalFare(currentDayRides)
                val currentDayDate = currentDayRides?.get(0)?.let { SetupUtil.convertToDate(it.startsAt) }
                holder.bind(currentDayDate?.let { SetupUtil.getMonthAndDay(it) },currentDayRides,totalFair/100)
            }

            Constant.RIDE_CARD -> {
                holder as RideCardViewHolder
                val currentRide = listOfRides?.get(position)
                if (currentRide != null) {
                    holder.bind(currentRide)
                }

            }

            else -> {
                holder as LocationViewHolder
                val currentLocation = locationList!![position]
                holder.bind(currentLocation,position+1)
            }
        }
    }

    override fun getItemCount(): Int {
        return when(elementType){
            Constant.RIDE_HEADER -> {
                return listOfDays?.size ?: 0
            }
            Constant.RIDE_CARD -> {
                return listOfRides?.size ?: 0
            }
            else -> locationList?.size ?: 0
        }
    }


    interface OnRideClickListener{
        fun onRideClick(ride: Ride)
    }


}