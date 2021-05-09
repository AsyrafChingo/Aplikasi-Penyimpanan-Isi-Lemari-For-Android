package com.dicoding.assosiate.lemariku


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.dicoding.assosiate.lemariku.Class.ClassLemariku
import com.dicoding.assosiate.lemariku.Fragment.Home
import java.text.SimpleDateFormat
import java.util.*

class CustomAdapter(val context: Context , val listLemariku : ArrayList<ClassLemariku>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_timeline_layout, parent , false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  listLemariku.size
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val databaseHelper = DatabaseHelper(context)
        val lemari = listLemariku[position]

        holder.textViewNamaPakaian.text = lemari.namaPakaian
        holder.textViewKategori.text = lemari.kategori
        holder.textviewCuciterakhir.text = holder.textviewCuciterakhir.text.toString() + lemari.tanggalCuciTerakhir
        holder.textviewTglBeli.text = holder.textviewTglBeli.text.toString() + lemari.tanggalBeli

        var foto = BitmapFactory.decodeByteArray(lemari.foto,0,lemari.foto.size)
        holder.foto.setImageBitmap(foto)


        if(listLemariku[position].status == 0)
        {
            //BAJU TERSEDIA DILEMARI
            holder.btnShare.setText("CUCI BAJU")
            holder.textviewStatus.setText("TERSEDIA DI LEMARI")

        }
        else
        {
            //BAJU SEDANG DICUCI
            holder.btnShare.setText("SELESAI DICUCI")
            holder.textviewStatus.setText("SEDANG DICUCI")
        }
        holder.btnHapus.setOnClickListener {
            Toast.makeText(context,"Hapus " , Toast.LENGTH_LONG).show()
            databaseHelper.deletePakaian(lemari.id)

        }

        holder.btnShare.setOnClickListener {
            Toast.makeText(context,"Share " + listLemariku[position].id, Toast.LENGTH_LONG).show()
            if(listLemariku[position].status == 0)
            {
                holder.btnShare.setText("SELESAI DICUCI")
                holder.textviewStatus.setText("SEDANG DICUCI")
                var currentDate = SimpleDateFormat("yyy-MM-dd", Locale.getDefault()).format(Date())
                databaseHelper.updateToLemariku(lemari.id,lemari.namaPakaian,lemari.kategori,lemari.foto,lemari.tanggalBeli,currentDate , 1)

            }
            else
            {
                databaseHelper.updateToLemariku(lemari.id,lemari.namaPakaian,lemari.kategori,lemari.foto,lemari.tanggalBeli,lemari.tanggalCuciTerakhir,0)
                holder.btnShare.setText("CUCI BAJU")
                holder.textviewStatus.setText("TERSEDIA DI LEMARI")
            }
        }


        holder.btnUpdate.setOnClickListener {
            move(lemari)
            Toast.makeText(context,"Update ", Toast.LENGTH_LONG).show()
        }

    }
    fun move(lemari : ClassLemariku)
    {
        val intent = Intent(context,UpdateActivity::class.java)
        intent.putExtra("pakaian" , lemari)
        context.startActivity(intent)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textViewNamaPakaian = itemView.findViewById<TextView>(R.id.tv_item_nama_pakaian)
        val textViewKategori = itemView.findViewById<TextView>(R.id.tv_item_kategori)
        val foto  = itemView.findViewById<ImageView>(R.id.img_item_photo)
        val textviewCuciterakhir = itemView.findViewById<TextView>(R.id.tv_item_cuci_trakhir)
        val textviewTglBeli = itemView.findViewById<TextView>(R.id.tv_item_tgl_beli)
        val textviewStatus = itemView.findViewById<TextView>(R.id.tv_item_status)
        val btnHapus = itemView.findViewById<Button>(R.id.btn_hapus)
        val btnShare = itemView.findViewById<Button>(R.id.btn_set_share)
        val btnUpdate = itemView.findViewById<Button>(R.id.btn_set_update)

    }

}