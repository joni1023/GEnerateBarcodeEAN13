package com.example.generatorbarcode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Custom_adapter: RecyclerView.Adapter<BarcodeViewHolder>() {

    private val barcodeList = mutableListOf<BarcodeEntity>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
        return BarcodeViewHolder(v)
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val barcode =barcodeList[position]
        holder.itemEtiqueta.text = barcode.etiqueta
        holder.itemValor.text=barcode.valor.toString()

    }

    override fun getItemCount() = barcodeList.size



    fun appBarcode(list: List<BarcodeEntity>) {
        barcodeList.clear()
        barcodeList.addAll(list)


    }
}
class BarcodeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
//       val itemImage=itemView.findViewById(R.id.img_cod)
       val itemValor =itemView.findViewById<TextView>(R.id.text_valor)
       val itemEtiqueta =itemView.findViewById<TextView>(R.id.text_label)


}