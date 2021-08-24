package com.example.hsd.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.hsd.R
import com.example.hsd.databinding.FragmentRidesBinding
import com.example.hsd.model.Ride
import com.example.hsd.ui.adapter.RidesAdapter
import com.example.hsd.utils.Constant
import com.example.hsd.utils.SetupUtil.setupRv
import com.example.hsd.utils.Status
import com.example.hsd.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RidesFragment : Fragment(),RidesAdapter.OnRideClickListener {

    private lateinit var binding: FragmentRidesBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var dayAdapter: RidesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRidesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        setupRetryButton()

        setupRecyclerview()

        fetchData()

        setupObserver()

        setupMapObserver()

    }

    private fun setupRetryButton(){
        binding.btnRetry.setOnClickListener {
            fetchData()
        }
    }

    private fun setupMapObserver() {
        viewModel.map.observe(viewLifecycleOwner,{
            dayAdapter.map = it
        })
    }

    private fun setupObserver() {
        viewModel.listOfDays.observe(viewLifecycleOwner,{
            when(it.status){

                Status.LOADING -> {
                    binding.apply {
                        progressbar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        noInternetBlock.visibility = View.GONE
                    }
                }

                Status.SUCCESS -> {
                    binding.apply {
                        dayAdapter.listOfDays = it.data
                        progressbar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        noInternetBlock.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    binding.apply {
                        progressbar.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        noInternetTv.text = it.message
                        noInternetBlock.visibility = View.VISIBLE
                    }
                }

            }
        })
    }

    private fun setupRecyclerview() {
        dayAdapter = RidesAdapter(this,Constant.RIDE_HEADER)
        setupRv(binding.recyclerView,dayAdapter)
    }

    private fun fetchData() {
        viewModel.getDataFormRepository()
    }

    override fun onRideClick(ride: Ride) {
        Log.d("CLICKED",ride.toString())
        viewModel.clicked = ride
        navController.navigate(R.id.rideDetailsFragment)
    }
}