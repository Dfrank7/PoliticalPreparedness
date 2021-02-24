package com.example.android.politicalpreparedness.election

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoterInfoViewModel(context : Context, private val dataSource: ElectionRepository,
                         private val voterInfoFragmentArgs: VoterInfoFragmentArgs) : ViewModel() {
    val database = ElectionDatabase.getInstance(context)
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo : LiveData<VoterInfoResponse>
    get() = _voterInfo
    var correspondenceAddress = MutableLiveData<String>()
    var stateAddress = MutableLiveData<String>()
    var isAddressAvailable = MutableLiveData<Boolean>()
    val _isElectionSaved :MutableLiveData<Boolean> = MutableLiveData(false)

    private val _status = MutableLiveData<VoterInfoAPIStatus>()
    val status: LiveData<VoterInfoAPIStatus> get() = _status

    private val _eventOpenUrl = MutableLiveData<String>()
    val eventOpenUrl: LiveData<String>
        get() = _eventOpenUrl


    fun checkStatus(): LiveData<Boolean>{
        viewModelScope.launch(Dispatchers.IO) {
            val electionFromDb = dataSource.checkElection(voterInfoFragmentArgs.argElectionId)
            _isElectionSaved.postValue(electionFromDb)
        }
        return _isElectionSaved
    }

    fun getVoterInfo(context: Context){
        viewModelScope.launch {
            try {
                _status.value = VoterInfoAPIStatus.LOADING
                val voterInfoResult = dataSource.getVoterInfo(voterInfoFragmentArgs.argDivision.state
                        .plus(" ")
                        .plus(voterInfoFragmentArgs.argDivision.country),
                        voterInfoFragmentArgs.argElectionId)
                _voterInfo.value = voterInfoResult
                val address = retrieveAddress(voterInfoResult.state?.first()?.electionAdministrationBody?.correspondenceAddress)
                stateAddress.value = address
                isAddressAvailable.value = address.isNotEmpty()

            }catch (e: Exception){
                _status.value = VoterInfoAPIStatus.ERROR
                showErrorMessage.value = context.getString(R.string.loading_voter_info)
                e.printStackTrace()
            }finally {
                if (_voterInfo.value?.election?.name.isNullOrEmpty()) {
                    _status.value = VoterInfoAPIStatus.ERROR
                }else{
                    _status.value = VoterInfoAPIStatus.DONE
                }
            }
        }
    }

    fun votingLocationSelected() {
        val voteData = _voterInfo.value
        val voteLocationUrl = voteData?.state?.elementAtOrNull(0)?.electionAdministrationBody?.votingLocationFinderUrl
                ?: ""

        if (voteLocationUrl.isNotEmpty()) {
            _eventOpenUrl.value = voteLocationUrl
        }
    }

    fun ballotInfoSelected() {
        val voteData = _voterInfo.value

        val ballotInfoUrl = voteData?.state?.elementAtOrNull(0)?.electionAdministrationBody?.ballotInfoUrl
                ?: ""

        if (ballotInfoUrl.isNotEmpty()) {
            _eventOpenUrl.value = ballotInfoUrl

        }
    }

    fun savedOrDeleteElection(election: Election){
        viewModelScope.launch {
            if (_isElectionSaved.value!!){
                dataSource.deleteElection(voterInfoFragmentArgs.argElectionId)
            }else{
                dataSource.saveElection(election)
            }
            _isElectionSaved.value = !_isElectionSaved.value!!
        }
    }

    fun onOpenUrlComplete() {
        _eventOpenUrl.value = ""
    }


    private fun retrieveAddress(correspondenceAddress: Address?): String {
        correspondenceAddress?.let {
            return "${it.line1} ${it.city} ${it.state} ${it.zip}"
        }
        return ""
    }

    enum class VoterInfoAPIStatus {
        LOADING,
        ERROR,
        DONE
    }

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}