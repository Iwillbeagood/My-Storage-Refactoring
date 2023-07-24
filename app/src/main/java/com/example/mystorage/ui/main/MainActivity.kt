package com.example.mystorage.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.example.mystorage.databinding.ActivityMainBinding
import com.example.mystorage.ui.info.InfoActivity
import com.example.mystorage.R
import com.example.mystorage.ui.item.add.ItemAddFragment
import com.example.mystorage.ui.main.MainViewModel.*
import com.example.mystorage.ui.main.adapter.MyPagerAdapter
import com.example.mystorage.ui.info.InfoNameEditFragment
import com.example.mystorage.ui.shoppingList.ShoppingListFragment
import com.example.mystorage.utils.etc.ActivityUtil.goToNextActivity
import com.example.mystorage.utils.etc.DialogUtils
import com.example.mystorage.utils.custom.CustomToast
import com.example.mystorage.utils.listener.setOnSingleClickListener
import com.example.mystorage.utils.etc.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 메뉴 아이템 설정
        val homeNameChangeView = findViewById<TextView>(R.id.menu_item_change_home_name)
        val strReset = findViewById<TextView>(R.id.menu_item_str_reset)
        val deleteAllItem = findViewById<TextView>(R.id.menu_item_delete_all)
        val makeShoppingList = findViewById<TextView>(R.id.menu_item_make_shopping_list)

        binding.addItemFab.setOnSingleClickListener { onAddItemFabClicked() }
        homeNameChangeView.setOnSingleClickListener { onMenuItemChangeHomeNameClicked() }
        strReset.setOnSingleClickListener { onMenuItemStrResetClicked() }
        deleteAllItem.setOnSingleClickListener { onMenuItemDeleteAll() }
        makeShoppingList.setOnSingleClickListener { onMakeShoppingList() }

        setupViewPager()
        setupTabLayout()

        repeatOnStarted {
            viewModel.eventFlow.collect { mainEvent -> handleEvent(mainEvent) }
        }
    }

    private fun handleEvent(event: MainEvent) = when (event) {
        is MainEvent.Success -> CustomToast.showToast(this, event.message)
        is MainEvent.Error -> CustomToast.showToast(this, event.message)
        is MainEvent.SetInfo -> goToSetInfoFragment()
    }

    private fun goToSetInfoFragment() {
        goToNextActivity(this, InfoActivity())
    }

    private fun setupViewPager() {
        val pagerAdapter = MyPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = pagerAdapter
    }

    private fun setupTabLayout() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_baseline_warehouse_24)
        binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_baseline_format_list_bulleted_24)
        binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.box_unpacked_svgrepo_com)
    }

    private fun onAddItemFabClicked() {
        val itemAddFragment = ItemAddFragment()
        itemAddFragment.show(supportFragmentManager, "ItemAddFragment")
    }

    private fun onMenuItemChangeHomeNameClicked() {
        closeDrawer()
        val infoNameEditFragment = InfoNameEditFragment()
        infoNameEditFragment.show(supportFragmentManager, "InfoNameEditFragment")
    }

    private fun onMenuItemStrResetClicked() {
        DialogUtils.showNoMessageDialog(
            this,
            "구조 설정을 재설정합니다",
            "확인",
            "취소",
            onPositiveClick = {
                goToSetInfoFragment()
                closeDrawer()
            },
            onNegativeClick = {
            }
        )
    }

    private fun onMenuItemDeleteAll() {
        DialogUtils.showNoMessageDialog(
            this,
            "물건을 창고에서 모두 제거합니다",
            "확인",
            "취소",
            onPositiveClick = {
                // 창고에서 물건 제거
                viewModel.deleteAllItems()
                closeDrawer()
            },
            onNegativeClick = {
            }
        )
    }

    private fun onMakeShoppingList() {
        val shoppingListFragment = ShoppingListFragment()
        shoppingListFragment.show(supportFragmentManager, "ShoppingListFragment")
        closeDrawer()
    }

    fun showDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    fun restartItemListFragment() {
        val pagerAdapter = binding.viewPager.adapter as? MyPagerAdapter
        pagerAdapter?.let {
            val fragment = it.getItem(1) // 1 is the position of ItemListFragment
            supportFragmentManager.beginTransaction().apply {
                detach(fragment)
                attach(fragment)
                commit()
            }
        }
    }
}