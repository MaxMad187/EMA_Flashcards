package de.hsworms.flashcards.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.hsworms.flashcards.MainActivity
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import de.hsworms.flashcards.ui.cardlist.CardListFragment
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
            val time = System.currentTimeMillis()
            val list = item.set.crossRef.filter { it.nextDate <= time }.toTypedArray()
            if(list.isNotEmpty()){
                val bundle = bundleOf("crossRefs" to list)
                itemView.findFragment<HomeFragment>().findNavController().navigate(R.id.nav_flashcard, bundle)
            }
        }

        /**
         * Bind the [CardSetItem] to the view
         */
        fun bind(item: CardSetItem) {
            this.item = item
            titleTextView.text = item.set.repository.name

            val time = System.currentTimeMillis()
            val list = item.set.crossRef.filter { it.nextDate <= time }

            val sh = list.filter { it.nextDate != 0L && it.interval <= 0 }.count()
            val mi = list.filter { it.nextDate != 0L && it.interval > 0}.count()
            val lo = list.filter { it.nextDate == 0L }.count()

            shortTimeCardCountTextView.text = sh.toString()
            middleTimeCardCountTextView.text = mi.toString()
            longTimeCardCountTextView.text = lo.toString()

            titleTextView.setOnClickListener(click)
            shortTimeCardCountTextView.setOnClickListener(click)
            middleTimeCardCountTextView.setOnClickListener(click)
            longTimeCardCountTextView.setOnClickListener(click)
        }
    }
}