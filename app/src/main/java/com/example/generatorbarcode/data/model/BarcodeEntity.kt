package com.example.generatorbarcode.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barcode")
data class BarcodeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long =0L,

    @ColumnInfo(name = "valor")
    val valor :Long,

    @ColumnInfo(name = "etiqueta")
    var etiqueta:String,

    @ColumnInfo(name= "descripcion")
    var descripcion:String,

)
