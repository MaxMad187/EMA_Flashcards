package de.hsworms.flashcards.ui.cardlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.fragment_cardlist.*
import kotlinx.android.synthetic.main.fragment_home.bottomAppBar
import kotlinx.android.synthetic.main.fragment_home.bottomAppBarFab
import kotlinx.android.synthetic.main.header_layout_generic.*
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

        headerHeadlineTextView.text = "Kartenliste"
        headerSublineTextView.text = ""

        setUpCardListRecyclerView()

        fetchData()

        bottomAppBarFab.setOnClickListener {
            findNavController().navigate(R.id.nav_edit)
        }

        searchEditText.addTextChangedListener {
            val txt = it?.toString()!!
            cardListAdapter?.search = txt
            cardListAdapter?.notifyDataSetChanged()
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
    fun fetchData() {
        cards.clear()

        val ctx = this.requireContext()
        GlobalScope.launch {
            FCDatabase.getDatabase(ctx).repositoryDao().getAllRepositoriesWithCards().forEach {
                it.cards.forEach { card ->
                    cards.add(CardItem(it.repository, FCDatabase.getDatabase(ctx).flashcardDao().getOne(card.cardId!!)!!, it.crossRef.filter { cross -> cross.cardId == card.cardId }[0]))
                }
            }

            // Show the items in the CardListAdapter
            cardListAdapter?.items = cards
            requireActivity().runOnUiThread {
                cardListAdapter?.notifyDataSetChanged()
            }
        }
    }
}