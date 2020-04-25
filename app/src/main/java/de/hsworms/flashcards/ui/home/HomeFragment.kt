package de.hsworms.flashcards.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import de.hsworms.flashcards.R
import de.hsworms.flashcards.model.CardStack
import de.hsworms.flashcards.ui.CardStackItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    // The adapter for the list on the home screen
    private var homeAdapter: HomeAdapter? = null

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        // Set Up the list on the home screen
        setUpHomeRecyclerView()

        // Show example items
        createSampleCardStackData()
    }

    private fun setUpHomeRecyclerView() {
        // Instantiate the HomeAdapter
        if (homeAdapter == null) {
            homeAdapter = HomeAdapter()
        }
        // Set up the RecyclerView
        fragmentHomeListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }
    }

    private fun createSampleCardStackData() {
        val listItems = mutableListOf<ListItem>()
        val cardStack1 = CardStack(
            id = 1,
            title = "IT-Sicherheit",
            shortTimeCardCount = 12,
            middleTimeCardCount = 13,
            longTimeCardCount = 14
        )
        val cardStackItem1 = CardStackItem(cardStack1)
        listItems.add(cardStackItem1)

        val cardStack2 = CardStack(
            id = 2,
            title = "EMA",
            shortTimeCardCount = 12,
            middleTimeCardCount = 13,
            longTimeCardCount = 14
        )
        val cardStackItem2 = CardStackItem(cardStack2)
        listItems.add(cardStackItem2)

        // Show the items in the HomeAdapter
        homeAdapter?.items = listItems
    }
}
