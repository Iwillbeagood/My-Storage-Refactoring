package com.example.mystorage.ui.info

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.mystorage.databinding.ActivityInfoBinding
import com.example.mystorage.ui.main.MainActivity
import com.example.mystorage.utils.etc.ActivityUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private val viewModel: InfoViewModel by viewModels()
    private val infoFragment1 = InfoFragment1()
    private val infoFragment2 = InfoFragment2()
    private val infoFragment3 = InfoFragment3()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            ActivityUtil.goToNextActivity(this@InfoActivity, MainActivity())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        infoFragment2.viewModel = viewModel
        infoFragment3.viewModel = viewModel

        setViewPager()

        this.onBackPressedDispatcher.addCallback(this, callback) //위에서 생성한 콜백 인스턴스 붙여주기
    }

    private fun setViewPager() {
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
