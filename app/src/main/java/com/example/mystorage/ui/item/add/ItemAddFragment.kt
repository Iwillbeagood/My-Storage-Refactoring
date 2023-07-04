package com.example.mystorage.ui.item.add

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mystorage.R
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.databinding.FragmentItemAddBinding
import com.example.mystorage.ui.item.ImageOptionsFragment
import com.example.mystorage.ui.item.add.ItemAddViewModel.*
import com.example.mystorage.ui.item.list.ItemListFragment
import com.example.mystorage.ui.item.structure.ItemStrFragment
import com.example.mystorage.utils.etc.LoadInfoForSpinner.infoToList
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.etc.*
import com.example.mystorage.utils.listener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Timestamp

@AndroidEntryPoint
class ItemAddFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        DialogSetUtil.setDialogWindow(dialog!!)
    }

    private lateinit var binding: FragmentItemAddBinding
    private val viewModel: ItemAddViewModel by viewModels()
    var items = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_add, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addItemImageLayout.setOnSingleClickListener {
            ImageOptionsFragment().show(parentFragmentManager.beginTransaction(), "ImageOptionsFragment")
        }
        binding.addPlusBtn.setOnSingleClickListener { onCountBtnClicked(1) }
        binding.addMinusBtn.setOnSingleClickListener { onCountBtnClicked(-1) }
        binding.addItemBtn.setOnSingleClickListener { onItemAddButtonClicked() }
        binding.addBackBtn.setOnSingleClickListener { onDismiss(dialog!!) }

        FocusChangeListener.setEditTextFocusChangeListener(binding.addItemNameEdit, binding.addItemNameLayout)
        FocusChangeListener.setEditTextFocusChangeListener(binding.addItemStoreEdit, binding.addItemStoreLayout)

        repeatOnStarted {
            viewModel.eventFlow.collect { itemAddEvent -> handleEvent(itemAddEvent) }
        }
    }

    private fun handleEvent(event: ItemAddEvent) = when (event) {
        is ItemAddEvent.Success -> {
            showToast(requireActivity(), event.message)
            onDismiss(dialog!!)
        }
        is ItemAddEvent.Error -> showToast(requireActivity(), event.message)
        is ItemAddEvent.GetInfo -> onInfoSetting(event.infoEntity)
        is ItemAddEvent.ItemImageAdd -> binding.addItemImageView.setImageBitmap(event.itemImage)
        is ItemAddEvent.ShowConfirmationDialog -> showConfirmationDialog(event.showConfirmation)
    }

    private fun showConfirmationDialog(showConfirmation: Boolean) {
        if (showConfirmation) {
            DialogUtils.showDialog(
                requireActivity(),
                "동일한 이름의 아이템이 존재합니다. 해당하는 아이템에 개수를 추가하시겠습니까?",
                "확인",
                "취소",
                onPositiveClick = {
                    viewModel.onConfirmationDialogConfirmed(getItemEntity())
                },
                onNegativeClick = {
                }
            )
        }
    }

    private fun getItemEntity(): ItemEntity = with(binding) {
        ItemEntity(
            0,
            item_name = addItemNameEdit.text.toString(),
            item_image = addItemImageView.drawable.toBitmap(),
            item_place = addItemPlaceSpinner.selectedItem.toString(),
            item_store = addItemStoreEdit.text.toString(),
            item_count = addItemCountEdit.text.toString().toInt(),
            item_state = ItemState.NOT_USED,
            created_at = Timestamp(System.currentTimeMillis()),
            updated_at = Timestamp(System.currentTimeMillis()),
            isSelected = false
        )
    }

    private fun onCountBtnClicked(i : Int) {
        val currentValue = binding.addItemCountEdit.text.toString().toIntOrNull() ?: 0
        val afterValue = currentValue + i
        if (afterValue > 0)
            binding.addItemCountEdit.setText(afterValue.toString())
    }

    private fun onItemAddButtonClicked() {
        viewModel.onItemAdd(getItemEntity())
    }

    private fun onInfoSetting(infoEntity: InfoEntity) {
        val spinner = binding.addItemPlaceSpinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        items.addAll(infoToList(infoEntity))
        adapter.notifyDataSetChanged()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val itemListFragment = parentFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":1") as ItemListFragment?
        itemListFragment?.onResume()
        val itemStrFragment = parentFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":0") as ItemStrFragment?
        itemStrFragment?.onResume()
    }
}