package com.example.mystorage.ui.info

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.mystorage.R
import com.example.mystorage.databinding.ActivityInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private val viewModel: InfoViewModel by viewModels()
    private val infoFragment1 = InfoFragment1()
    private val infoFragment2 = InfoFragment2()
    private val infoFragment3 = InfoFragment3()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        infoFragment2.viewModel = viewModel
        infoFragment3.viewModel = viewModel

        setViewPager()
    }

    fun setViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        binding.viewPager.adapter = adapter

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) = when(position) {
                0 -> {
                    binding.page1View.setBackgroundColor(Color.BLACK)
                    binding.page2View.setBackgroundColor(Color.GRAY)
                    binding.page3View.setBackgroundColor(Color.GRAY)
                }
                1 -> {
                    binding.page1View.setBackgroundColor(Color.GRAY)
                    binding.page2View.setBackgroundColor(Color.BLACK)
                    binding.page3View.setBackgroundColor(Color.GRAY)
                }
                2 -> {
                    infoFragment2.getSelectedChipValue()
                    binding.page1View.setBackgroundColor(Color.GRAY)
                    binding.page2View.setBackgroundColor(Color.GRAY)
                    binding.page3View.setBackgroundColor(Color.BLACK)
                }
                else -> {}
            }
        })
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragmentList = listOf(
            infoFragment1,
            infoFragment2,
            infoFragment3
        )

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }
}
