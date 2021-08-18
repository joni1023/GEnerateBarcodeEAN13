package com.example.generatorbarcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_mylist.*
import kotlinx.android.synthetic.main.card_layout.*
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {
    private lateinit var barcodeDao : BarcodeDao
    private lateinit var barcodeadpater:Custom_adapter
//    private lateinit var mylist:List<BarcodeEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_mylist)

    val barcodeDB = BarcodeDatabase.getDatabase(this)
    barcodeDao = barcodeDB.getBarcodeDao()

    val recycler = findViewById<RecyclerView>(R.id.recyclerID)
    recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    barcodeadpater = Custom_adapter()



    lifecycleScope.launch {
        val listanew = barcodeDao.getAllBarcode()

        barcodeadpater.appBarcode(listanew)
        barcodeadpater.notifyDataSetChanged()
        recycler.adapter = barcodeadpater


    }

}
}