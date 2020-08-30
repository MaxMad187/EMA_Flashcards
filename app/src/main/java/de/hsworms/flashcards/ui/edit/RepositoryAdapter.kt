package de.hsworms.flashcards.ui.edit

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import de.hsworms.flashcard.database.entity.RepositoryWithCards

class RepositoryAdapter(ctx: Context, val res: Int, private val arr: Array<RepositoryWithCards>) :
    ArrayAdapter<RepositoryWithCards>(ctx, res, arr) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.text = arr[position].repository.name
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.text = arr[position].repository.name
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        return label
    }
}