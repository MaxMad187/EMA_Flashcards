package de.hsworms.flashcards.ui.cardlist

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import de.hsworms.flashcards.ui.ListItem

/**
 * The Adapter for the home screen
 */
class CardListAdapter : ListDelegationAdapter<List<ListItem>>() {

    init {
        delegatesManager.apply {
            addDelegate(CardItemAdapterDelegate())
        }
    }
}