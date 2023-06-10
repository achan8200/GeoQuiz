package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlin.math.roundToInt

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        previousButton = findViewById(R.id.previous_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        trueButton.setEnabled(quizViewModel.checkClickable())
        falseButton.setEnabled(quizViewModel.checkClickable())

        val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            quizViewModel.clickableButtonArray[quizViewModel.currentIndex] = false
            trueButton.setEnabled(quizViewModel.checkClickable())
            falseButton.setEnabled(quizViewModel.checkClickable())
        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            quizViewModel.clickableButtonArray[quizViewModel.currentIndex] = false
            trueButton.setEnabled(quizViewModel.checkClickable())
            falseButton.setEnabled(quizViewModel.checkClickable())
        }

        previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
            trueButton.setEnabled(quizViewModel.checkClickable())
            falseButton.setEnabled(quizViewModel.checkClickable())
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            trueButton.setEnabled(quizViewModel.checkClickable())
            falseButton.setEnabled(quizViewModel.checkClickable())
        }

        cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            quizViewModel.isCheaterArray[quizViewModel.currentIndex] = quizViewModel.isCheater
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        quizViewModel.count++
        if (!quizViewModel.isCheaterArray[quizViewModel.currentIndex] && userAnswer == correctAnswer) {
            quizViewModel.correctAnswers++
        }

        val messageResId = when {
            quizViewModel.isCheaterArray[quizViewModel.currentIndex] -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()

        if (quizViewModel.count == quizViewModel.getQuizSize()) {
            val score: Double = ((quizViewModel.correctAnswers.toDouble() / quizViewModel.count) * 100 * 100.0).roundToInt() / 100.0
            Toast.makeText(this, "Your score: $score%", Toast.LENGTH_SHORT)
                .show()
        }
    }
}