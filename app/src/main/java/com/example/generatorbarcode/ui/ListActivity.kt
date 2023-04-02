package com.example.generatorbarcode.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajts.androidmads.library.ExcelToSQLite
import com.ajts.androidmads.library.ExcelToSQLite.ImportListener
import com.ajts.androidmads.library.SQLiteToExcel
import com.example.generatorbarcode.R
import com.example.generatorbarcode.ui.adapter.BarcodeAdapter
import com.example.generatorbarcode.data.database.BarcodeDao
import com.example.generatorbarcode.data.database.BarcodeDatabase
import com.example.generatorbarcode.data.model.BarcodeEntity
import com.example.generatorbarcode.databinding.ActivityMylistBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_mylist.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class ListActivity : AppCompatActivity() {
    private val CHANNEL_ID = "GeneradorEAN13"
    private lateinit var barcodeDao : BarcodeDao
    private lateinit var barcodeadpater: BarcodeAdapter
    private lateinit var listaoriginal:MutableList<BarcodeEntity>
    private lateinit var myrecycler:RecyclerView
    lateinit var sqliteToExcel: SQLiteToExcel
    private val VALOR_RETORNO = 1;
    private lateinit var mAdView : AdView
    var directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    val CHANEL_ID = "GeneradorEAN13"
    private lateinit var binding: ActivityMylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //viewBinding
    binding = ActivityMylistBinding.inflate(layoutInflater)
    setContentView(binding.root)
    // fin binding

    createNotificationChannel()

    //toolbar
    setSupportActionBar(binding.topAppBar)
    supportActionBar?.setDisplayShowTitleEnabled(false)
    //end toolbar

    listaoriginal= mutableListOf()
    val barcodeDB = BarcodeDatabase.getDatabase(this)
    barcodeDao = barcodeDB.getBarcodeDao()

    //admob
    MobileAds.initialize(this) {}
    mAdView = binding.adViewList
    val adRequest = AdRequest.Builder().build()
    mAdView.loadAd(adRequest)
    //admob

    intiRecyclerView()




    topAppBar.setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
            R.id.importDB -> {
                intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.type = "application/vnd.ms-excel";
                startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
                true
            }
            R.id.exportDB -> {
                //export---------------------------------------
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
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/vnd.ms-excel"

                            // Optionally, specify a URI for the file that should appear in the
                            // system file picker when it loads.
                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, directory_path.toUri())
                        }
                        val pendingIntent = PendingIntent.getActivity(this@ListActivity,0,intent,0)
                        var builder = NotificationCompat.Builder(this@ListActivity, CHANEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_barcode_barcode)
                            .setContentTitle(getString(R.string.title_notification))
                            .setContentText(getString(R.string.description_notificatiom))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .addAction(R.drawable.ic_about,"ver",pendingIntent)

                        with(NotificationManagerCompat.from(this@ListActivity)) {
                            // notificationId is a unique int for each notification that you must define
                            val notificationId = 0
                            notify(notificationId, builder.build())
                        }
                    }
                    override fun onError(e: java.lang.Exception) {
                        Toast.makeText(this@ListActivity, e.message, Toast.LENGTH_SHORT).show()

                    }
                })
                //end export-----------------------------------------------
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
                        refreshAdapter()

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
        search.queryHint="Buscar"
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                lifecycleScope.launch {
                    val listanewsearch = barcodeDao.getSeach(query!!)
                    refreshAdapter(listanewsearch as MutableList<BarcodeEntity>)
//                    listaoriginal = listanewsearch as MutableList<BarcodeEntity>
//                    myrecycler.adapter = BarcodeAdapter(
//                        barcodeList = listaoriginal,
//                        onClickListener = {barcodeEntity ->onItemSelect(barcodeEntity)},
//                        onClickDelete = {position -> onClickDelete(position)}
//                    )

                }

                return true
            }
        })

        return true
    }

    fun intiRecyclerView(){
        myrecycler = binding.recyclerID
        myrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        refreshAdapter()

    }
    private fun refreshAdapter(listBarcode: MutableList<BarcodeEntity> = mutableListOf()){
        if (listBarcode.size != 0){
            listaoriginal=listBarcode
            barcodeadpater=  BarcodeAdapter(
                barcodeList = listaoriginal,
                onClickListener = {barcodeEntity ->onItemSelect(barcodeEntity)},
                onClickDelete = {position -> onClickDelete(position)}
            )
            myrecycler.adapter = barcodeadpater
        }else{
            lifecycleScope.launch {
                listaoriginal = barcodeDao.getAllBarcode()
                barcodeadpater=  BarcodeAdapter(
                    barcodeList = listaoriginal,
                    onClickListener = {barcodeEntity ->onItemSelect(barcodeEntity)},
                    onClickDelete = {position -> onClickDelete(position)}
                )
                myrecycler.adapter = barcodeadpater
            }
        }



    }
     private fun onItemSelect(barcodeEntity: BarcodeEntity){
         Toast.makeText(this, barcodeEntity.descripcion, Toast.LENGTH_SHORT).show()
     }
    private fun onClickDelete(position:Int){
        val barcodeRemoved = listaoriginal[position]
        GlobalScope.launch {
            barcodeDao.deleteBarcode(barcodeRemoved)
        }
        listaoriginal.removeAt(position)
        myrecycler.adapter?.notifyItemRemoved(position)



    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}