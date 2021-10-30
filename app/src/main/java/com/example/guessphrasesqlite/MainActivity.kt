package com.example.guessphrasesqlite

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var gameAnswer = "Squid Game"
    var gameAnswerhash = ""
    var guessedLetters = ""
    var isguessPhrase = true
    var letterCount = 0
    var phraseCount = 0
    private lateinit var sharedPreferences: SharedPreferences
     var score = 0
     var highScore = 0
     var randomNumber = -1
    lateinit var phrases:ArrayList<String>
    lateinit var messages:ArrayList<String>
    private lateinit var db: DatabaseHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(this)
        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        highScore = sharedPreferences.getInt("HighScore", 0)
        HighScore.text = "High Score: $highScore"
        getPhrase()
        updateAnswer()
        messages = ArrayList()

        Guessbutton.setOnClickListener{
            checkAnswer()
            recyclerView.adapter = GuessthephraseAdapter(messages)
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }
     fun getRandomNumber(PrandomNumber:Int):Int{
        randomNumber = Random.nextInt(0,phrases.size)
         if(randomNumber==PrandomNumber){
             return getRandomNumber(PrandomNumber)
         }else{
             return randomNumber
         }
    }
    private fun getPhrase(){
       phrases=db.viewPhrases()
        randomNumber= getRandomNumber(randomNumber)
        gameAnswer=phrases[randomNumber]
        check_lettersPhrase(' ')
        Log.d("fff","the Answer is $gameAnswer")
    }

    fun checkAnswer(){
        phraseCount++
        val user_Guess = userGuess.text.toString()
        var phraseguessesLeft = 10 - phraseCount
        if(isguessPhrase){
            if(user_Guess.toUpperCase() == gameAnswer.toUpperCase()){
                updateScore()
                Alert("You win!")
            }else{
                messages.add("Wrong guess: $user_Guess")
                if(phraseCount<10){messages.add("$phraseguessesLeft phrases guesses remaining")}
                isguessPhrase = false
                updateAnswer()
            }
        }else{
            if(user_Guess.isNotEmpty() && user_Guess.length==1){
                isguessPhrase = true
                checkAnswer_letters(user_Guess)
            }else{
                Snackbar.make(mainLayout, "Please enter only one letter!!", Snackbar.LENGTH_LONG).show()
            }
        }

        userGuess.text.clear()
    }
    fun checkAnswer_letters(letter:String ){
        var numberfund = 0
        letterCount++
        var guessesLeft = 10 - letterCount
        var letter_char:Char=' '
        for(char in gameAnswer.indices){
            if(gameAnswer[char].toString().toUpperCase()
                == letter.toUpperCase()){
                numberfund++
            }
        }
        if(numberfund>0){
            messages.add("Find $numberfund ${letter.toUpperCase()}(s)")
        }else{
            messages.add("No ${letter.toUpperCase()} is found")
        }

        if(letterCount<10){messages.add("$guessesLeft letters guesses remaining")}

        guessedLetters = letter
        letter_char = letter.single()
        check_lettersPhrase(letter_char.toUpperCase())
        updateAnswer()
    }
    fun check_lettersPhrase(letters:Char){
        var gameAnswerhash_new:String = gameAnswerhash
        gameAnswerhash=""
        if(letters==' '){
            for(char in gameAnswer.indices){
                if(gameAnswer[char] == ' '){
                    gameAnswerhash +=' '
                }else{
                    gameAnswerhash +='*'
                }
            }
        }else{
            for(char in gameAnswer.indices){
                if(gameAnswer[char].toUpperCase() == ' '){
                    gameAnswerhash +=' '
                }else if(gameAnswer[char].toUpperCase() == letters){
                    gameAnswerhash += "$letters"

                }else{
                    if(gameAnswerhash_new[char].toUpperCase() != '*'){
                        gameAnswerhash += gameAnswerhash_new[char]
                    }else{
                        gameAnswerhash +='*'
                    }

                }
            }
        }
    }
    private fun updateScore(){
        score = 10 - letterCount
        if(score >= highScore){
            highScore = score
            with(sharedPreferences.edit()) {
                putInt("HighScore", highScore)
                apply()
            }
            Snackbar.make(mainLayout, "NEW HIGH SCORE!!", Snackbar.LENGTH_LONG).show()
        }
    }
    fun updateAnswer(){
        PhrasetextView.text = "Phrase: " + gameAnswerhash.toUpperCase()
        LetterstextView.text = "Guessed Letters: "+guessedLetters.toUpperCase()
        HighScore.text = "High Score: $highScore"
        if(isguessPhrase){
            userGuess.hint = "Guess the full phrase"
        }else{
            userGuess.hint = "Guess a letter"
        }
    }
    private fun Alert(message:String){
        // first we create a variable to hold an AlertDialog builder
        val dialogBuilder = AlertDialog.Builder(this)
        // then we set up the input

        // here we set the message of our alert dialog
        dialogBuilder.setTitle(message)
            // positive button text and action
            .setPositiveButton("Try again", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
                guessedLetters=""
                letterCount = 0
                phraseCount = 0
                messages.clear()
                getPhrase()
                isguessPhrase = true
                updateAnswer()



            })
        // negative button text and action
        // create dialog box
        val alert = dialogBuilder.create()
        // show alert dialog
        alert.show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addPhrase -> {
                val intent = Intent(this, AddPhrase()::class.java)
                startActivity(intent);
                return true
            }
            R.id.game -> {
                val intent = Intent(this, MainActivity()::class.java)
                startActivity(intent);
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}