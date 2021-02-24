package com.example.android.politicalpreparedness.representative

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class RepRepository() {
    private lateinit var mRepresentativeResponse: RepresentativeResponse

    private val api = CivicsApi.retrofitService
    suspend fun getRepresentatives(address: String) : RepresentativeResponse {
        withContext(Dispatchers.IO){
            val repResponse = api.getRepresentatives(address).await()
            mRepresentativeResponse = repResponse
        }
        return mRepresentativeResponse
    }
}