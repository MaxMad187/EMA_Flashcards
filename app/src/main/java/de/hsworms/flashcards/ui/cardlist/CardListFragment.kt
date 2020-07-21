package de.hsworms.flashcards.ui.cardlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardItem
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import de.hsworms.flashcards.ui.edit.EditFragment
import de.hsworms.flashcards.ui.home.HomeAdapter
import de.hsworms.flashcards.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_cardlist.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.bottomAppBar
import kotlinx.android.synthetic.main.fragment_home.bottomAppBarFab
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CardListFragment : Fragment() {

    private lateinit var cardListViewModel: CardListViewModel
    private var cardListAdapter: CardListAdapter? = null
    private var cards: MutableList<ListItem> = mutableListOf()

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

        setUpCardListRecyclerView()

        fetchData()

        bottomAppBarFab.setOnClickListener {
            findNavController().navigate(R.id.nav_edit)
        }
    }

    private fun setUpCardListRecyclerView() {
        // Instantiate the HomeAdapter
        if (cardListAdapter == null) {
            cardListAdapter = CardListAdapter()
        }
        // Set up the RecyclerView
        fragmentCardListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cardListAdapter
        }
    }

    /**
     * fetches all cards from the database
     */
    private fun fetchData() {
        cards.clear()

        val ctx = this.requireContext()
        GlobalScope.launch {
            FCDatabase.getDatabase(ctx).repositoryDao().getAllRepositoriesWithCards().forEach {
                it.cards.forEach { card ->
                    cards.add(CardItem(it.repository, FCDatabase.getDatabase(ctx).flashcardDao().getOne(card.cardId!!)!!))
                }
            }

            // Show the items in the CardListAdapter
            cardListAdapter?.items = cards
        }
    }
}