package com.example.guessphrasesqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_phrase.*

class AddPhrase : AppCompatActivity() {
    private lateinit var db: DatabaseHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_phrase)
        db = DatabaseHandler(this)
        addphrase.setOnClickListener { addPhrase() }
    }
    private fun addPhrase(){
        if(phrase_et.text.isEmpty()){
            Toast.makeText(this, "Error phrase is empty!!", Toast.LENGTH_LONG).show()
        }else{
            db.addPhrases(phrase_et.text.toString())
            phrase_et.text.clear()
            Toast.makeText(this, "Phrase Added successfully!!", Toast.LENGTH_LONG).show()
        }

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