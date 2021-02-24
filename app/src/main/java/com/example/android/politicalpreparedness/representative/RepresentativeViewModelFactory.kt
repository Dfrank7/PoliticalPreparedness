package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.election.VoterInfoFragmentArgs
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.repository.ElectionRepository
import java.lang.IllegalArgumentException

class RepresentativeViewModelFactory(private val repository: RepRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)){
            return RepresentativeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel class")
    }

}