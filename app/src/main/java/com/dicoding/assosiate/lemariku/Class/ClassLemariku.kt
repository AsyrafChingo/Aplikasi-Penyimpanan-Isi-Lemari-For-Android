package com.dicoding.assosiate.lemariku.Class

import java.io.Serializable

class ClassLemariku : Serializable {
    var id : Int = 0
    var namaPakaian : String = ""
    var kategori : String = ""
    var tanggalBeli = ""
    var tanggalCuciTerakhir : String = ""
    var foto : ByteArray = ByteArray(20000)
    var status : Int= 0
}