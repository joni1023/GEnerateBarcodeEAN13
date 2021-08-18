package com.example.generatorbarcode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val barcodeDb=BarcodeDatabase.getDatabase(this)



//        edittext numero
        et_entrada.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 12) {
                    for (num in 0..9) {
                        generatebarcode(num)

                            //--lower teclado
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(tv_codigo.windowToken, 0)
                            //---
                    }

                }
            }

        })
//        fin editext

        b_refresh.setOnClickListener {
            et_entrada.text.clear()
        }
        btn_save.setOnClickListener {
            //guardar
            lifecycleScope.launch {
                var newbar=BarcodeEntity(etiqueta= "dsdsd",valor= tv_codigo.text.toString().toLong())
                barcodeDb.getBarcodeDao().saveBarcode(newbar)
                val cant = barcodeDb.getBarcodeDao().getAllBarcode().size
                tv_titulo.text=cant.toString()

            }

        }
        btn_list.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java).apply {

            }
            startActivity(intent)
        }
        }

    private fun generatebarcode(i :Int){
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                et_entrada.text.toString()+""+i,
                BarcodeFormat.EAN_13,
                800,
                400
            )
                imagcode.setImageBitmap(bitmap)
                tv_codigo.text =et_entrada.text.toString()+""+i
        } catch (e: Exception) {
            //Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}