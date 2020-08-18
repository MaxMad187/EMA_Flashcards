package de.hsworms.flashcards.ui.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import de.hsworms.flashcard.database.FCDatabase
import de.hsworms.flashcard.database.entity.FlashcardNormal
import de.hsworms.flashcard.database.entity.RepositoryCardCrossRef
import de.hsworms.flashcards.R
import kotlinx.android.synthetic.main.flashcard_layout.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.header_layout_flashcard.*
import kotlinx.android.synthetic.main.sec_header_time_view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FlashcardFragment : Fragment() {

    private lateinit var flashcardViewModel: FlashcardViewModel

    private val cardList = mutableListOf<ViewCard>()
    private lateinit var activeCard: ViewCard
    private var repoID = -1
    private var countdown = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        flashcardViewModel = ViewModelProviders.of(this).get(FlashcardViewModel::class.java)
        return inflater.inflate(R.layout.flashcard_layout, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(bottomAppBar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flashcardViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        headerHeadlineTextView.text = "Kartenset lernen"
        headerSublineTextView.text = ""

        val crossRef = arguments?.get("crossRefs") as Array<RepositoryCardCrossRef>
        repoID = crossRef[0].repoId
        GlobalScope.launch {
            crossRef.forEach {
                val fc = FCDatabase.getDatabase(requireContext()).flashcardDao().getOne(it.cardId)!!
                cardList.add(ViewCard(fc, it.nextDate, it.interval))
            }
            cardList.shuffle()
            countdown = cardList.size
            requireActivity().runOnUiThread {
                nextCard()
            }
        }

        flashcardAnswerButton.setOnClickListener {
            flashcardAnswerGroup.visibility = View.VISIBLE
            flashcardQuestionGroup.visibility = View.GONE
        }

        flashcardShortTimeButton.setOnClickListener { shortTime() }
        flashcardMiddleTimeButton.setOnClickListener { middleTime() }
        flashcardLongTimeButton.setOnClickListener { longTime() }
    }

    private fun nextCard() {
        if (cardList.isEmpty()) {
            findNavController().navigate(R.id.nav_home)
            return
        }

        if (countdown == 0) {
            cardList.shuffle()
            countdown = cardList.size
        }

        val sh = cardList.filter { it.time != 0L && it.interval <= 0 }.count()
        val mi = cardList.filter { it.time != 0L && it.interval > 0 }.count()
        val lo = cardList.filter { it.time == 0L }.count()

        secHeaderTimeViewShortTimeTextView.text = sh.toString()
        secHeaderTimeViewMiddleTimeTextView.text = mi.toString()
        secHeaderTimeViewLongTimeTextView.text = lo.toString()

        activeCard = cardList.removeAt(0)

        flashcardQuestionTextView.text = (activeCard.card as FlashcardNormal).front
        flashcardAnswerTextView.text = (activeCard.card as FlashcardNormal).back

        flashcardAnswerGroup.visibility = View.GONE
        flashcardQuestionGroup.visibility = View.VISIBLE

        countdown--
    }

    private fun calcEF(ef: Double, q: Int): Double {
        return ef + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
    }

    private fun calcDays(ef: Double, n: Int): Int {
        return when (n) {
            0 -> 0
            1 -> 1
            2 -> 6
            else -> (calcDays(ef, n - 1) * ef).roundToInt()
        }
    }

    private fun shortTime() {
        val vc = ViewCard(activeCard.card, 1, 0, activeCard.ef)
        cardList.add(vc)
        nextCard()
    }

    private fun middleTime() {
        val ef = calcEF(activeCard.ef, 3)
        val vc = ViewCard(activeCard.card, 1, activeCard.interval, ef)
        cardList.add(vc)
        nextCard()
    }

    private fun longTime() {
        val ef = calcEF(activeCard.ef, 5)
        val interval = activeCard.interval + 1
        val days = calcDays(ef, interval)

        val time = System.currentTimeMillis() + 86400000 * days
        val cross = RepositoryCardCrossRef(repoID, activeCard.card.cardId!!, time, interval)

        GlobalScope.launch {
            FCDatabase.getDatabase(requireContext()).repositoryDao().update(cross)
            requireActivity().runOnUiThread {
                nextCard()
            }
        }
    }
}