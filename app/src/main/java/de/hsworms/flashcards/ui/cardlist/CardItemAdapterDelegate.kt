package de.hsworms.flashcards.ui.cardlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardItem
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.list_item_card.view.*

/**
 * Adapter delegate for [CardSetItem]
 */
class CardItemAdapterDelegate : AbsListItemAdapterDelegate<CardItem, ListItem, CardItemAdapterDelegate.ViewHolder>() {

    /**
     * Check, if the type matches this delegate
     */
    override fun isForViewType(item: ListItem, items: MutableList<ListItem>, position: Int): Boolean {
        return item is CardItem
    }

    /**
     * Inflate the layout and create the [ViewHolder] instance
     */
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_card, parent, false))
    }

    /**
     * Call [ViewHolder.bind] with the [CardItem] as parameter
     */
    override fun onBindViewHolder(item: CardItem, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    /**
     * ViewHolder for [CardSetItem]
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var item: CardItem
        private val titleTextView = itemView.listItemCardTitle
        private val repoTextView = itemView.listItemCardRepo

        private val click: View.OnClickListener = View.OnClickListener {
            val bundle = bundleOf("toEdit" to item.cross)
            itemView.findFragment<CardListFragment>().findNavController().navigate(R.id.nav_edit, bundle)
        }

        /**
         * Bind the [CardItem] to the view
         */
        fun bind(item: CardItem) {
            this.item = item
            titleTextView.text = (item.card as FlashcardNormal).front
            titleTextView.setSingleLine()
            repoTextView.text = item.repo.name

            titleTextView.setOnClickListener(click)
            repoTextView.setOnClickListener(click)
        }
    }
}