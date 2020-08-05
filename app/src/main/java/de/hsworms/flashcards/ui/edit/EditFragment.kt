package de.hsworms.flashcards.ui.edit

import android.os.Bundle
import android.util.Log
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
        (activity as AppCompatActivity).setSupportActionBar(bottomAppBar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        headerHeadlineTextView.text = "Karte bearbeiten/erstellen"
        headerSublineTextView.text = ""

        GlobalScope.launch {
            val repos = FCDatabase.getDatabase(requireContext()).repositoryDao().getAllRepositoriesWithCards().toTypedArray()
            requireActivity().runOnUiThread {
                repositorySpinner.adapter = RepositoryAdapter(requireContext(), android.R.layout.simple_spinner_item, repos)
            }

            if (arguments?.containsKey("toEdit")!!) {
                cross = arguments?.get("toEdit") as RepositoryCardCrossRef
                val index = repos.indexOfFirst { it.repository.repoId == cross?.repoId }
                repositorySpinner.setSelection(index)

                val fn = FCDatabase.getDatabase(requireContext()).flashcardDao().getFlashcardNormal(cross?.cardId!!)

                requireActivity().runOnUiThread {
                    cardEditFront.setText(fn?.front)
                    cardEditBack.setText(fn?.back)
                }
            }
        }

        bottomAppBarFab.setOnClickListener {
            saveCard()
        }
    }

    private fun saveCard() {
        val front = cardEditFront.text.toString()
        val back = cardEditBack.text.toString()
        if(front.isEmpty()) {
            Toast.makeText(requireContext(), "Bitte geben Sie eine Vorderseite an!", Toast.LENGTH_SHORT).show()
            return
        }
        if(back.isEmpty()) {
            Toast.makeText(requireContext(), "Bitte geben Sie eine Rückseite an!", Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch {
            val fn = FlashcardNormal(cross?.cardId, front, back)
            var id = cross?.cardId
            if(cross == null) {
                id = FCDatabase.getDatabase(requireContext()).flashcardDao().insert(fn)[0]
                val cross = RepositoryCardCrossRef((repositorySpinner.selectedItem as RepositoryWithCards).repository.repoId!!, id!!, 0, 0)
                FCDatabase.getDatabase(requireContext()).repositoryDao().addCard(cross)
            } else {
                FCDatabase.getDatabase(requireContext()).flashcardDao().update(fn)
                val cross = RepositoryCardCrossRef((repositorySpinner.selectedItem as RepositoryWithCards).repository.repoId!!, id!!, cross?.nextDate!!, cross?.interval!!)
                FCDatabase.getDatabase(requireContext()).repositoryDao().update(cross)
            }


            cross = null

            requireActivity().runOnUiThread {
                cardEditFront.setText("")
                cardEditBack.setText("")
            }
        }
    }
}