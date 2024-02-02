package com.example.generatorbarcode.core


import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.*
import com.example.generatorbarcode.R
import com.example.generatorbarcode.data.model.BarcodeEntity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


class BarcodeViewHolder(itemView: View): ViewHolder(itemView) {

    val itemDescripcion = itemView.findViewById<TextView>(R.id.cl_text_description)
    val itemEtiqueta =itemView.findViewById<TextView>(R.id.cl_text_label)
    val itemImage= itemView.findViewById<ImageView>(R.id.cl_img_cod)
    val itemCod=itemView.findViewById<TextView>(R.id.cl_text_cod)
    val btn_delete = itemView.findViewById<Button>(R.id.cl_button_delete)
    val btn_edit = itemView.findViewById<Button>(R.id.cl_button_edit)
    val btn_share = itemView.findViewById<Button>(R.id.cl_button_share)



    fun render(
        barcodeModel: BarcodeEntity,
        onClickUpdate: (Int) -> Unit,
        onClickDelete: (Int) -> Unit,
        onClickShare: (Int) -> Unit
    ){
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
        btn_share.setOnClickListener { onClickShare(bindingAdapterPosition) }
        btn_delete.setOnClickListener { onClickDelete(bindingAdapterPosition)}
        btn_edit.setOnClickListener { onClickUpdate(bindingAdapterPosition) }

    }

}