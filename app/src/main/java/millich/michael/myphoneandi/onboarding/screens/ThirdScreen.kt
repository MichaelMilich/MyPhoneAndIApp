package millich.michael.myphoneandi.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import millich.michael.myphoneandi.R

class ThirdScreen : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        val viewPager =  activity?.findViewById<ViewPager2>(R.id.view_pager)

        val next = activity?.findViewById<TextView>(R.id.finish)
        next?.setOnClickListener {
            findNavController(next).navigate(R.id.action_viewPagerFragment_to_homeFragment)
        }

        return view
    }

}