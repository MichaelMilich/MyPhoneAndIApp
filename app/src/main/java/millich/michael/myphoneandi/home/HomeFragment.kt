package millich.michael.myphoneandi.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
        // the required code to start/stop the service.
        binding.buttonStartService.setOnClickListener{
            this.context?.let { it1 -> Snackbar.make(it1,it,"Made start", Snackbar.LENGTH_SHORT).show() }
            viewModel.start()
        }
        binding.buttonStopService.setOnClickListener{
            this.context?.let { it1 -> Snackbar.make(it1,it,"Made stop", Snackbar.LENGTH_SHORT).show() }
            viewModel.stop()
        }

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
        return binding.root
    }
    private fun callClockViewTags(eventList: List<UnlockEvent>){
        viewLifecycleOwner.lifecycleScope.launch {
            clockView.afterMeasured {
                clockView.createTimeTags(eventList,(clockView.binding.analogClockView.width/2).toFloat()+0.5f)
            }
        }
    }
}