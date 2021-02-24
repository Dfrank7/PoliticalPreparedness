package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.Utils

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel
    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var voterInfoFragmentArgs: VoterInfoFragmentArgs
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentVoterInfoBinding.inflate(inflater)
        val database = ElectionDatabase.getInstance(this.requireContext())
        val voterInfoFragmentArgs by navArgs<VoterInfoFragmentArgs>()
        val voterInfoViewModelFactory = VoterInfoViewModelFactory(this.requireContext(),
                ElectionRepository(database), voterInfoFragmentArgs)
        viewModel = ViewModelProvider(this, voterInfoViewModelFactory).get(VoterInfoViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        viewModel.getVoterInfo(requireContext())

        viewModel.checkStatus().observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    binding.followElectionButton.text = getString(R.string.unfollow_txt)
                }else{
                    binding.followElectionButton.text = getText(R.string.follow_elect)
                }
            }
        })

        viewModel.eventOpenUrl.observe(viewLifecycleOwner, Observer { url->
            if(url.isNotEmpty()){
                openWebPage(url)
                viewModel.onOpenUrlComplete()
            }
        })

        viewModel.showErrorMessage.observe(viewLifecycleOwner, Observer {
            it.let {
                Utils.useSnackBar(requireActivity().findViewById(android.R.id.content), it)
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            it.let {
                when(it){
                    VoterInfoViewModel.VoterInfoAPIStatus.LOADING -> binding.showLoading.visibility = View.VISIBLE

                    VoterInfoViewModel.VoterInfoAPIStatus.DONE ->{
                        binding.showLoading.visibility = View.GONE
                        binding.followElectionButton.visibility = View.VISIBLE
                        binding.stateBallot.visibility = View.VISIBLE
                        binding.stateLocations.visibility = View.VISIBLE
                    }
                    VoterInfoViewModel.VoterInfoAPIStatus.ERROR-> {
                        binding.showLoading.visibility = View.GONE
                        Utils.useSnackBar(requireActivity().findViewById(android.R.id.content), getString(R.string.loading_error))
                    }
                }
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

    fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    //TODO: Create method to load URL intents

}