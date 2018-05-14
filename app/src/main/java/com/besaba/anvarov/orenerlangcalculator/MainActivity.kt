package com.besaba.anvarov.orenerlangcalculator

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var rB: Int = 0 // состояние радиокнопки
    private var sb6 = StringBuffer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Ads.showBottomBanner(this)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            var vBHT: Float
            val vBlocking: Float
            val lines: Int
            tv_Otvet.clearComposingText()
            if (!et_1.text.isEmpty() and !et_2.text.isEmpty()) {
                when (rB) {
                    0 -> {
                        vBlocking = et_1.text.toString().toFloat()
                        if (!checkBlocking(vBlocking)) {
                            tv_Otvet.setText(R.string.erBlock)
                            return@setOnClickListener
                        }
                        lines = et_2.text.toString().toInt()
                        if (!checkLines(lines)) {
                            tv_Otvet.setText(R.string.erLine)
                            return@setOnClickListener
                        }
                        vBHT = calcBHT(vBlocking, lines)
                        vBHT = (Math.rint(100.0 * vBHT) / 100.0).toFloat()
                        tv_Otvet.text = vBHT.toString()
                    }
                    1 -> {
                        vBHT = et_1.text.toString().toFloat()
                        if (!checkBHT(vBHT)) {
                            tv_Otvet.setText(R.string.erBHT)
                            return@setOnClickListener
                        }
                        lines = et_2.text.toString().toInt()
                        if (!checkLines(lines)) {
                            tv_Otvet.setText(R.string.erLine)
                            return@setOnClickListener
                        }
                        vBlocking = myRound(calcBlocking(vBHT, lines))
                        if (vBlocking < 0.0001)
                            tv_Otvet.setText(R.string.accuracy)
                        else {
                            sb6.delete(0, sb6.length)
                            sb6.insert(0, vBlocking.toString())
                            tv_Otvet.text = sb6.substring(0, sb6.length)
                        }
                    }
                    2 -> {
                        vBHT = et_1.text.toString().toFloat()
                        if (!checkBHT(vBHT)) {
                            tv_Otvet.setText(R.string.erBHT)
                            return@setOnClickListener
                        }
                        vBlocking = et_2.text.toString().toFloat()
                        if (!checkBlocking(vBlocking)) {
                            tv_Otvet.setText(R.string.erBlock)
                            return@setOnClickListener
                        }
                        lines = calcLines(vBHT, vBlocking)
                        tv_Otvet.text = lines.toString()
                    }
                }
            }
        }

        tv_Otvet.text = ""
        radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.radioButton -> {
                    rB = 0
                    tv_1.setText(R.string.txt2)
                    tv_2.setText(R.string.txt3)
                    tv_Result.setText(R.string.txt1)
                }
                R.id.radioButton2 -> {
                    rB = 1
                    tv_1.setText(R.string.txt4)
                    tv_2.setText(R.string.txt3)
                    tv_Result.setText(R.string.txt2)
                }
                R.id.radioButton3 -> {
                    rB = 2
                    tv_1.setText(R.string.txt4)
                    tv_2.setText(R.string.txt2)
                    tv_Result.setText(R.string.txt3)
                }
            }
        }
        rB = 2
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                onAbout()
                true
            }
            R.id.action_reset -> {
                et_1.setText("")
                et_2.setText("")
                tv_Otvet.text = ""
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onAbout() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

    private fun checkBlocking(iBlocking: Float): Boolean {
        return !((iBlocking <= 0) or (iBlocking >= 1))
    }

    private fun checkBHT(iBHT: Float): Boolean {
        return iBHT > 0
    }

    private fun checkLines(iLines: Int): Boolean {
        return iLines > 1
    }

    private fun calcBHT(p: Float, V: Int): Float {
        val epsilon: Float = p / 1000
        val limit: Float = V.toFloat()
        var delta: Float
        var iA: Float
        var b: Float
        // точность вычисления
        iA = limit / 2
        delta = limit / 4
        b = calcBlocking(iA, V) // пробное вычисление потерь
        while (Math.abs(b - p) > epsilon) {
            if (b > p) {
                iA -= delta
                delta /= 2
            } else
                iA += delta
            b = calcBlocking(iA, V) // пробное вычисление потерь
        }
        return iA
    }

    private fun calcBlocking(Y: Float, V: Int): Float {
        var partial = 1f
        var i = 1
        while (i <= V) {
            partial *= Y
            partial /= (i + partial)
            i++
        }
        return partial
    }

    private fun calcLines(iBHT: Float, iBlocking: Float): Int {
        var partial = 1f
        var ntrunks = 1
        while (ntrunks < Integer.MAX_VALUE) {
            partial *= iBHT
            partial /= (ntrunks + partial)
            if (partial <= iBlocking)
                return ntrunks
            ntrunks++
        }
        return Integer.MAX_VALUE
    }

    private fun myRound(number: Float): Float {
        var pow = 10
        (1..3)
                .forEach {
                    pow *= 10
                }
        val tmp = number * pow
        return (if (tmp - tmp.toInt() >= 0.5f) tmp + 1 else tmp).toInt().toFloat() / pow
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("rB", rB)
        outState.putString("tv_Otvet", tv_Otvet.text.toString())
        outState.putString("tv_Result", tv_Result.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        rB = savedInstanceState.getInt("rB")
        tv_Otvet.text = savedInstanceState.getString("tv_Otvet")
        tv_Result.text = savedInstanceState.getString("tv_Result")
    }
}
