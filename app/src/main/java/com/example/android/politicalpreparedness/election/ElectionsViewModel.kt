package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.utils.Utils
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(context : Context, private val electionRepository: ElectionRepository): ViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElection = MutableLiveData<List<Election>>()
    val upcomingElection : LiveData<List<Election>>
    get() = _upcomingElection
    private val _savedElection = MutableLiveData<List<Election>>()
    val savedElection : LiveData<List<Election>>
    get() = _savedElection
    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo : LiveData<Election>
    get() = _navigateToVoterInfo
    private val _status = MutableLiveData<ElectionAPIStatus>()
    val status: LiveData<ElectionAPIStatus> get() = _status
    private val _checkInternet = MutableLiveData<Boolean>()
    val checkInternet : LiveData<Boolean>
        get() = _checkInternet

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    init {
        viewModelScope.launch {
            try {
                _status.value = ElectionAPIStatus.LOADING
                _upcomingElection.value = electionRepository.getUpcominElectionOnline()
                _savedElection.value = electionRepository.getSavedElections()
            }catch (e:Exception){
                _status.value = ElectionAPIStatus.ERROR
                e.printStackTrace()
            }finally {
                _status.value = ElectionAPIStatus.DONE
            }
        }

        viewModelScope.launch {
            _checkInternet.value = Utils.isConnectionAvailable(context)
        }
    }

    private fun getUpcomingElections(){
        viewModelScope.launch {
            val electionList = electionRepository.getUpcominElectionOnline()
            _upcomingElection.value = electionList
        }
    }
    private fun getSavedElections(){
        viewModelScope.launch {
            val electList = electionRepository.getSavedElections()

            _savedElection.value = electList
        }
    }

    fun onElectionClicked(election: Election){
        _navigateToVoterInfo.value = election
    }

    fun onCompleteNavigation(){
        _navigateToVoterInfo.value = null
    }

    fun refresh(){
        getUpcomingElections()
        getSavedElections()
    }

    enum class ElectionAPIStatus {
        LOADING,
        ERROR,
        DONE
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}