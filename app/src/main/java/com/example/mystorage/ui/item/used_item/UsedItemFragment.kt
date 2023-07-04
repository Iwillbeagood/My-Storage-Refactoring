package com.example.mystorage.ui.item.used_item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.databinding.FragmentUsedItemBinding
import com.example.mystorage.ui.item.used_item.adapter.UsedItemAdapter
import com.example.mystorage.ui.item.used_item.UsedItemViewModel.*
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.etc.DialogUtils
import com.example.mystorage.utils.etc.GridSpacingItemDecoration
import com.example.mystorage.utils.etc.repeatOnStarted
import com.example.mystorage.utils.listener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsedItemFragment : Fragment(){

    lateinit var binding: FragmentUsedItemBinding
    private val viewModel: UsedItemViewModel by viewModels()
    private val usedItemAdapter = UsedItemAdapter { position ->
        viewModel.onUsedItemClick(
            position
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUsedItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.tag = "used_item_fragment_tag"

        viewModel.usedItemAdapter = usedItemAdapter

        setGridRecyclerView()

        binding.usedItemClearBtn.setOnSingleClickListener { onUsedItemClickBtnClicked() }

        repeatOnStarted {
            viewModel.eventFlow.collect { itemAddEvent -> handleEvent(itemAddEvent) }
        }

        viewModel.usedItems.observe(viewLifecycleOwner) { items ->
            binding.emptyHint.visibility =
                if (items.isNotEmpty()) View.GONE
                else View.VISIBLE

            usedItemAdapter.submitList(items)
        }
    }

    private fun handleEvent(event: UsedItemEvent) = when (event) {
        is UsedItemEvent.Success -> showToast(requireActivity(), event.message)
        is UsedItemEvent.Error -> showToast(requireActivity(), event.message)
        is UsedItemEvent.SendItem -> showItemOptionsDialog(event.item.item_ID)
    }

    private fun onUsedItemClickBtnClicked() {
        DialogUtils.showNoMessageDialog(
            requireContext(),
            "물건을 사용 완료에서 모두 제거합니다",
            "확인",
            "취소",
            onPositiveClick = {
                viewModel.onUsedItemDeleteAll()
            },
            onNegativeClick = {
            }
        )
    }

    private fun setGridRecyclerView() {
        val spanCount = 2 // 그리드 열 수
        val spacing = 10 // 아이템 간의 간격 (px)

        binding.usedItemRV.apply {
            val layoutManager = GridLayoutManager(context, spanCount).apply {
                orientation = RecyclerView.VERTICAL
            }
            this.layoutManager = layoutManager
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, true))
            adapter = usedItemAdapter
        }
    }

    private fun showItemOptionsDialog(itemID: Int) {
        val usedItemClickDialogFragment = UsedItemOptionsFragment()
        val bundle = Bundle()
        bundle.putInt("itemID", itemID)

        usedItemClickDialogFragment.arguments = bundle
        usedItemClickDialogFragment.show(parentFragmentManager, "UsedItemClickDialogFragment")
    }
}