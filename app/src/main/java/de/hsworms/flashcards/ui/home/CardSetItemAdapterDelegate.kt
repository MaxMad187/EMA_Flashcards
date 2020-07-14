package de.hsworms.flashcards.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.hsworms.flashcards.MainActivity
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.list_item_card_set.view.*

/**
 * Adapter delegate for [CardSetItem]
 */
class CardSetItemAdapterDelegate : AbsListItemAdapterDelegate<CardSetItem, ListItem, CardSetItemAdapterDelegate.ViewHolder>() {

    /**
     * Check, if the type matches this delegate
     */
    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean {
        return item is CardSetItem
    }

    /**
     * Inflate the layout and create the [ViewHolder] instance
     */
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_card_set, parent, false))
    }

    /**
     * Call [ViewHolder.bind] with the [CardSetItem] as parameter
     */
    override fun onBindViewHolder(item: CardSetItem, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    /**
     * ViewHolder for [CardSetItem]
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var item: CardSetItem
        private val titleTextView = itemView.listItemCardSetTitleTextView
        private val shortTimeCardCountTextView = itemView.listItemCardSetShortTimeCardCountTextView
        private val middleTimeCardCountTextView = itemView.listItemCardSetMiddleTimeCardCountTextView
        private val longTimeCardCountTextView = itemView.listItemCardSetLongTimeCardCountTextView

        private val click: View.OnClickListener = View.OnClickListener {
            Log.i("Flashcard",item.set.name + " was clicked")
        }

        /**
         * Bind the [CardSetItem] to the view
         */
        fun bind(item: CardSetItem) {
            this.item = item
            titleTextView.text = item.set.name
            /*
            TODO
            shortTimeCardCountTextView.text = item.set.shortTimeCardCount.toString()
            middleTimeCardCountTextView.text = item.set.middleTimeCardCount.toString()
            longTimeCardCountTextView.text = item.set.longTimeCardCount.toString()*/

            titleTextView.setOnClickListener(click)
        }
    }
}