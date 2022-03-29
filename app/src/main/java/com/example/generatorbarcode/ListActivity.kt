package com.example.generatorbarcode

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajts.androidmads.library.ExcelToSQLite
import com.ajts.androidmads.library.ExcelToSQLite.ImportListener
import com.ajts.androidmads.library.SQLiteToExcel
import com.example.generatorbarcode.adapter.BarcodeAdapter
import com.example.generatorbarcode.databinding.ActivityMylistBinding
import kotlinx.android.synthetic.main.activity_mylist.*
import kotlinx.android.synthetic.main.card_layout.*
import kotlinx.coroutines.launch
import java.io.File


class ListActivity : AppCompatActivity() {
    private lateinit var barcodeDao : BarcodeDao
//    private lateinit var barcodeadpater:BarcodeAdapter
    private lateinit var listaoriginal:List<BarcodeEntity>
    private lateinit var myrecycler:RecyclerView
    lateinit var sqliteToExcel: SQLiteToExcel
    private val VALOR_RETORNO = 1;
    
    var directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
//    private lateinit var mylist:List<BarcodeEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //viewBinding
    val binding = ActivityMylistBinding.inflate(layoutInflater)
    setContentView(binding.root)
    //end viewBinding
    //toolbar
    setSupportActionBar(binding.topAppBar)
    supportActionBar?.setDisplayShowTitleEnabled(false)
    //end toolbar

    val barcodeDB = BarcodeDatabase.getDatabase(this)
    barcodeDao = barcodeDB.getBarcodeDao()
//
//    myrecycler = findViewById<RecyclerView>(R.id.recyclerID)
//    myrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//    barcodeadpater = Custom_adapter()
//
//
//
    intiRecyclerView()
    lifecycleScope.launch {
        val listanew = barcodeDao.getAllBarcode()
        listaoriginal = listanew
//        barcodeadpater.appBarcode(listanew)
//        barcodeadpater.notifyDataSetChanged()
        myrecycler.adapter = BarcodeAdapter(listaoriginal)


    }



    topAppBar.setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
            R.id.importDB -> {
                intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.type = "*/*";
                startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
//                Toast.makeText(this, "coomming son", Toast.LENGTH_SHORT).show();
                true
            }
            R.id.exportDB -> {
                //export
                val file = File(directory_path)
                if (!file.exists()) {
                    Toast.makeText(this@ListActivity, "no existe el directotio", Toast.LENGTH_SHORT).show();

                }
                sqliteToExcel = SQLiteToExcel(applicationContext, "barcode2-db",directory_path);
                val columnsToExclude = ArrayList<String>()
                columnsToExclude.add("id")
                sqliteToExcel.setExcludeColumns(columnsToExclude)
                sqliteToExcel.exportSingleTable("barcode", "export_cod.xls", object :
                    SQLiteToExcel.ExportListener {
                    override fun onStart() {}
                    override fun onCompleted(filePath: String) {
                        Toast.makeText(this@ListActivity, "exportacion finalizada, revise la carpeta Donwload", Toast.LENGTH_SHORT).show()
                    }
                    override fun onError(e: java.lang.Exception) {
                        Toast.makeText(this@ListActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                })
                //end export
                true
            }
            R.id.app_bar_search -> {
                // Handle more item (inside overflow menu) press
                true
            }
            else -> false
        }
    }
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //
        if (resultCode === RESULT_CANCELED) {
            //Cancelado por el usuario
        }
        if (resultCode === RESULT_OK && requestCode === VALOR_RETORNO) {

            val uri = data?.data
            val file = File(uri!!.path) //create path from uri

            val split = file.path.split(":").toTypedArray() //split the path.

            val filePath = split[1] //assign it to a string(your choice).

            //import
            val excelToSQLite = ExcelToSQLite(applicationContext, "barcode2-db");
            excelToSQLite.importFromFile(filePath, object : ImportListener {
                override fun onStart() {}
                override fun onCompleted(dbName: String) {
//                        Toast.makeText(this@ListActivity, "importado", Toast.LENGTH_SHORT).show()
                        lifecycleScope.launch {
                            val listanew = barcodeDao.getAllBarcode()
                            listaoriginal = listanew
//                            barcodeadpater.appBarcode(listanew)
//                            barcodeadpater.notifyDataSetChanged()
                            myrecycler.adapter = BarcodeAdapter(listaoriginal)

                        }


                }
                override fun onError(e: Exception) {
                    Toast.makeText(this@ListActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            })
            // end import
        }


    }

    //Toolbar and menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menulist, menu)
        val search = menu.findItem(R.id.app_bar_search).actionView as SearchView
        search.queryHint="dsdsdsdsds"
        search.tooltipText = "bbbbbbbbbbbbbbbbbbbbbbbbbbbb"
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
//                lifecycleScope.launch {
//                    val listanewsearch = barcodeDao.getSeach(query!!)
//                    listaoriginal = listanewsearch
//                    barcodeadpater.appBarcode(listanewsearch)
//                    barcodeadpater.notifyDataSetChanged()
//                    myrecycler.adapter = barcodeadpater
//
//                }
                return true
            }
        })

        return true
    }

    fun intiRecyclerView(){
        myrecycler = findViewById<RecyclerView>(R.id.recyclerID)
        myrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}