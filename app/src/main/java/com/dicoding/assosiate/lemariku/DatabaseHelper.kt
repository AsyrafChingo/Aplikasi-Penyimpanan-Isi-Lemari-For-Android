package com.dicoding.assosiate.lemariku

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.dicoding.assosiate.lemariku.Class.ClassLemariku
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DatabaseHelper(val context: Context) : SQLiteOpenHelper(context , DATABASE_NAME , null , DATABASE_VERSION )  {
    companion object {
        val DATABASE_VERSION = 5
        val DATABASE_NAME = "peniruasdatabasev3.db"


        val TABLE_NAME = "lemariku"
        val COLUMN_ID = "id"
        val COLUMN_NAMA_PAKAIAN = "nama_pakaian"
        val COLUMN_TANGGAL_BELI = "tanggal_beli"
        val COLUMN_TANGGAL_CUCI_TERAKHIR = "tanggal_cuci_terakhir"
        val COLUMN_KATEGORI = "kategori"
        val COLUMN_FOTO = "foto"
        val COLUMN_STATUS= "status"
    }




    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTableLemariku())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}" )
    }

    fun createTableLemariku() : String
    {
        val sql = "CREATE TABLE ${TABLE_NAME} (" +
                "${COLUMN_ID} INTEGER PRIMARY KEY," +
                "${COLUMN_NAMA_PAKAIAN} TEXT," +
                "${COLUMN_KATEGORI} TEXT," +
                "${COLUMN_FOTO} BLOB," +
                "${COLUMN_TANGGAL_BELI} TEXT," +
                "${COLUMN_TANGGAL_CUCI_TERAKHIR} TEXT," +
                "${COLUMN_STATUS} INTEGER)"
        return sql
    }


    fun insertToLemariku(namaPakaian :String , kategori : String , foto : ByteArray){
        var currentDate = SimpleDateFormat("yyy-MM-dd",Locale.getDefault()).format(Date())
        try {
            val db = writableDatabase
            var values = ContentValues().apply{
                put(COLUMN_FOTO, foto)
                put(COLUMN_NAMA_PAKAIAN, namaPakaian)
                put(COLUMN_KATEGORI, kategori)
                put(COLUMN_TANGGAL_BELI,  currentDate)
                put(COLUMN_TANGGAL_CUCI_TERAKHIR ,  currentDate)
                put(COLUMN_STATUS ,0)
            }
            db?.insert(TABLE_NAME, null , values)
            Log.d("Database Operations" , "Insert Data Successfully")
        }
        catch (ex : SQLException){
            Log.e("Error Insert " , ex.sqlState)
        }
    }

    fun updateToLemariku(id : Int ,namaPakaian :String , kategori : String , foto : ByteArray, tanggalBeli : String , tanggalCuciTerakhir : String  , status : Int ){
        try {
            val db = writableDatabase
            var values = ContentValues().apply{
                put(COLUMN_FOTO, foto)
                put(COLUMN_NAMA_PAKAIAN, namaPakaian)
                put(COLUMN_KATEGORI, kategori)
                put(COLUMN_TANGGAL_BELI,  tanggalBeli)
                put(COLUMN_TANGGAL_CUCI_TERAKHIR ,  tanggalCuciTerakhir)
                put(COLUMN_STATUS,status)
            }
            val selection: String = COLUMN_ID + " = " + id
            db?.update(TABLE_NAME, values , selection,null)
            Log.d("Database Operations" , "Update Data Successfully")
        }
        catch (ex : SQLException){
            Log.e("Error Update " , ex.sqlState)
        }
    }


    fun deletePakaian(_id : Int)
    {
        try {
            val db = writableDatabase
            db?.delete(TABLE_NAME, COLUMN_ID + "=?", arrayOf(_id.toString()))
            Log.d("Database Operations" , "Delete Data Successfully")
        }
        catch (ex : SQLException){
            Log.e("Error Delete " , ex.sqlState)
        }
    }


    fun selectAllPakaian() : ArrayList<ClassLemariku>{

        val lemari = ArrayList<ClassLemariku>()
        try {
            val db = readableDatabase
            val cursor = db.query(
                    TABLE_NAME , null, null , null , null , null , null
            )
            with(cursor){
                while (this.moveToNext()){

                    var id = getInt(getColumnIndex(COLUMN_ID))
                    var namaPakaian : String = getString(getColumnIndex(COLUMN_NAMA_PAKAIAN))
                    var kategori : String = getString(getColumnIndex(COLUMN_KATEGORI))
                    var foto = getBlob(getColumnIndex(COLUMN_FOTO))
                    var tglBeli = getString(getColumnIndex(COLUMN_TANGGAL_BELI))
                    var tglCuciTerakhir : String= getString(getColumnIndex(COLUMN_TANGGAL_CUCI_TERAKHIR))
                    var status = getInt(getColumnIndex(COLUMN_STATUS))

                    val lemariku = ClassLemariku()
                    lemariku.id = id
                    lemariku.foto = foto
                    lemariku.namaPakaian = namaPakaian
                    lemariku.kategori = kategori
                    lemariku.tanggalBeli = tglBeli
                    lemariku.tanggalCuciTerakhir = tglCuciTerakhir
                    lemariku.status = status

                    lemari.add(lemariku)
                }
            }

            Log.d("Database Operations" , "Select Data Successfully")

        }
        catch (ex : Exception)
        {
            Log.e("Error Message" , ex.message)
        }
        return lemari
    }


}