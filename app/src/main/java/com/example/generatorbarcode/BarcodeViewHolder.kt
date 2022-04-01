package com.example.generatorbarcode

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.generatorbarcode.adapter.BarcodeAdapter
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BarcodeViewHolder(itemView: View): ViewHolder(itemView) {
    val itemDescripcion = itemView.findViewById<TextView>(R.id.text_description)
    val itemEtiqueta =itemView.findViewById<TextView>(R.id.text_label)
    val itemImage= itemView.findViewById<ImageView>(R.id.img_cod)
    val itemCod=itemView.findViewById<TextView>(R.id.text_cod)

    fun render(barcodeModel :BarcodeEntity){
        itemDescripcion.text = barcodeModel.descripcion
        itemEtiqueta.text = barcodeModel.etiqueta
        itemCod.text = barcodeModel.valor.toString()
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(
            barcodeModel.valor.toString(),
            BarcodeFormat.EAN_13,
            450,
            60
        )
        itemImage.setImageBitmap(bitmap)

    }

}