package de.hsworms.flashcards.ui.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FlashcardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is flashcard Fragment"
    }
    val text: LiveData<String> = _text
}