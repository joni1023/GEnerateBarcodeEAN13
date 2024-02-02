package com.example.generatorbarcode.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.generatorbarcode.data.model.BarcodeEntity
import com.example.generatorbarcode.core.BarcodeViewHolder
import com.example.generatorbarcode.R

class BarcodeAdapter (
    private var barcodeList: List<BarcodeEntity>,
    private val onClickDelete: (Int) -> Unit,
    private val onClickShare: (Int) -> Unit,
    private val onClickUpdate: (Int) -> Unit,
    ): RecyclerView.Adapter<BarcodeViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_new,parent,false)

        return BarcodeViewHolder(v)
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val item = barcodeList[position]
        holder.render(item, onClickUpdate, onClickDelete, onClickShare)
    }
    //tamaño de la lista
    override fun getItemCount(): Int = barcodeList.size

    fun updateBarcodes (barcodeList : List<BarcodeEntity>){
        this.barcodeList = barcodeList
        notifyDataSetChanged()
    }

}