package org.obebeokeke.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Error

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var formulaTextView: TextView
    private val basicOperations = arrayListOf('+', '-', '*', '/')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.result)
        formulaTextView = findViewById(R.id.formula)

        val allButtons = findViewById<View>(R.id.buttons_layout).touchables
        for (button in allButtons) {
            button as Button
            val buttonText = button.text.toString()

            button.setOnClickListener {
                when (buttonText) {
                    "C" -> {
                        resultTextView.text = "0"
                        formulaTextView.text = String()
                    }
                    "=" -> {
                        try {
                            calculateResult()
                        } catch (error: Error) {
                            Log.d(this::class.java.toString(), "Invalid ")
                        }
                    }
                    String() -> {
                        removeFromFormula()
                    }
                    else -> {
                        addToFormula(button.text)
                    }
                }
            }
        }
    }

    private fun addToFormula(addCharacter: CharSequence) {

        if (addCharacter == "()") {
            if (resultTextView.text.last() !in basicOperations && '(' in resultTextView.text && resultTextView.text.last() != '(') {
                (resultTextView.text.toString() + ")").also { resultTextView.text = it }
                return
            } else {
                if (resultTextView.text == "0") { resultTextView.text = "("; return }  // Replace 0 with '('
                if (resultTextView.text.last().isDigit()) return  // Prevent putting a '(' after a digit

                (resultTextView.text.toString() + "(").also { resultTextView.text = it }
                return
            }
        }

        // Prevent leading zero
        if (resultTextView.text.toString() == "0") {
            if (addCharacter == "00" || addCharacter == "0") return
            if (addCharacter != ".") resultTextView.text = String()
        }

        // Prevent NoSuchElementException crash when char sequence is empty
        if (resultTextView.text.isNotEmpty()) {
            if (resultTextView.text.last() == '.' && addCharacter == ".") return  // Prevent infinite decimal points
        }

        (resultTextView.text.toString() + addCharacter.toString()).also { resultTextView.text = it }
    }

    private fun removeFromFormula() {
        // Instead of showing an empty string, show a 0
        if (resultTextView.text.length == 1) {
            resultTextView.text.also { resultTextView.text = "0" }
        } else {
            resultTextView.text.also { resultTextView.text = it.dropLast(1) }
        }
    }

    private fun calculateResult() {
        // Prevent calculation of expression when no operation is present
        var validFormula = false
        loop@ for (basicOperation in basicOperations) {
            for (thisChar in resultTextView.text) {
                if (basicOperation == thisChar) {
                    validFormula = true
                    break@loop
                }
            }
        }
        if (!validFormula) return

        formulaTextView.text = resultTextView.text
        val result = ExpressionBuilder(resultTextView.text.toString()).build().evaluate()

        // Make the result a double if the user used a decimal number or division
        if ('.' in resultTextView.text || '/' in resultTextView.text) {
            resultTextView.text = result.toString()
        } else {
            resultTextView.text = result.toInt().toString()
        }
    }
}