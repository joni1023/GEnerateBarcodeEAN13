package com.example.generatorbarcode.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajts.androidmads.library.ExcelToSQLite
import com.ajts.androidmads.library.ExcelToSQLite.ImportListener
import com.ajts.androidmads.library.SQLiteToExcel
import com.example.generatorbarcode.R
import com.example.generatorbarcode.Utils
import com.example.generatorbarcode.ui.adapter.BarcodeAdapter
import com.example.generatorbarcode.data.database.BarcodeDao
import com.example.generatorbarcode.data.database.BarcodeDatabase
import com.example.generatorbarcode.data.model.BarcodeEntity
import com.example.generatorbarcode.databinding.ActivityMylistBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_mylist.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ListActivity : AppCompatActivity() {
    private val CHANNEL_ID = "GeneradorEAN13"
    private lateinit var barcodeDao : BarcodeDao
    private lateinit var barcodeadpater: BarcodeAdapter
    private lateinit var listaoriginal:MutableList<BarcodeEntity>
    private var listaEtiquetas= arrayListOf<String>()
    private lateinit var myrecycler:RecyclerView
    lateinit var sqliteToExcel: SQLiteToExcel
    private val VALOR_RETORNO = 1;
    private lateinit var mAdView : AdView
    var directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    val CHANEL_ID = "GeneradorEAN13"
    private lateinit var binding: ActivityMylistBinding
    lateinit var myprefrences: SharedPreferences
    private val COMPARTIR_REQUEST_CODE = 123
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0



    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //viewBinding
    binding = ActivityMylistBinding.inflate(layoutInflater)
    setContentView(binding.root)
        myprefrences= this.getSharedPreferences("sa",Context.MODE_PRIVATE) ?: return
    // fin binding

    createNotificationChannel()




    //toolbar
    setSupportActionBar(binding.topAppBar)
    supportActionBar?.setDisplayShowTitleEnabled(false)
    //end toolbar

    listaoriginal= mutableListOf()
    val barcodeDB = BarcodeDatabase.getDatabase(this)
    barcodeDao = barcodeDB.getBarcodeDao()
        lifecycleScope.launch {
            listaEtiquetas.addAll(barcodeDB.getBarcodeDao().getEtiquetas())
        }

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
                        val pendingIntent = PendingIntent.getActivity(this@ListActivity,0,intent,PendingIntent.FLAG_MUTABLE)
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
            excelToSQLite.importFromFile(directory_path, object : ImportListener {
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // Comprueba si el permiso fue concedido
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, puedes realizar la operación
                    // Guardar el Bitmap en el almacenamiento externo, etc.
                } else {
                    // Permiso denegado, manejar según sea necesario
                }
            }
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

                }

                return true
            }
        })

        return true
    }

    fun intiRecyclerView(){
        myrecycler = binding.recyclerID
        myrecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            listaoriginal = barcodeDao.getAllBarcode()
            barcodeadpater=  BarcodeAdapter(
                barcodeList = listaoriginal,
                onClickUpdate = {position -> onClickUpdate(position)},
                onClickDelete = {position -> onClickDelete(position)},
                onClickShare = {position -> onClickShare(position)}
            )
            myrecycler.adapter = barcodeadpater
        }
    }

    private fun onClickDonwload(position: Int) {
        Toast.makeText(this, "donload", Toast.LENGTH_SHORT).show()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Si no tienes el permiso, solicítalo
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        } else {
            // Si ya tienes el permiso, puedes continuar con la operación
            // Guardar el Bitmap en el almacenamiento externo, etc.
        }
        var barcodeU = listaoriginal[position]
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(
            barcodeU.valor.toString(),
            BarcodeFormat.EAN_13,
            450,
            60
        )

        val estadoAlmacenamiento = Environment.getExternalStorageState()

        if (estadoAlmacenamiento == Environment.MEDIA_MOUNTED) {
            // Obtener el directorio de almacenamiento externo
            val directorioAlmacenamientoExterno = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            if (directorioAlmacenamientoExterno != null) {
                // Crear un archivo en el directorio de almacenamiento externo
                val archivo = File(directorioAlmacenamientoExterno, "nombreArchivo")

                try {
                    // Abrir un OutputStream y guardar el Bitmap
                    val stream = FileOutputStream(archivo)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun onClickUpdate(position : Int) {

        var barcodeU = listaoriginal[position]
        val view = LayoutInflater.from(this).inflate(R.layout.update_dialog,null)
        val inputTextDescription = view.findViewById<EditText>(R.id.textdescriocionupdate)
        val cometiquetas = view.findViewById<AutoCompleteTextView>(R.id.aUpdate)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,listaEtiquetas)
        cometiquetas.setAdapter(adapter)
        inputTextDescription.setText(barcodeU.descripcion)
        cometiquetas.setText(barcodeU.etiqueta)

        MaterialAlertDialogBuilder(this)
            .setTitle("Modificar codigo de barra")
            .setMessage("Desea modificar el codigo numero ${barcodeU.valor.toString()}?")
            .setView(view)
            .setPositiveButton(R.string.accept){ dialog , which ->
                barcodeU.descripcion=inputTextDescription.text.toString()
                barcodeU.etiqueta = cometiquetas.text.toString()
                lifecycleScope.launch {
                    try {
                        barcodeDao.updateBarcode(barcodeU)
                    }catch (e:Exception){
                        Toast.makeText(this@ListActivity, "error update", Toast.LENGTH_SHORT).show()
                    }
                }
                myrecycler.adapter?.notifyItemChanged(position)
                Toast.makeText(this, "Se modifico correctamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel){dialog, which ->
                Toast.makeText(this, "Cancelo modificacion", Toast.LENGTH_SHORT).show()
            }
            .show()






    }

    private fun onClickShare(position: Int) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(
            listaoriginal[position].valor.toString(),
            BarcodeFormat.EAN_13,
            450,
            60
        )
// Crear un archivo Uri para la imagen
        val imagePath = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            listaoriginal[position].descripcion.toString(),
            null)
        val imageUri = Uri.parse(imagePath)
        val editorpref= myprefrences.edit()
        editorpref.putString("myUri",imagePath)
        editorpref.commit()

// Crear un intent para compartir la imagen
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

// Mostrar el selector de aplicaciones para compartir
        startActivityForResult(Intent.createChooser(shareIntent, "Compartir imagen"), COMPARTIR_REQUEST_CODE)

    }

    private fun refreshAdapter(listBarcode: MutableList<BarcodeEntity> = mutableListOf()){
        barcodeadpater.updateBarcodes(listBarcode)
    }
     private fun onItemSelect(barcodeEntity: BarcodeEntity){
         Toast.makeText(this, barcodeEntity.descripcion, Toast.LENGTH_SHORT).show()
     }
    private fun onClickDelete(position:Int){
        val barcodeRemoved = listaoriginal[position]
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar codigo")
            .setMessage(resources.getString(R.string.msj_delete) +" "+ barcodeRemoved.valor )

            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                GlobalScope.launch {
                    barcodeDao.deleteBarcode(barcodeRemoved)
                }
                listaoriginal.removeAt(position)
                myrecycler.adapter?.notifyItemRemoved(position)
            }
            .show()
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