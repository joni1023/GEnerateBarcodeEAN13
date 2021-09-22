package com.example.generatorbarcode

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcode")
    suspend fun getAllBarcode():List<BarcodeEntity>
    @Insert
    suspend fun saveBarcode(barcode:BarcodeEntity)
    @Query("SELECT DISTINCT etiqueta FROM barcode")
    suspend fun getEtiquetas():List<String>
    @Delete
    fun deleteBarcode(barcode: BarcodeEntity)
    @Query("SELECT * FROM barcode WHERE descripcion LIKE '%' || :query || '%'")
    suspend fun getSeach(query:String):List<BarcodeEntity>
}