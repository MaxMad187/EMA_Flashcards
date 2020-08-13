package de.hsworms.flashcards.ui.home

import android.app.AlertDialog
import android.content.Intent
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
import de.hsworms.flashcard.database.entity.Repository
import de.hsworms.flashcards.MainActivity
import de.hsworms.flashcards.R
import de.hsworms.flashcards.ui.CardSetItem
import de.hsworms.flashcards.ui.ListItem
import kotlinx.android.synthetic.main.delete_dialog.view.*
import kotlinx.android.synthetic.main.list_item_card_set.view.*
import kotlinx.android.synthetic.main.rename_dialog.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

        private val longClick: View.OnLongClickListener = View.OnLongClickListener {
            val ctx = itemView.findFragment<HomeFragment>().requireContext()
            val popup = PopupMenu(ctx, it)
            popup.menuInflater.inflate(R.menu.repository_menu, popup.menu)
            popup.setOnMenuItemClickListener {mi ->
                when(mi.itemId) {
                    R.id.repomenu_rename -> rename()
                    R.id.repomenu_delete -> delete()
                    R.id.repomenu_export -> export()
                    else -> false
                }
                true
            }
            popup.show()
            true
        }

        private fun export() {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.type = "application/json"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            MainActivity.SAVE_REPO_ID = item.set.repository.repoId!!
            itemView.findFragment<HomeFragment>().activity?.startActivityForResult(intent, MainActivity.SAVE_DIALOG)
        }

        private fun delete() {
            val frag = itemView.findFragment<HomeFragment>();
            val ctx = frag.requireContext()
            val mDialogView = LayoutInflater.from(ctx).inflate(R.layout.delete_dialog, null)
            val mBuilder = AlertDialog.Builder(ctx).setView(mDialogView).setTitle("\"" + item.set.repository.name + "\" löschen") // TODO string externalisieren
            val mDeleteDialog = mBuilder.show()
            mDialogView.dialogDeleteBtn.setOnClickListener {
                mDeleteDialog.dismiss()
                GlobalScope.launch {
                    // delete cards
                    item.set.cards.forEach {
                        FCDatabase.getDatabase(ctx).flashcardDao().delete(it)
                    }
                    FCDatabase.getDatabase(ctx).repositoryDao().delete(item.set.repository)
                    frag.fetchData()
                }
            }
            mDialogView.dialogCancelBtn.setOnClickListener {
                mDeleteDialog.dismiss()
            }
        }

        private fun rename() {
            val frag = itemView.findFragment<HomeFragment>();
            val ctx = frag.requireContext()
            val mDialogView = LayoutInflater.from(ctx).inflate(R.layout.rename_dialog, null)
            mDialogView.dialogNameEt.setText(item.set.repository.name)
            val mBuilder = AlertDialog.Builder(ctx).setView(mDialogView).setTitle("Kartenstapel umbennen") // TODO string externalisieren
            val mAlertDialog = mBuilder.show()
            mDialogView.dialogRenameBtn.setOnClickListener {
                mAlertDialog.dismiss()
                val name = mDialogView.dialogNameEt.text.toString()
                val repo = Repository(item.set.repository.repoId, name)
                GlobalScope.launch {
                    FCDatabase.getDatabase(ctx).repositoryDao().update(repo)
                    frag.fetchData()
                }
            }
            mDialogView.dialogRNCancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
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
            titleTextView.setOnLongClickListener(longClick)
            shortTimeCardCountTextView.setOnClickListener(click)
            shortTimeCardCountTextView.setOnLongClickListener(longClick)
            middleTimeCardCountTextView.setOnClickListener(click)
            middleTimeCardCountTextView.setOnLongClickListener(longClick)
            longTimeCardCountTextView.setOnClickListener(click)
            longTimeCardCountTextView.setOnLongClickListener(longClick)
        }
    }
}