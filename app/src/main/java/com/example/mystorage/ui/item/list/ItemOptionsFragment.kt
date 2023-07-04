package com.example.mystorage.ui.item.list

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mystorage.R
import com.example.mystorage.ui.item.update.ItemUpdateFragment
import com.example.mystorage.databinding.FragmentItemOptionsBinding
import com.example.mystorage.ui.item.list.ItemOptionsViewModel.*
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.listener.setOnSingleClickListener
import com.example.mystorage.utils.etc.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemOptionsFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private val viewModel: ItemOptionsViewModel by viewModels()
    lateinit var binding: FragmentItemOptionsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentItemOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val itemID = bundle?.getInt("itemID")!!.toInt()

        binding.itemUsed.setOnSingleClickListener { viewModel.onItemUsed(itemID) }
        binding.itemSingleUse.setOnSingleClickListener { viewModel.onItemSingleUse(itemID) }
        binding.deleteItem.setOnSingleClickListener { viewModel.onItemDelete(itemID) }
        binding.itemEdit.setOnSingleClickListener { showItemEditDialog(itemID) }

        repeatOnStarted {
            viewModel.eventFlow.collect { optionEvent -> handleEvent(optionEvent) }
        }
    }

    private fun handleEvent(event: OptionEvent) = when (event) {
        is OptionEvent.Success -> {
            showToast(requireActivity(), event.message)
            dismiss()
        }
        is OptionEvent.Error -> showToast(requireActivity(), event.message)
    }

    private fun showItemEditDialog(itemID: Int) {
        val itemUpdateFragment = ItemUpdateFragment()
        val bundle = Bundle()
        bundle.putInt("itemID", itemID)

        itemUpdateFragment.arguments = bundle
        itemUpdateFragment.show(parentFragmentManager, "ItemUpdateFragment")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val itemListFragment = parentFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":1") as ItemListFragment?
        itemListFragment?.onResume() // itemListFragment 인스턴스를 가져와서 getResponseOnItemLoad 메소드 호출
    }
}