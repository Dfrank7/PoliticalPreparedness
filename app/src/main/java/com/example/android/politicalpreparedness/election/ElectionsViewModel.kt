package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(context : Context, private val electionRepository: ElectionRepository): ViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElection = MutableLiveData<List<Election>>()
    val upcomingElection : LiveData<List<Election>>
    get() = _upcomingElection
    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo : LiveData<Election>
    get() = _navigateToVoterInfo

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    init {
        viewModelScope.launch {
            try {
                _upcomingElection.value = electionRepository.getUpcominElectionOnline()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun onElectionClicked(election: Election){
        _navigateToVoterInfo.value = election
    }

    fun onCompleteNavigation(){
        _navigateToVoterInfo.value = null
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}