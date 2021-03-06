package com.example.generatorbarcode.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.generatorbarcode.BarcodeEntity
import com.example.generatorbarcode.BarcodeViewHolder
import com.example.generatorbarcode.R

class BarcodeAdapter (val barcodeList: List<BarcodeEntity>): RecyclerView.Adapter<BarcodeViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)

        return BarcodeViewHolder(v)
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val item = barcodeList[position]
        holder.render(item)
    }
    //tamaño de la lista
    override fun getItemCount(): Int = barcodeList.size

}