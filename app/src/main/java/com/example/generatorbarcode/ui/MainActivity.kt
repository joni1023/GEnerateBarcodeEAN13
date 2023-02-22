package com.example.generatorbarcode.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ajts.androidmads.library.SQLiteToExcel
import com.example.generatorbarcode.R
import com.example.generatorbarcode.data.database.BarcodeDatabase
import com.example.generatorbarcode.data.model.BarcodeEntity
import com.example.generatorbarcode.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {
    private lateinit var barcodeDb: BarcodeDatabase
    private var listaEtiquetas= arrayListOf<String>()
    lateinit var sqliteToExcel: SQLiteToExcel
    lateinit var mAdView : AdView
    var directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    val CHANNEL_ID = "ANDROID_CHANEL"
    val REQUESTCODE = 200;
    lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //end viewBinding
        verificarPermisos()
        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //end toolbar
        //init db
        barcodeDb= BarcodeDatabase.getDatabase(this)
        //end db
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

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
                    }

                }

            }

        })
        //fin editext

        binding.bRefresh.setOnClickListener {
            resetFields()
        }

        binding.btnSave.setOnClickListener {
            //guardar
            if (et_entrada.length()==12) {
                if(binding.textdescriocion.text?.isEmpty() == false){

                    lifecycleScope.launch {
                        var newbar = BarcodeEntity(
                            etiqueta = binding.autoCompleteTextView.editableText.toString(),
                            valor = (text_codigo.text.toString()).toLong(),
                            descripcion = binding.textdescriocion.text.toString(),
                        )
                        barcodeDb.getBarcodeDao().saveBarcode(newbar)
                    }
                    resetFields()

                }else{
                    binding.textdescriocion.error = getString(R.string.completar)
                }

            }else{
                binding.etEntrada.error =getString(R.string.ingreseCodigo)

            }
        }


    }

    private fun resetFields() {
        binding.etEntrada.text?.clear()
        binding.textdescriocion.text?.clear()
        binding.autoCompleteTextView.text?.clear()
        binding.imagcode.setImageResource(R.mipmap.ic_launcher_foreground)
        binding.textCodigo.text = getString(R.string.text_down_cod)
    }

    private fun verificarPermisos() {
        val permisoWrite = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permisoRead = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        if(permisoWrite == PackageManager.PERMISSION_GRANTED && permisoRead == PackageManager.PERMISSION_GRANTED ) {

        }else{
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),REQUESTCODE);
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
//            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
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
            R.id.itemlist -> {
                val intent = Intent(this, ListActivity::class.java).apply {}
            startActivity(intent)
            }
            R.id.itemabout -> {
                MaterialAlertDialogBuilder(this).setTitle("Generador Código EAN13")
                    .setMessage("Desarrollado por JEF1023"+"\n"+"version: 2.0"+"\n"+"Gracias por elegirnos")
                    .setIcon(R.mipmap.ic_launcher_barcode_round)
                    .setPositiveButton("Compartir"){ dialog, which ->
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "https://play.google.com/store/apps/details?id=com.jef1023.generatorbarcode"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                    .setNeutralButton("Calificar"){dialog, which ->
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                        } catch (e: ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                        }

                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // end toolbar and menu

}