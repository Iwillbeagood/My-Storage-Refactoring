package com.example.mystorage.ui.item.used_item

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mystorage.R
import com.example.mystorage.databinding.FragmentUsedItemOptionsBinding
import com.example.mystorage.ui.item.used_item.UsedItemOptionsViewModel.UsedOptionEvent
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.listener.setOnSingleClickListener
import com.example.mystorage.utils.etc.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsedItemOptionsFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private val viewModel: UsedItemOptionsViewModel by viewModels()
    private lateinit var binding: FragmentUsedItemOptionsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsedItemOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val itemID = bundle?.getInt("itemID")!!.toInt()

        binding.usedItemReturn.setOnSingleClickListener { viewModel.onUsedItemReturn(itemID) }
        binding.usedItemDelete.setOnSingleClickListener { viewModel.onItemDelete(itemID) }

        repeatOnStarted {
            viewModel.eventFlow.collect { usedOptionEvent -> handleEvent(usedOptionEvent) }
        }
    }

    private fun handleEvent(event: UsedOptionEvent) = when (event) {
        is UsedOptionEvent.Success -> {
            dismiss()
            showToast(requireActivity(), event.message)
        }
        is UsedOptionEvent.Error -> showToast(requireActivity(), event.message)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val usedItemFragment = parentFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":2") as UsedItemFragment?
        usedItemFragment?.onResume() // itemListFragment 인스턴스를 가져와서 getResponseOnItemLoad 메소드 호출
    }
}