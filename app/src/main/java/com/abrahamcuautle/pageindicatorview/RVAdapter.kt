package com.abrahamcuautle.pageindicatorview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class RVAdapter: RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val viewpager: ViewPager2
        val pageIndicatorView: PageIndicatorView
        init {
            viewpager = view.findViewById(R.id.viewPager)
            pageIndicatorView = view.findViewById(R.id.pageIndicatorView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewpager.adapter = ViewPagerAdater()
        PageIndicatorMediator(holder.viewpager, holder.pageIndicatorView).attach()
    }

    override fun getItemCount(): Int {
      return 20
    }

}