package com.example.generatorbarcode.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.generatorbarcode.data.model.BarcodeEntity
import com.example.generatorbarcode.core.BarcodeViewHolder
import com.example.generatorbarcode.R

class BarcodeAdapter (val barcodeList: List<BarcodeEntity>): RecyclerView.Adapter<BarcodeViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_new,parent,false)

        return BarcodeViewHolder(v)
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val item = barcodeList[position]
        holder.render(item)
    }
    //tamaño de la lista
    override fun getItemCount(): Int = barcodeList.size

}