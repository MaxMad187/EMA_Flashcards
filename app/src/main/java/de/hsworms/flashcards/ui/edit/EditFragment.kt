package de.hsworms.flashcards.ui.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_home.*

class EditFragment : Fragment() {

    private lateinit var editViewModel: EditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val cross = arguments?.get("toEdit") as RepositoryCardCrossRef
        Log.i("test", cross.toString())
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

        // Show the create set dialog on FAB click
        //bottomAppBarFab.setOnClickListener { showCreateSetDialog() }
    }
}