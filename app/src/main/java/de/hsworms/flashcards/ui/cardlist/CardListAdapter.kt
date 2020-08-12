package de.hsworms.flashcards.ui.cardlist

import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcards.ui.CardItem
import de.hsworms.flashcards.ui.ListItem


/**
 * The Adapter for the home screen
 */
class CardListAdapter : ListDelegationAdapter<List<ListItem>>() {

    var search: String = ""

    init {
        delegatesManager.apply {
            addDelegate(CardItemAdapterDelegate())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        val item = items[position] as CardItem
        val card = (item.card as FlashcardNormal)
        if(card.front.contains(search, true) || card.back.contains(search, true) || item.repo.name.contains(search, true)) {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = AbsListView.LayoutParams(-1, -2)
        } else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = AbsListView.LayoutParams(-1, 1)
        }
    }
}