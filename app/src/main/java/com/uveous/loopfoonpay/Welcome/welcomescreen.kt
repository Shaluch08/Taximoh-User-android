package com.uveous.loopfoonpay.Welcome

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.uveous.loopfoonpay.LoginActivity
import com.uveous.loopfoonpay.R


class welcomescreen : AppCompatActivity(){

    lateinit var viewPager: ViewPager
    lateinit var  button: AppCompatButton
    private var adapter: SliderPagerAdapter? = null
    var click:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcomescreen)

        viewPager = findViewById(R.id.pagerIntroSlider)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        button = findViewById(R.id.button)
        // init slider pager adapter
        // init slider pager adapter
        adapter = SliderPagerAdapter(supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)

        viewPager.setAdapter(adapter)

        tabLayout.setupWithViewPager(viewPager)

        changeStatusBarColor()
        button.setOnClickListener(View.OnClickListener {
            if (viewPager.getCurrentItem() < adapter!!.getCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1)
            }
            if(click==3){
                button.isEnabled=false
                val intent = Intent(this@welcomescreen, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                click=position
            }
            override fun onPageSelected(position: Int) {
                if (position == adapter!!.getCount() - 1) {
                    button.setBackgroundResource(R.drawable.ic_button_bg1)
                    button.setText("Get Started")
                } else {
                    button.setBackgroundResource(R.drawable.ic_button_bg)
                    button.setText("Next")
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.teal_700))
        }
    }
    class SliderPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        override fun getItem(position: Int): Fragment {
            return BlankFragment.newInstance(position)
        }

        // size is hardcoded
        override fun getCount(): Int {
            return 4
        }
    }
}