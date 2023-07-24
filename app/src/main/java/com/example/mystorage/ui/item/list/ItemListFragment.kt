package com.example.mystorage.ui.item.list

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.databinding.FragmentItemListBinding
import com.example.mystorage.ui.item.list.adapter.ItemListAdapter
import com.example.mystorage.ui.item.list.ItemListViewModel.*
import com.example.mystorage.ui.main.MainActivity
import com.example.mystorage.utils.etc.TextWatcherUtil.createTextWatcher
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.etc.*
import com.example.mystorage.utils.listener.ItemClickListener
import com.example.mystorage.utils.listener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemListFragment : Fragment() {

    private lateinit var binding: FragmentItemListBinding
    private val viewModel: ItemListViewModel by viewModels()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.longClickModeLayout.visibility == View.VISIBLE) {
                changeLongClickMode(false)
                viewModel.exitLongMode()
            }
        }
    }

    private val itemListAdapter = ItemListAdapter(
        object : ItemClickListener {
            override fun onItemClick(position: Int, state: Boolean) {
                viewModel.onItemClick(position, state)
            }

            override fun onItemLongClick(position: Int, state: Boolean) {
                viewModel.onItemLongClick(position, state)
            }
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        setGridRecyclerView()

        viewModel.listAdapter = itemListAdapter

        binding.listMenuBtn.setOnSingleClickListener { (activity as? MainActivity)?.showDrawer() }
        binding.moveSelectedItemBtn.setOnSingleClickListener { onMoveSelectedItemClicked() }
        binding.deleteSelectedItemBtn.setOnSingleClickListener { onDeleteSelectedItemClicked() }
        binding.exitLongClickModeBtn.setOnSingleClickListener {
            changeLongClickMode(false)
            viewModel.exitLongMode()
        }

        // 검색어로 아이템 찾기
        binding.listSearchEdit.addTextChangedListener(createTextWatcher { searchText ->
            viewModel.filterItemList(searchText)
        })

        // 전체를 선택하면 아이템 전체 선택
        binding.selectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAllItemsSelectedState(isChecked)
        }

        repeatOnStarted {
            viewModel.eventFlow.collect { itemAddEvent -> handleEvent(itemAddEvent) }
        }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            binding.emptyHint.visibility =
                if (!items.isNullOrEmpty()) View.GONE
                else View.VISIBLE

            itemListAdapter.submitList(items)
        }
    }

    private fun handleEvent(event: ItemListEvent) = when (event) {
        is ItemListEvent.Success -> {
            showToast(requireActivity(), event.message)
            changeLongClickMode(false)
        }
        is ItemListEvent.Error -> showToast(requireActivity(), event.message)
        is ItemListEvent.SendItem -> showItemOptionsDialog(event.item.item_ID)
        is ItemListEvent.GetInfo -> onInfoSetting(event.info)
        is ItemListEvent.LongClickMode -> changeLongClickMode(event.state)
    }

    private fun onInfoSetting(infoEntity: InfoEntity) {
        val items = mutableListOf<String>()
        val spinner = binding.placeSpinner
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        items.addAll(LoadInfoForSpinner.infoToList(infoEntity))
        adapter.notifyDataSetChanged()
    }

    private fun changeLongClickMode(state: Boolean) = when(state) {
        true -> {
            VibrationUtils.vibrate(requireContext(), 20L)
            binding.longClickModeLayout.visibility = View.VISIBLE
        }
        false -> {
            binding.longClickModeLayout.visibility = View.GONE
        }
    }

    private fun showItemOptionsDialog(itemID: Int) {
        val itemOptionsFragment = ItemOptionsFragment()
        val bundle = Bundle()
        bundle.putInt("itemID", itemID)

        itemOptionsFragment.arguments = bundle
        itemOptionsFragment.show(parentFragmentManager, "ItemOptionsFragment")
    }

    private fun setGridRecyclerView() {
        val spanCount = 2 // 그리드 열 수
        val spacing = 16 // 아이템 간의 간격 (px)

        // 그리드 레이아웃 매니저 설정
        binding.listItemRV.apply {
            this.layoutManager = GridLayoutManager(context, spanCount).apply { orientation = RecyclerView.VERTICAL }
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, true))
            adapter = itemListAdapter
        }
    }

    private fun onMoveSelectedItemClicked() {
        val updatePlace = binding.placeSpinner.selectedItem.toString()
        DialogUtils.showDialog(
            requireActivity(),
            "${updatePlace}으로 선택된 물건을 이동합니다",
            "확인",
            "취소",
            onPositiveClick = {
                viewModel.onUpdateSelectedItemPlace(updatePlace)
            },
            onNegativeClick = {
            }
        )
    }

    private fun onDeleteSelectedItemClicked() {
        DialogUtils.showDialog(
            requireActivity(),
            "선택된 물건을 삭제합니다",
            "확인",
            "취소",
            onPositiveClick = {
                viewModel.onSelectedItemDelete()
            },
            onNegativeClick = {
            }
        )
    }
}
