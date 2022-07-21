package com.example.calypsodivelog.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.calypsodivelog.R
import com.example.calypsodivelog.service.ClickListenerPhotosDivelog

class PhotosDiveLogAdapter(private val listener: ClickListenerPhotosDivelog) :
    RecyclerView.Adapter<PhotosDiveLogHolder>() {
    private val itemList = mutableListOf<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosDiveLogHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item_view, parent, false)
        return PhotosDiveLogHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotosDiveLogHolder, position: Int) {
        val item: Bitmap = itemList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            // Metodo sobreescrito de la interfaz 'ClickListenerPhotosDivelog'
            listener.itemSelectedPhoto(itemList[position], holder.itemView, position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(list: MutableList<Bitmap>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }
}

/*
*************************** HOLDER - Data List***************************************
* */
class PhotosDiveLogHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val ivPhoto: ImageView = v.findViewById(R.id.imageViewPhoto)

    fun bind(photo: Bitmap) {
        ivPhoto.setImageBitmap(photo)
    }
}