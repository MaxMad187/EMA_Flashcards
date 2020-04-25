package de.hsworms.flashcards.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardStackItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.list_item_card_stack.view.*

/**
 * Adapter delegate for [CardStackItem]
 */
class CardStackItemAdapterDelegate :
    AbsListItemAdapterDelegate<CardStackItem, ListItem, CardStackItemAdapterDelegate.ViewHolder>() {

    /**
     * Check, if the type matches this delegate
     */
    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean {
        return item is CardStackItem
    }

    /**
     * Inflate the layout and create the [ViewHolder] instance
     */
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_card_stack, parent, false))
    }

    /**
     * Call [ViewHolder.bind] with the [CardStackItem] as parameter
     */
    override fun onBindViewHolder(item: CardStackItem, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    /**
     * ViewHolder for [CardStackItem]
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView = itemView.listItemCardStackTitleTextView
        private val shortTimeCardCountTextView = itemView.listItemCardStackShortTimeCardCountTextView
        private val middleTimeCardCountTextView = itemView.listItemCardStackMiddleTimeCardCountTextView
        private val longTimeCardCountTextView = itemView.listItemCardStackLongTimeCardCountTextView

        /**
         * Bind the [CardStackItem] to the view
         */
        fun bind(item: CardStackItem) {
            titleTextView.text = item.cardStack.title
            shortTimeCardCountTextView.text = item.cardStack.shortTimeCardCount.toString()
            middleTimeCardCountTextView.text = item.cardStack.middleTimeCardCount.toString()
            longTimeCardCountTextView.text = item.cardStack.longTimeCardCount.toString()
        }
    }
}