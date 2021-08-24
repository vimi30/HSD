package com.example.hsd.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hsd.model.Ride
import com.example.hsd.model.Rides
import com.example.hsd.repository.Repository
import com.example.hsd.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(private val repository: Repository) : ViewModel(){

    private var _listOfDays = MutableLiveData<Resource<List<CharSequence>>>()

    val listOfDays : LiveData<Resource<List<CharSequence>>>
    get() = _listOfDays

    private var _map = MutableLiveData<Map<CharSequence, List<Ride>>>()
    val map:LiveData<Map<CharSequence, List<Ride>>>
    get() = _map

    var clicked : Ride? = null



    fun getDataFormRepository(){

        _listOfDays.postValue(Resource.loading(null))
        viewModelScope.launch {

            try {
                repository.getRidesFromServer()
                    .let { response ->

                        if(response.isSuccessful){

                            // grouping upcoming rides according to dates(2021-06-17)

                            var grouped = response.body()?.rides?.groupBy { it.startsAt.subSequence(0,10) }

                            _map.postValue(grouped)

                            Log.d("GROUPED",grouped.toString())

                            var days = grouped?.map { it.key }

                            _listOfDays.postValue(Resource.success(days))
                            Log.d("DAYS",days.toString())

//                            Log.d("RIDES", response.body().toString())
                        }else{
                            Log.e("NetworkResponse", "${response.errorBody().toString()}")
                            _listOfDays.postValue(Resource.error("Something Went Wrong!",null))
                        }

                    }

            }catch (e:Exception){
                _listOfDays.postValue((Resource.error("No Internet Connection",null)))
            }

        }


    }


}