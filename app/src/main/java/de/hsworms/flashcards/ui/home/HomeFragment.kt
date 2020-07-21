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
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    // The adapter for the list on the home screen
    private var homeAdapter: HomeAdapter? = null
    private var repositories: MutableList<ListItem> = mutableListOf()

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
        fetchData()
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
            .setTitle("Kartenstapel erstellen") // TODO string externalisieren
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
     * Add a new card set to the database
     */
    private fun addSetItem(name: String) {
        val ctx = this.requireContext()

        GlobalScope.launch {
            val repo = Repository(null, name)
            val id = FCDatabase.getDatabase(ctx).repositoryDao().insert(repo)
            val cr = FCDatabase.getDatabase(ctx).repositoryDao().getRepositoryWithCards(id[0].toInt())!!
            repositories.add(CardSetItem(cr))

            // Update the adapter
            homeAdapter?.items = repositories
            requireActivity().runOnUiThread {
                homeAdapter?.notifyDataSetChanged()
            }
        }
    }

    /**
     * fetches all repositories from the database
     */
    private fun fetchData() {
        repositories.clear()

        val ctx = this.requireContext()
        GlobalScope.launch {
            FCDatabase.getDatabase(ctx).repositoryDao().getAllRepositoriesWithCards().forEach {
                repositories.add(CardSetItem(it))
            }

            // Show the items in the HomeAdapter
            homeAdapter?.items = repositories
        }
    }
}
