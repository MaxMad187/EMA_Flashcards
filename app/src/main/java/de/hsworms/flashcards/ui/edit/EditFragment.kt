package de.hsworms.flashcards.ui.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcard.database.entity.RepositoryWithCards
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.bottomAppBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditFragment : Fragment() {

    private lateinit var editViewModel: EditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        editViewModel = ViewModelProviders.of(this).get(EditViewModel::class.java)
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(bottomAppBar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        GlobalScope.launch {
            val repos = FCDatabase.getDatabase(requireContext()).repositoryDao().getAllRepositoriesWithCards().toTypedArray()
            repositorySpinner.adapter = RepositoryAdapter(requireContext(), android.R.layout.simple_spinner_item, repos)
        }

        if(arguments?.containsKey("toEdit")!!) {
            val cross = arguments?.get("toEdit") as RepositoryCardCrossRef
            Log.i("test", cross.toString())
        } else {
            Log.i("test", "noID")
        }

        // Show the create set dialog on FAB click
        //bottomAppBarFab.setOnClickListener { showCreateSetDialog() }
    }
}