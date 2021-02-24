package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(val repRepository: RepRepository): ViewModel() {

    //TODO: Establish live data for representatives and address

    private val _repList = MutableLiveData<List<Representative>>()
    val repList: LiveData<List<Representative>>
        get() = _repList
    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    //TODO: Create function to fetch representatives from API from a provided address

    fun getRepresentatives(address: Address){
        val address1 = address.line1
        val address2 = address.line2
        val state = address.state
        val city = address.city
        val zip = address.zip

        val completeAddress = "$address2 $address1, $city, $state, $zip"
        getRepresentative(completeAddress)
    }

    fun getRepresentative(address: String){
        viewModelScope.launch {
            try {
                val(offices, officials) =repRepository.getRepresentatives(address)
                _repList.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            }catch (e: Exception){
                e.printStackTrace()

            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    fun getAddress(address: Address){
        _address.value = address
    }
    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
