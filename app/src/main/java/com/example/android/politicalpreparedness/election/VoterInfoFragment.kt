package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.repository.ElectionRepository

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var binding = FragmentVoterInfoBinding.inflate(inflater)
        val voterInfoFragmentArgs by navArgs<VoterInfoFragmentArgs>()
        val voterInfoViewModelFactory = VoterInfoViewModelFactory(this.requireContext(), ElectionRepository(), voterInfoFragmentArgs)
        viewModel = ViewModelProvider(this, voterInfoViewModelFactory).get(VoterInfoViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.voterInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("ok", it.election.id.toString())
            }
        })


        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root

    }

    //TODO: Create method to load URL intents

}