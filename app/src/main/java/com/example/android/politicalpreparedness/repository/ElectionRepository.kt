package com.example.android.politicalpreparedness.repository

import android.content.Context
import com.example.android.politicalpreparedness.Utils
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class ElectionRepository() {
    private lateinit var electionT: List<Election>
    private lateinit var mVoterInfoResponse: VoterInfoResponse

    private val api = CivicsApi.retrofitService

    suspend fun getUpcominElectionOnline() : List<Election>{

            withContext(Dispatchers.IO){
                val electionResponse = api.getUpcomingElections().await()
                electionT = electionResponse.elections
            }
        return electionT
    }

    suspend fun getVoterInfo(adress: String, id: Int) : VoterInfoResponse{
        withContext(Dispatchers.IO){
            val voterInfoResponse = api.getVotersInfo(adress, id).await()
            mVoterInfoResponse = voterInfoResponse
        }
        return mVoterInfoResponse
    }
}