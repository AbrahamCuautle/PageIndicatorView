package com.abrahamcuautle.pageindicatorview

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdater: RecyclerView.Adapter<ViewPagerAdater.ViewPagerHolder>() {

    class ViewPagerHolder(view: ImageView): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view = ImageView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
        }
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val bg = if (position % 2 == 0) R.drawable.image1 else R.drawable.image2
        holder.itemView.setBackgroundResource(bg)
        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context, MainActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
       return 5
    }


}