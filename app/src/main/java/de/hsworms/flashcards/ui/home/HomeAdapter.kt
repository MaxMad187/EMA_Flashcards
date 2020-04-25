package de.hsworms.flashcards.ui.home

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import de.hsworms.flashcards.ui.ListItem

/**
 * The Adapter for the home screen
 */
class HomeAdapter : ListDelegationAdapter<List<ListItem>>() {

    init {
        delegatesManager.apply {
            addDelegate(CardSetItemAdapterDelegate())
        }
    }
}