package com.dicoding.assosiate.lemariku

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import com.dicoding.assosiate.lemariku.Class.ClassLemariku
import kotlinx.android.synthetic.main.activity_update.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private val IMAGE_DIRECTORY = "/demopeniruas"
    private val GALLERY = 1
    private val CAMERA = 2

    private val PERMISSION_CODE= 1000


    var namaPakaian = ""
    var kategori = ""
    var foto = ByteArray(2000)
    var terakhirCuci = ""
    var tglBeli = ""
    var jumlahPakaiStlhCuci = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        val intentLemari = intent.extras.getSerializable("pakaian") as ClassLemariku

        imageview_pakaian!!.setOnClickListener {
            showPictureDialog()
        }

        val option = arrayOf("BAJU" , "CELANA" , "PAKAIAN DALAM")
        spinner_kategori!!.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, option)
        spinner_kategori!!.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                kategori = option[position]
            }
        }
        val databaseHelper = DatabaseHelper(this)

        btn_update!!.setOnClickListener {
            namaPakaian = input_nama_pakaian.text.toString()
            terakhirCuci = input_terakhir_cuci.text.toString()
            tglBeli = input_tgl_beli.text.toString()
            databaseHelper.updateToLemariku(intentLemari.id , namaPakaian,kategori,foto,tglBeli,terakhirCuci , 0)
        }

        input_nama_pakaian.setText(intentLemari.namaPakaian)
        for (i in 0 until option.size)
        {
            if(intentLemari.kategori == option.get(i))
            {
                spinner_kategori.setSelection(i)
                break
            }
        }

        //MEMBUAT PILIHAN KALENDER UNTUK MENGUBAH TANGGALNYA
        val c= Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        input_tgl_beli.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->

                input_tgl_beli.setText(""+mYear+"-"+mMonth+"-"+mDay)
            },year,month,day)
            dpd.show()
        }

        input_terakhir_cuci.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->

                input_terakhir_cuci.setText(""+mYear+"-"+mMonth+"-"+mDay)
            },year,month,day)
            dpd.show()
        }



        //TERIMA INTENT DARI DATA YANG DI PILIH DI LIST
        var intentFoto = BitmapFactory.decodeByteArray(intentLemari.foto,0,intentLemari.foto.size)
        imageview_pakaian.setImageBitmap(intentFoto)
        foto = intentLemari.foto
        input_tgl_beli.setText(intentLemari.tanggalBeli)
        input_terakhir_cuci.setText(intentLemari.tanggalCuciTerakhir)

    }



    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog ,which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            )
            {
                //permission was enabled
                val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission , PERMISSION_CODE)
            }
            else
            {
                //ALREADY GRANTED
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }
        }
        else{
            //system os under marsmellow
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }

    }





    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data.data as Uri
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI) as Bitmap
                    var stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG,100, stream)
                    foto = stream.toByteArray()
                    imageview_pakaian!!.setImageBitmap(bitmap)

                    Toast.makeText(this, "Image Saved from gallery!", Toast.LENGTH_SHORT).show()


                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else
            if (requestCode == CAMERA)
            {
                if(data != null)
                {
                    val thumbnail = data.extras!!.get("data") as Bitmap
                    imageview_pakaian!!.setImageBitmap(thumbnail)
//                saveImage(thumbnail)
                    val path = saveImage(thumbnail)
                    var stream = ByteArrayOutputStream()
                    thumbnail.compress(Bitmap.CompressFormat.PNG,100, stream)
                    foto = stream.toByteArray()
                    Toast.makeText(this, "Image Saved! " + path, Toast.LENGTH_LONG).show()
//                val intent = Intent(context, AddNewsActivity::class.java)
//                intent.putExtra("bitmap" , thumbnail)
//                intent.putExtra("path", path)
//                startActivity(intent)
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            PERMISSION_CODE->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){

                }
                else{
                    Toast.makeText(this,"permisson denied" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
}
