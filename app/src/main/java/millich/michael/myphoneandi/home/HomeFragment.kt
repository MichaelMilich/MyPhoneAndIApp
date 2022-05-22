package millich.michael.myphoneandi.home

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.ClockView
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.afterMeasured
import millich.michael.myphoneandi.database.UnlockDatabase
import millich.michael.myphoneandi.database.UnlockEvent
import millich.michael.myphoneandi.databinding.FragmentHomeBinding

/**
 * Currently the main fragment in use in the application.
 * provides the viewModel with al required for it to function.
 * Sets up the Clock View. as well as database.
 */
class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binder: FragmentHomeBinding
    private lateinit var clockView: ClockView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val binding : FragmentHomeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        // all the basic requirements.
        val application = requireNotNull(this.activity).application
        val databaseDAO = UnlockDatabase.getInstance(application).unlockDatabaseDAO

        val viewModelFactory = HomeViewModelFactory(application,databaseDAO)
        viewModel = ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)
        binding.viewModel=viewModel

        // seting up the adapter for te recycleView
        val adapter = UnlockEventAdapter()
        binding.unlockList.adapter=adapter


        //Observing changes in the 12H list for the ClockView
        viewModel.unlockEvents12H.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if(it.isNotEmpty()) {
                    //The id should start from 1, that is why we change the eventid for the given list.
                        //The given list is always read from the database so we dont get id of -1
                    val firstId = it[it.size - 1].eventId - 1
                    for (event in it)
                        event.eventId -= firstId
                    //Giving the list to the clockView.
                    callClockViewTags(it)
                }
            }
        })

        //Observing changes in the 24H list for the recycleView.
        viewModel.unlockEvents24H.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if(it.isNotEmpty()) {
                    //The id should start from 1, that is why we change the eventid for the given list.
                    //The given list is always read from the database so we dont get id of -1
                    val firstId = it[it.size - 1].eventId - 1
                    for (event in it)
                        event.eventId -= firstId

                    adapter.submitList(it)
                }
            }
        })

        binding.clockView.binding.lifecycleOwner=viewLifecycleOwner
        binding.lifecycleOwner = viewLifecycleOwner
        binder=binding
        clockView=binder.clockView

        setHasOptionsMenu(true)
        return binding.root
    }
    private fun callClockViewTags(eventList: List<UnlockEvent>){
        viewLifecycleOwner.lifecycleScope.launch {
            clockView.afterMeasured {
                clockView.createTimeTags(eventList,(clockView.binding.analogClockView.width/2).toFloat()+0.5f)
            }
        }
    }

    /**
     * The Home Fragment is the main fragment in the navigation.
     * As such it has to create and inflate the menu and options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }
    /**
     * The Home Fragment is the main fragment in the navigation.
     * As such it has to manage teh navigation - at least to pass it to the navigation controller
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.
        onNavDestinationSelected(item,requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    /**
     * The Home Fragment is the starting point of the navigation.
     * But, the second we load it, we need to check if we already did the onBoarding.
     * If we didn't, we have to go to the viewPagerFragment.
     * That is why - as soon as we went through onCreateView and into onViewCreated we check through the viewmodel if we need to pass to the viewPagerFragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("TAG","OnViewCreated")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        val value = sharedPreferences.getBoolean("Finished",false)
        Log.i("TAG","value = $value")
        if (!value)
            findNavController().navigate(R.id.action_homeFragment_to_viewPagerFragment)
    }
}