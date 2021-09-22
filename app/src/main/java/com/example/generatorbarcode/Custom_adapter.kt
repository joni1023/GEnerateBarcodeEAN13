package com.example.generatorbarcode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.GlobalScope


class Custom_adapter: RecyclerView.Adapter<BarcodeViewHolder>() , View.OnClickListener {

    private val barcodeList = mutableListOf<BarcodeEntity>()
//    private var mlistener:onItemClickListener?=null

//    interface onItemClickListener{
//        fun onItemClick(barcode :BarcodeEntity)
//    }
//     fun setOnItemClickListener(listener: onItemClickListener){
//        mlistener=listener
//     }
private var barcodes=BarcodeEntity(descripcion = "",etiqueta = "",id = 0L,valor = 0L)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
        return BarcodeViewHolder(v)
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val barcode =barcodeList[position]
        barcodes=barcode
        holder.itemEtiqueta.text = barcode.etiqueta
        holder.itemDescripcion.text=barcode.descripcion
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(
            barcode.valor.toString(),
            BarcodeFormat.EAN_13,
            450,
            60
        )
        holder.itemImage.setImageBitmap(bitmap)
//        holder.itemView.setOnClickListener {
//            mlistener?.onItemClick(barcode)
//        }
        holder.itemDescripcion.setOnClickListener(this)
//        holder.btnDelete.setOnClickListener(this)
    }

    override fun getItemCount() = barcodeList.size



    fun appBarcode(list: List<BarcodeEntity>) {
        barcodeList.clear()
        barcodeList.addAll(list)


    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.text_description -> {
                }
//                R.id.btn_delete -> {
////                    var barcodeDb = BarcodeDatabase.getDatabase(v.context)
////                    GlobalScope.launch{
////                        //barcodeDb.getBarcodeDao().deleteBarcode(barcodes)
////                    }
//                    Toast.makeText(v.context, "function no asignada", Toast.LENGTH_SHORT).show()
//
//                }

            }
        }
    }


}
class BarcodeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
       val itemDescripcion = itemView.findViewById<TextView>(R.id.text_description)
       val itemEtiqueta =itemView.findViewById<TextView>(R.id.text_label)
       val itemImage= itemView.findViewById<ImageView>(R.id.img_cod)
//        val btnDelete = itemView.findViewById<Button>(R.id.btn_delete)

}