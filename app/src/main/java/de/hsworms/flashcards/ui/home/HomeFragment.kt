package de.hsworms.flashcards.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import de.hsworms.flashcards.R
import de.hsworms.flashcards.model.Set
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    // The adapter for the list on the home screen
    private var homeAdapter: HomeAdapter? = null
    private var testItems: MutableList<ListItem> = mutableListOf()

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(bottomAppBar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        // Show the create set dialog on FAB click
        bottomAppBarFab.setOnClickListener { showCreateSetDialog() }

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

    private fun showCreateSetDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("Kartenstapel erstellen")
        val mAlertDialog = mBuilder.show()
        mDialogView.dialogCreateBtn.setOnClickListener {
            mAlertDialog.dismiss()
            val name = mDialogView.dialogNameEt.text.toString()
            addSetItem(name)
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    /**
     * Add a new card set to the temporary [testItems]
     * TODO: Use database instead of [testItems]
     */
    private fun addSetItem(name: String) {
        val set = Set(
            id = 1,
            name = name,
            shortTimeCardCount = 0,
            middleTimeCardCount = 0,
            longTimeCardCount = 0
        )
        testItems.add(CardSetItem(set))
        // Update the adapter
        homeAdapter?.items = testItems
    }

    private fun createSampleCardStackData() {
        val cardSet1 = Set(
            id = 1,
            name = "IT-Sicherheit",
            shortTimeCardCount = 12,
            middleTimeCardCount = 13,
            longTimeCardCount = 14
        )
        val cardSetItem1 = CardSetItem(cardSet1)
        testItems.add(cardSetItem1)

        val cardSet2 = Set(
            id = 2,
            name = "EMA",
            shortTimeCardCount = 12,
            middleTimeCardCount = 13,
            longTimeCardCount = 14
        )
        val cardSetItem2 = CardSetItem(cardSet2)
        testItems.add(cardSetItem2)

        // Show the items in the HomeAdapter
        homeAdapter?.items = testItems
    }
}
