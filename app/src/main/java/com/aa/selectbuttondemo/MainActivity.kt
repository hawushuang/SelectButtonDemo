package com.aa.selectbuttondemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aa.selectbutton.SelectButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        select_button.setOnClickListener(object : SelectButton.OnClickListener {
            override fun onClick(position: Int, text: String) {
                Toast.makeText(this@MainActivity, "$text：：：$position", Toast.LENGTH_SHORT).show()
            }

        })
        val texts = arrayOf("A", "B")
        select_button.setTexts(texts)
        select_button.setIsLeft(false)
    }
}
