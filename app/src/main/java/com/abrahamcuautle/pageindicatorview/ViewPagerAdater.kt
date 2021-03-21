package com.abrahamcuautle.pageindicatorview

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdater: RecyclerView.Adapter<ViewPagerAdater.ViewPagerHolder>() {

    private val colors = arrayListOf(Color.BLUE, Color.MAGENTA, Color.GREEN, Color.YELLOW, Color.CYAN)

    class ViewPagerHolder(view: View): RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view = View(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
        }
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val color = colors[position]
        holder.itemView.setBackgroundColor(color)
        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context, MainActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
       return colors.size
    }


}