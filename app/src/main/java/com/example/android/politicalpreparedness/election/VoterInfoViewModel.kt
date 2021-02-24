package com.example.android.politicalpreparedness.election

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoterInfoViewModel(context : Context, private val dataSource: ElectionRepository,
                         private val voterInfoFragmentArgs: VoterInfoFragmentArgs) : ViewModel() {
    val database = ElectionDatabase.getInstance(context)

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo : LiveData<VoterInfoResponse>
    get() = _voterInfo
    var stateAddress = MutableLiveData<String>()
    var isAddressAvailable = MutableLiveData<Boolean>()
    val _isElectionSaved :MutableLiveData<Boolean> = MutableLiveData(false)
    val isElectionSaved : LiveData<Boolean>
    get() = _isElectionSaved

    init {
        viewModelScope.launch {
            try {
                _voterInfo.value = dataSource.getVoterInfo(voterInfoFragmentArgs.argDivision.state
                        .plus(" ")
                        .plus(voterInfoFragmentArgs.argDivision.country),
                        voterInfoFragmentArgs.argElectionId)
                //isSaved.postValue(electionFromDb != null)

            }catch (e: Exception){
                e.printStackTrace()
            }
        }

    }

    fun checkStatus(id: Int): LiveData<Boolean>{
        viewModelScope.launch(Dispatchers.IO) {
            val electionFromDb = dataSource.checkElection(id)
            Log.d("okkVaa", electionFromDb.toString())
            _isElectionSaved.postValue(electionFromDb != null)
        }
        return _isElectionSaved
    }

    fun getVoterInfo(){
        viewModelScope.launch {
            try {
                val voterInfoResult = dataSource.getVoterInfo(voterInfoFragmentArgs.argDivision.state
                        .plus(" ")
                        .plus(voterInfoFragmentArgs.argDivision.country),
                        voterInfoFragmentArgs.argElectionId)
                val address = retrieveAddress(voterInfoResult.state?.first()?.electionAdministrationBody?.correspondenceAddress)
                stateAddress.value = address
                isAddressAvailable.value = address.isNotEmpty()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun savedOrDeleteElection(election: Election){
        viewModelScope.launch {
            if (_isElectionSaved.value!!){
                dataSource.deleteElection(voterInfoFragmentArgs.argElectionId)
            }else{
                dataSource.saveElection(election)
               // _isElectionSaved.value = true
            }
           // _isElectionSaved.value = !_isElectionSaved.value!!
        }
    }


    private fun retrieveAddress(correspondenceAddress: Address?): String {
       // Timber.d("address? $correspondenceAddress")
        correspondenceAddress?.let {
            return "${it.line1} ${it.city} ${it.state} ${it.zip}"
        }
        return ""
    }

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}