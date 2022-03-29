package com.example.generatorbarcode

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ajts.androidmads.library.SQLiteToExcel
import com.ajts.androidmads.library.SQLiteToExcel.ExportListener
import com.example.generatorbarcode.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_layout.view.*
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var barcodeDb:BarcodeDatabase
    private var listaEtiquetas= arrayListOf<String>()
    lateinit var sqliteToExcel: SQLiteToExcel
    var directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewBinding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //end viewBinding
        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //end toolbar
        //init db
        barcodeDb=BarcodeDatabase.getDatabase(this)
        //end db



        lifecycleScope.launch {
            listaEtiquetas.addAll(barcodeDb.getBarcodeDao().getEtiquetas())
        }
            val adapter =ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,listaEtiquetas)
        binding.autoCompleteTextView.setAdapter(adapter)

//        edittext numero
        binding.etEntrada.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 12) {
                    for (num in 0..9) {
                        generatebarcode(num)

                            //--lower teclado
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(textdescriocion.windowToken, 0)
                            //---
//                        binding.etEntrada.setSelection(binding.etEntrada.length())
                    }
                    binding.textcodeend.visibility= View.VISIBLE
                }else{
                    binding.textcodeend.visibility= View.INVISIBLE
                }

            }

        })
//        fin editext

        binding.bRefresh.setOnClickListener {
            binding.etEntrada.text?.clear()
        }

        binding.btnSave.setOnClickListener {
            //guardar
            if (et_entrada.length()==12) {
                lifecycleScope.launch {
                    var newbar = BarcodeEntity(
                        etiqueta = binding.autoCompleteTextView.editableText.toString(),
                        valor = (text_codigo.text.toString()).toLong(),
                        descripcion = binding.textdescriocion.text.toString(),
                    )
                    barcodeDb.getBarcodeDao().saveBarcode(newbar)
                }
                binding.etEntrada.text?.clear()
                binding.textdescriocion.text?.clear()
                binding.autoCompleteTextView.text?.clear()
            }else{
                Toast.makeText(this@MainActivity, "el campo debe estar lleno", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun generatebarcode(i :Int){
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                et_entrada.text.toString()+""+i,
                BarcodeFormat.EAN_13,
                700,
                400
            )
                imagcode.setImageBitmap(bitmap)
                text_codigo.text=(et_entrada.text.toString()+""+i)

        } catch (e: Exception) {
            //Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    //Toolbar and menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.itemlist -> {val intent = Intent(this, ListActivity::class.java).apply {}
            startActivity(intent)}
            R.id.itemabout -> {
                MaterialAlertDialogBuilder(this).setTitle("Desarrollado por:")
                    .setMessage("JEF1023")

                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // end toolbar and menu
}