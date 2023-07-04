package com.example.mystorage.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.mystorage.ui.item.list.ItemListFragment
import com.example.mystorage.ui.item.structure.ItemStrFragment
import com.example.mystorage.ui.item.used_item.UsedItemFragment

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> ItemStrFragment()
            1 -> ItemListFragment()
            2 -> UsedItemFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "구조"
            1 -> "목록"
            2 -> "사용 완료"
            else -> null
        }
    }
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}