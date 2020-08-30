package de.hsworms.flashcards.ui.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

    private val cardList = mutableListOf<ViewCard>()
    private lateinit var activeCard: ViewCard
    private var repoID = -1
    private var countdown = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.flashcard_layout, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).setSupportActionBar(bottom_app_bar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header_headline_text_view.text = getString(R.string.flashcard_header_headline)
        header_subline_text_view.text = ""

        (arguments?.get("crossRefs") as? Array<*>)?.filterIsInstance<RepositoryCardCrossRef>()?.let { crossRef ->
            repoID = crossRef[0].repoId
            val ctx = requireContext()
            GlobalScope.launch {
                FCDatabase.getDatabase(ctx).apply {
                    crossRef.forEach {
                        val fc = flashcardDao().getOne(it.cardId) ?: return@forEach
                        cardList.add(ViewCard(fc, it.nextDate, it.interval))
                    }
                }
                cardList.shuffle()
                countdown = cardList.size
                requireActivity().runOnUiThread {
                    nextCard()
                }
            }

            flashcard_answer_button.setOnClickListener {
                flashcard_answer_group.visibility = View.VISIBLE
                flashcard_question_group.visibility = View.GONE
            }

            flashcard_short_time_button.setOnClickListener { shortTime() }
            flashcard_middle_time_button.setOnClickListener { middleTime() }
            flashcard_long_time_button.setOnClickListener { longTime() }
        }

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

        sec_header_time_view_short_time_text_view.text = sh.toString()
        sec_header_time_view_middle_time_text_view.text = mi.toString()
        sec_header_time_view_long_time_text_view.text = lo.toString()

        activeCard = cardList.removeAt(0)

        flashcardQuestionTextView.text = (activeCard.card as FlashcardNormal).front
        flashcard_answer_text_view.text = (activeCard.card as FlashcardNormal).back

        flashcard_answer_group.visibility = View.GONE
        flashcard_question_group.visibility = View.VISIBLE

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
        val cross = RepositoryCardCrossRef(repoID, activeCard.card.cardId ?: return, time, interval)

        val ctx = requireContext()
        GlobalScope.launch {
            FCDatabase.getDatabase(ctx).repositoryDao().update(cross)
            requireActivity().runOnUiThread {
                nextCard()
            }
        }
    }
}