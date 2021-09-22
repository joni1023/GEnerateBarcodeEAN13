package com.example.generatorbarcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.generatorbarcode.databinding.ActivityMylistBinding
import kotlinx.android.synthetic.main.activity_mylist.*
import kotlinx.android.synthetic.main.card_layout.*
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() ,SearchView.OnQueryTextListener{
    private lateinit var barcodeDao : BarcodeDao
    private lateinit var barcodeadpater:Custom_adapter
    private lateinit var listaoriginal:List<BarcodeEntity>
    private lateinit var myrecycler:RecyclerView
//    private lateinit var mylist:List<BarcodeEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //viewBinding
    val binding = ActivityMylistBinding.inflate(layoutInflater)
    setContentView(binding.root)
    //end viewBinding
    //toolbar
//    setSupportActionBar(binding.toolbarlist)
//    supportActionBar?.setDisplayShowTitleEnabled(false)
    //end toolbar

    val barcodeDB = BarcodeDatabase.getDatabase(this)
    barcodeDao = barcodeDB.getBarcodeDao()

    myrecycler = findViewById<RecyclerView>(R.id.recyclerID)
    myrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    barcodeadpater = Custom_adapter()



    lifecycleScope.launch {
        val listanew = barcodeDao.getAllBarcode()
        listaoriginal = listanew
        barcodeadpater.appBarcode(listanew)
        barcodeadpater.notifyDataSetChanged()
        myrecycler.adapter = barcodeadpater


    }

    binding.searchView.setOnQueryTextListener(this)

}

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        lifecycleScope.launch {
            val listanewsearch = barcodeDao.getSeach(newText!!)
            listaoriginal = listanewsearch
            barcodeadpater.appBarcode(listanewsearch)
            barcodeadpater.notifyDataSetChanged()
            myrecycler.adapter = barcodeadpater


        }
        return false
    }
    //Toolbar and menu
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menulist, menu)
//        return true
//    }
}