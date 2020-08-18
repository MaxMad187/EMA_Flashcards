package de.hsworms.flashcards.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcard.database.entity.RepositoryWithCards
import de.hsworms.flashcards.R
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.header_layout_generic.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditFragment : Fragment() {

    private lateinit var editViewModel: EditViewModel
    private var cross: RepositoryCardCrossRef? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        editViewModel = ViewModelProviders.of(this).get(EditViewModel::class.java)
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(bottom_app_bar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        header_headline_text_view.text = getString(R.string.edit_header_headline)
        header_subline_text_view.text = ""

        val ctx = requireContext()
        GlobalScope.launch {
            FCDatabase.getDatabase(ctx).apply {
                val repos = repositoryDao().getAllRepositoriesWithCards().toTypedArray()
                requireActivity().runOnUiThread {
                    repository_spinner.adapter = RepositoryAdapter(ctx, android.R.layout.simple_spinner_item, repos)
                }

                if (arguments?.containsKey("toEdit")!!) {
                    cross = arguments?.get("toEdit") as RepositoryCardCrossRef
                    val index = repos.indexOfFirst { it.repository.repoId == cross?.repoId }
                    repository_spinner.setSelection(index)

                    val fn = flashcardDao().getFlashcardNormal(cross?.cardId!!)

                    requireActivity().runOnUiThread {
                        card_edit_front.setText(fn?.front)
                        card_edit_back.setText(fn?.back)
                    }
                }
            }
        }

        bottom_app_bar_fab.setOnClickListener {
            saveCard()
        }
    }

    private fun saveCard() {
        val front = card_edit_front.text.toString()
        val back = card_edit_back.text.toString()
        if (front.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.save_missing_front), Toast.LENGTH_SHORT).show()
            return
        }
        if (back.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.save_missing_back), Toast.LENGTH_SHORT).show()
            return
        }

        val ctx = requireContext()
        GlobalScope.launch {
            FCDatabase.getDatabase(ctx).apply {
                val fn = FlashcardNormal(cross?.cardId, front, back)
                var id = cross?.cardId
                if (cross == null) {
                    id = flashcardDao().insert(fn)[0]
                    val cross = RepositoryCardCrossRef(
                        (repository_spinner.selectedItem as RepositoryWithCards).repository.repoId!!,
                        id, 0, 0
                    )
                    repositoryDao().addCard(cross)
                } else {
                    flashcardDao().update(fn)
                    val cross = RepositoryCardCrossRef(
                        (repository_spinner.selectedItem as RepositoryWithCards).repository.repoId!!,
                        id!!,
                        cross?.nextDate!!,
                        cross?.interval!!
                    )
                    repositoryDao().update(cross)
                }
            }

            cross = null

            requireActivity().runOnUiThread {
                card_edit_front.setText("")
                card_edit_back.setText("")
            }
        }
    }
}