package com.example.randomjokefetcher

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var buttonFetch: Button
    private lateinit var textViewJoke: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonFetch = findViewById(R.id.buttonFetch)
        textViewJoke = findViewById(R.id.textViewJoke)

        buttonFetch.setOnClickListener {
            fetchJoke()
        }
    }

    private fun fetchJoke() {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = URL("https://api.chucknorris.io/jokes/random")
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 10_000
                    readTimeout = 10_000
                }

                val jokeText = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                    JSONObject(jsonString).getString("value")
                } else {
                    "Error fetching joke: ${connection.responseCode}"
                }

                withContext(Dispatchers.Main) {
                    textViewJoke.text = jokeText
                }
            } finally {
                connection.disconnect()
            }
        }
    }
}
