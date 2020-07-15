package de.hsworms.flashcards.ui.cardlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class CardListFragment : Fragment() {

    private lateinit var cardListViewModel: CardListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        cardListViewModel = ViewModelProviders.of(this).get(CardListViewModel::class.java)
        return inflater.inflate(R.layout.fragment_cardlist, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(bottomAppBar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardListViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        // Show the create set dialog on FAB click
        //bottomAppBarFab.setOnClickListener { showCreateSetDialog() }
    }
}