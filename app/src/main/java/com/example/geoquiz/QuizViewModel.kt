package com.example.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_france, true),
        Question(R.string.question_river, false),
        Question(R.string.question_mountain, true),
        Question(R.string.question_italy, true),
        Question(R.string.question_ocean, false),
        Question(R.string.question_niagara, true)
    )

    var clickableButtonArray = arrayOf(true,true,true,true,true,true)
    var count = 0;
    var correctAnswers = 0;

    var currentIndex = 0
    var isCheater = false
    var isCheaterArray = arrayOf(false,false,false,false,false,false)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToPrevious() {
        currentIndex = (currentIndex - 1) % questionBank.size
        if (currentIndex < 0) currentIndex = 0
    }

    fun moveToNext() {
        currentIndex = if (currentIndex == questionBank.size - 1) questionBank.size - 1 else (currentIndex + 1) % questionBank.size
    }

    fun checkClickable(): Boolean {
        return clickableButtonArray[currentIndex]
    }

    fun getQuizSize(): Int {
        return questionBank.size
    }
}