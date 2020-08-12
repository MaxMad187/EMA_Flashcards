package de.hsworms.flashcards.ui.cardlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is cardlist Fragment"
    }
    val text: LiveData<String> = _text
}