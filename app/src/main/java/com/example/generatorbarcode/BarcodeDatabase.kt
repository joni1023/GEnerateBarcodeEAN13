package com.example.generatorbarcode

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    version = 1,
    entities = [BarcodeEntity::class],
    exportSchema = true
)
abstract class BarcodeDatabase: RoomDatabase(){
    abstract fun getBarcodeDao():BarcodeDao

    companion object{
        @Volatile
        private var INSTANCE:BarcodeDatabase?=null
        fun getDatabase(context: Context):BarcodeDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance= Room.databaseBuilder(context,BarcodeDatabase::class.java,"barcode2-db").build()

                INSTANCE= instance

                return instance
            }
        }

    }

}