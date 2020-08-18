package de.hsworms.flashcards.ui.cardlist

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardItem
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.confirm_dialog.view.*
import kotlinx.android.synthetic.main.list_item_card.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

        private val longClick: View.OnLongClickListener = View.OnLongClickListener {
            val ctx = itemView.findFragment<CardListFragment>().requireContext()
            val popup = PopupMenu(ctx, it)
            popup.menuInflater.inflate(R.menu.card_menu, popup.menu)
            popup.setOnMenuItemClickListener { mi ->
                when (mi.itemId) {
                    R.id.card_menu_delete -> delete()
                    R.id.card_menu_reset -> reset()
                    else -> false
                }
                true
            }
            popup.show()
            true
        }

        private fun reset() {
            val frag = itemView.findFragment<CardListFragment>()
            val ctx = frag.requireContext()
            val mDialogView = LayoutInflater.from(ctx).inflate(R.layout.confirm_dialog, null)
            val mBuilder = AlertDialog.Builder(ctx).setView(mDialogView)
                .setTitle("\"" + (item.card as FlashcardNormal).front + "\" zurücksetzen") // TODO string externalisieren
            val mDeleteDialog = mBuilder.show()
            mDialogView.dialogDeleteBtn.setOnClickListener {
                mDeleteDialog.dismiss()
                GlobalScope.launch {
                    // reset card
                    FCDatabase.getDatabase(ctx).repositoryDao()
                        .update(RepositoryCardCrossRef(item.repo.repoId!!, item.card.cardId!!, 0, 0))
                    frag.fetchData()
                }
            }
            mDialogView.dialogCancelBtn.setOnClickListener {
                mDeleteDialog.dismiss()
            }
        }

        private fun delete() {
            val ctx = itemView.findFragment<CardListFragment>().requireContext()
            val mDialogView = LayoutInflater.from(ctx).inflate(R.layout.confirm_dialog, null)
            val mBuilder = AlertDialog.Builder(ctx).setView(mDialogView)
                .setTitle("\"" + titleTextView.text + "\" löschen") // TODO string externalisieren
            val mDeleteDialog = mBuilder.show()
            mDialogView.dialogDeleteBtn.setOnClickListener {
                mDeleteDialog.dismiss()
                GlobalScope.launch {
                    FCDatabase.getDatabase(ctx).flashcardDao().delete(item.card)
                    itemView.findFragment<CardListFragment>().fetchData()
                }
            }
            mDialogView.dialogCancelBtn.setOnClickListener {
                mDeleteDialog.dismiss()
            }
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
            titleTextView.setOnLongClickListener(longClick)
            repoTextView.setOnClickListener(click)
            repoTextView.setOnLongClickListener(longClick)
        }
    }
}