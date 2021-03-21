package com.abrahamcuautle.pageindicatorview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class FirstActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_first_activity)
        val vp = findViewById<ViewPager2>(R.id.viewPager)
        vp.adapter = ViewPagerAdater()

        val piv = findViewById<PageIndicatorView>(R.id.pageIndicatorView)
        PageIndicatorMediator(vp, piv).attach()
    }

}