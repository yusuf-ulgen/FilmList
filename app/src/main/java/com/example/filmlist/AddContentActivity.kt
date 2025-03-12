package com.example.filmlist

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_content)

        val filmDiziSpinner: Spinner = findViewById(R.id.filmDiziSpinner)
        val searchBar: EditText = findViewById(R.id.searchBar)
        val date: EditText = findViewById(R.id.date)
        val ratingBar: SeekBar = findViewById(R.id.ratingBar)
        val ratingText: TextView = findViewById(R.id.ratingText)
        val commentEditText: EditText = findViewById(R.id.commentEditText)
        val spoilerCheckBox: CheckBox = findViewById(R.id.spoilerCheckBox)

        // Tarih Spinner'ları
        val startDaySpinner: Spinner = findViewById(R.id.startDaySpinner)
        val startMonthSpinner: Spinner = findViewById(R.id.startMonthSpinner)
        val startYearSpinner: Spinner = findViewById(R.id.startYearSpinner)
        val endDaySpinner: Spinner = findViewById(R.id.endDaySpinner)
        val endMonthSpinner: Spinner = findViewById(R.id.endMonthSpinner)
        val endYearSpinner: Spinner = findViewById(R.id.endYearSpinner)

        // Film/Dizi Spinner için Adapter
        val options = arrayOf("Film", "Dizi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filmDiziSpinner.adapter = adapter

        // Spinner Seçimi için Listener
        filmDiziSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (position == 0) { // Film
                    date.visibility = android.view.View.VISIBLE
                    startDaySpinner.visibility = android.view.View.GONE
                    startMonthSpinner.visibility = android.view.View.GONE
                    startYearSpinner.visibility = android.view.View.GONE
                    endDaySpinner.visibility = android.view.View.GONE
                    endMonthSpinner.visibility = android.view.View.GONE
                    endYearSpinner.visibility = android.view.View.GONE
                } else { // Dizi
                    date.visibility = android.view.View.GONE
                    startDaySpinner.visibility = android.view.View.VISIBLE
                    startMonthSpinner.visibility = android.view.View.VISIBLE
                    startYearSpinner.visibility = android.view.View.VISIBLE
                    endDaySpinner.visibility = android.view.View.VISIBLE
                    endMonthSpinner.visibility = android.view.View.VISIBLE
                    endYearSpinner.visibility = android.view.View.VISIBLE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // SeekBar için Listener (Puanlama)
        ratingBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratingText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Margin ve Padding için sistem çubuklarına uyum sağlanması
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
