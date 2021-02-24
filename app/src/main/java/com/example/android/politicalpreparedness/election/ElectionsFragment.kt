package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.utils.Utils
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.repository.ElectionRepository

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
//    val database = ElectionDatabase.getInstance(this.requireContext())
    lateinit var database: ElectionDatabase
    private val viewModel : ElectionsViewModel by lazy {
        ViewModelProvider(this, ElectionsViewModelFactory(this.requireContext(), ElectionRepository(database))).get(ElectionsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        database = ElectionDatabase.getInstance(this.requireContext())

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        val upcomingAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener{
            it?.let {
                viewModel.onElectionClicked(it)
            }
        })
        val savedAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener{
            it?.let {
                viewModel.onElectionClicked(it)
            }
        })
        binding.recyclerUpcoming.adapter = upcomingAdapter
        binding.recyclerSaved.adapter = savedAdapter

        viewModel.upcomingElection.observe(viewLifecycleOwner, Observer {
            it.let {
                upcomingAdapter.submitList(it)
            }
        })

        viewModel.savedElection.observe(viewLifecycleOwner, Observer {
            it.let {
                savedAdapter.submitList(it)
            }
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id,it.division))
                viewModel.onCompleteNavigation()
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            it.let {
                when(it){
                    ElectionsViewModel.ElectionAPIStatus.LOADING -> binding.statusLoadingWheel.visibility = View.VISIBLE
                    ElectionsViewModel.ElectionAPIStatus.DONE -> binding.statusLoadingWheel.visibility = View.GONE
                    ElectionsViewModel.ElectionAPIStatus.ERROR-> {
                        binding.statusLoadingWheel.visibility = View.GONE
                        Utils.useSnackBar(requireActivity().findViewById(android.R.id.content), getString(R.string.loading_error))
                    }
                }
            }
        })

        viewModel.checkInternet.observe(viewLifecycleOwner, Observer {
            it.let {
                if (it.equals(false)){
                    Utils.useSnackBar(requireActivity().findViewById(android.R.id.content),
                            getString(R.string.internet_error_message))
                }
            }
        })

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refresh()
    }

    //TODO: Refresh adapters when fragment loads

}