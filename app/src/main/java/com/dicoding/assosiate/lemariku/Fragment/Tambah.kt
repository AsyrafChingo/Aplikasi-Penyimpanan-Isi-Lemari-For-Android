package com.dicoding.assosiate.lemariku.Fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dicoding.assosiate.lemariku.DatabaseHelper

import com.dicoding.assosiate.lemariku.R
import kotlinx.android.synthetic.main.fragment_tambah.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Tambah.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Tambah.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Tambah : Fragment() {


    private val IMAGE_DIRECTORY = "/demopeniruas"

    var btn_simpan : Button? = null
    var spinner_kategori : Spinner? = null
    var input_nama : EditText? = null
    var imageview_p : ImageView? = null


    private val GALLERY = 1
    private val CAMERA = 2

    private val PERMISSION_CODE= 1000
//    var image_rui : Uri? = null
//    private val IMAGE_CAPTURE_CODE= 1001




    var namaPakaian : String = ""
    var kategori : String = ""
    var foto : ByteArray = ByteArray(20000)




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_tambah, container, false)
        btn_simpan = view.findViewById(R.id.btn_tambah)
        spinner_kategori = view.findViewById(R.id.spinner_kategori)
        input_nama = view.findViewById(R.id.input_nama_pakaian)
        imageview_p = view.findViewById(R.id.imageview_pakaian)



        imageview_p!!.setOnClickListener {
            showPictureDialog()
        }

        val option = arrayOf("Thriller" , "CELANA" , "PAKAIAN DALAM")
        spinner_kategori!!.adapter = ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, option)
        spinner_kategori!!.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    kategori = option[position]
            }
        }
        val databaseHelper = DatabaseHelper(context!!)
        btn_simpan!!.setOnClickListener {
            if(input_nama!!.text.toString().isEmpty())
            {
                input_nama!!.setError("Nama Pakaian Harus Diisi")
            }
            else
            {
                namaPakaian = input_nama_pakaian.text.toString()
                databaseHelper.insertToLemariku(namaPakaian,kategori,foto)
                Toast.makeText(context, "Sukses Menambahkan Pakaian Ke lemari", Toast.LENGTH_SHORT).show()
                imageview_p!!.setImageResource(R.drawable.camera)
                input_nama!!.setText("")
                btn_simpan!!.isEnabled = false
            }

        }
        return  view
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context!!)
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
            if(checkSelfPermission(context!!,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(context!!,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
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
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI) as Bitmap
                    var stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG,100, stream)
                    foto = stream.toByteArray()
                    imageview_p!!.setImageBitmap(bitmap)
                    btn_simpan!!.isEnabled = true
                    Toast.makeText(context, "Image Saved from gallery!", Toast.LENGTH_SHORT).show()
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else
        if (requestCode == CAMERA)
        {
            if(data != null)
            {
                val thumbnail = data.extras!!.get("data") as Bitmap
                imageview_p!!.setImageBitmap(thumbnail)
                val path = saveImage(thumbnail)
                var stream = ByteArrayOutputStream()
                thumbnail.compress(Bitmap.CompressFormat.PNG,100, stream)
                foto = stream.toByteArray()
                Toast.makeText(context, "Image Saved At : " + path, Toast.LENGTH_LONG).show()
                btn_simpan!!.isEnabled = true
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
                    Toast.makeText(context,"permisson denied" ,Toast.LENGTH_SHORT).show()
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
            MediaScannerConnection.scanFile(context,
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
