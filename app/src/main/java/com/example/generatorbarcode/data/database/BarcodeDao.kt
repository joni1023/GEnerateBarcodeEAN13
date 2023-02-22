package com.example.generatorbarcode.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.generatorbarcode.data.model.BarcodeEntity

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcode")
    suspend fun getAllBarcode():MutableList<BarcodeEntity>
    @Insert
    suspend fun saveBarcode(barcode: BarcodeEntity)
    @Query("SELECT DISTINCT etiqueta FROM barcode")
    suspend fun getEtiquetas():List<String>
    @Delete
    fun deleteBarcode(barcode: BarcodeEntity)
    @Query("SELECT * FROM barcode WHERE descripcion LIKE '%' || :query || '%'")
    suspend fun getSeach(query:String):List<BarcodeEntity>
}