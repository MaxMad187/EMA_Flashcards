package de.hsworms.flashcards.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcards.MainActivity
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.header_layout_generic.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    // The adapter for the list on the home screen
    private var homeAdapter: HomeAdapter? = null
    private var repositories: MutableList<ListItem> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(bottom_app_bar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header_headline_text_view.text = getString(R.string.home_header_headline)
        header_subline_text_view.text = ""

        // Show the create set dialog on FAB click
        bottom_app_bar_fab.setOnClickListener { showCreatePopUp() }

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
        fragment_home_list_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }
    }

    private fun import() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/json"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        activity?.startActivityForResult(intent, MainActivity.OPEN_DIALOG)
    }

    private fun showCreatePopUp() {
        val ctx = requireContext()
        val popup = PopupMenu(ctx, bottom_app_bar_fab)
        popup.menuInflater.inflate(R.menu.create_menu, popup.menu)
        popup.setOnMenuItemClickListener { mi ->
            when (mi.itemId) {
                R.id.create_menu_create -> showCreateSetDialog()
                R.id.create_menu_import -> import()
            }
            true
        }
        popup.show()
    }

    private fun showCreateSetDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle(getString(R.string.popup_create))
        val mAlertDialog = mBuilder.show()
        mDialogView.dialog_create_btn.setOnClickListener {
            mAlertDialog.dismiss()
            val name = mDialogView.dialog_name_et.text.toString()
            addSetItem(name)
        }
        //cancel button click of custom layout
        mDialogView.dialog_cancel_btn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    /**
     * Add a new card set to the database
     */
    private fun addSetItem(name: String) {
        val ctx = this.requireContext()

        GlobalScope.launch {
            FCDatabase.getDatabase(ctx).apply {
                val repo = Repository(null, name)
                val id = repositoryDao().insert(repo)
                val cr = repositoryDao().getRepositoryWithCards(id[0].toInt()) ?: return@apply
                repositories.add(CardSetItem(cr))
            }

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
    fun fetchData() {
        repositories.clear()

        val ctx = this.requireContext()
        GlobalScope.launch {
            var toLearn = 0
            val time = System.currentTimeMillis()
            FCDatabase.getDatabase(ctx).repositoryDao().getAllRepositoriesWithCards().forEach {
                repositories.add(CardSetItem(it))
                toLearn += it.crossRef.filter { c -> c.nextDate <= time }.count()
            }

            // Show the items in the HomeAdapter
            homeAdapter?.items = repositories

            activity?.runOnUiThread {
                homeAdapter?.notifyDataSetChanged()
                if (header_subline_text_view != null) {
                    header_subline_text_view.text = getString(R.string.home_header_subline, toLearn)
                }
            }
        }
    }
}
