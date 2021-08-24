package com.example.hsd.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkInterceptor(context: Context) : Interceptor {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun intercept(chain: Interceptor.Chain): Response {
        return if(isNetworkAvailable()){
            chain.proceed(chain.request())
        }else
            throw IOException("No Internet Connection")
    }

    private fun isNetworkAvailable() : Boolean {

        val networkInfo = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        networkInfo.also { networkCapabilities ->

            if(networkCapabilities!=null){
                if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    return true
                }else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    return true
                }
            }
            return false;
        }

    }

}