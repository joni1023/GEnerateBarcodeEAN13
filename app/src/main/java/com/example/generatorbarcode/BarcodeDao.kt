package com.example.generatorbarcode

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcode")
    suspend fun getAllBarcode():List<BarcodeEntity>
    @Insert
    suspend fun saveBarcode(barcode:BarcodeEntity)
}