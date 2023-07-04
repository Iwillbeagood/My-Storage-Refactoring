package com.example.mystorage.ui.item.update

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mystorage.R
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.databinding.FragmentItemUpdateBinding
import com.example.mystorage.ui.item.list.ItemListFragment
import com.example.mystorage.ui.item.update.ItemUpdateViewModel.*
import com.example.mystorage.ui.main.MainActivity
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.converter.BitmapSizeConverter
import com.example.mystorage.utils.etc.*
import com.example.mystorage.utils.image.DecodeFileUtil
import com.example.mystorage.utils.image.ImageLoader.loadBitmap
import com.example.mystorage.utils.image.ImageLoader.loadUrl
import com.example.mystorage.utils.listener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.sql.Timestamp

@AndroidEntryPoint
class ItemUpdateFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        DialogSetUtil.setDialogWindow(dialog!!)
    }

    private lateinit var binding: FragmentItemUpdateBinding
    private val viewModel: ItemUpdateViewModel by viewModels()
    private lateinit var imageOptionsDialog: AlertDialog
    var items = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_update, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateItemImageLayout.setOnSingleClickListener { showImageOptionsDialog() }
        binding.updatePlusBtn.setOnSingleClickListener { onCountBtnClicked(1) }
        binding.updateMinusBtn.setOnSingleClickListener { onCountBtnClicked(-1) }
        binding.updateItemBtn.setOnSingleClickListener { onItemUpdateBtnClicked() }
        binding.updateItemBack.setOnSingleClickListener { onDismiss(dialog!!) }

        FocusChangeListener.setEditTextFocusChangeListener(binding.updateItemNameEdit, binding.updateItemNameLayout)
        FocusChangeListener.setEditTextFocusChangeListener(binding.updateItemStoreEdit, binding.updateItemStoreLayout)

        val bundle = arguments
        val itemID = bundle?.getInt("itemID")!!.toInt()

        viewModel.getItem(itemID)

        repeatOnStarted {
            viewModel.eventFlow.collect { itemAddEvent -> handleEvent(itemAddEvent) }
        }
    }

    private fun handleEvent(event: ItemUpdateEvent) = when (event) {
        is ItemUpdateEvent.Success -> {
            showToast(requireActivity(), event.message)
            onDismiss(dialog!!)
            (activity as? MainActivity)?.restartItemListFragment()
        }
        is ItemUpdateEvent.Error -> showToast(requireActivity(), event.message)
        is ItemUpdateEvent.GetInfo -> onInfoSetting(event.infoEntity)
        is ItemUpdateEvent.GetItem -> setUpdateSetting(event.item)
    }

    private fun onInfoSetting(infoEntity: InfoEntity) {
        val spinner = binding.updateItemPlaceSpinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        items.addAll(LoadInfoForSpinner.infoToList(infoEntity))
        adapter.notifyDataSetChanged()
    }

    private fun setUpdateSetting(item: ItemEntity) {
        binding.updateItemNameEdit.setText(item.item_name)
        binding.updateItemStoreEdit.setText(item.item_store)
        binding.updateItemCountEdit.setText(item.item_count)

        val index = items.indexOf(item.item_place.toString())
        binding.updateItemPlaceSpinner.setSelection(index)
        binding.updateItemImageview.loadBitmap(item.item_image!!)
    }

    fun updateImageBitmap(imageBitmap: Bitmap) {
        BitmapSizeConverter.convertBitmapSize(imageBitmap)
        binding.updateItemImageview.loadBitmap(imageBitmap)
    }

    fun updateImageURL(imageUri: Uri) {
        binding.updateItemImageview.loadUrl(imageUri.toString())

        val file = File(imageUri.toString())
        val bitmap: Bitmap? = DecodeFileUtil.decodeFileWithOrientation(file)

        if (bitmap != null) {
            BitmapSizeConverter.convertBitmapSize(bitmap)
        }
    }

    private fun getItemEntity(): ItemEntity  = with(binding) {
        ItemEntity(
            item_ID = 0,
            item_name = updateItemNameEdit.text.toString(),
            item_image = updateItemImageview.drawable.toBitmap(),
            item_place = updateItemPlaceSpinner.selectedItem.toString(),
            item_store = updateItemStoreEdit.text.toString(),
            item_count = updateItemCountEdit.text.toString().toInt(),
            item_state = ItemState.NOT_USED,
            created_at = Timestamp(System.currentTimeMillis()),
            updated_at = Timestamp(System.currentTimeMillis()),
            isSelected = false
        )
    }

    private fun showImageOptionsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.fragment_image_options)
        imageOptionsDialog = builder.create()
        imageOptionsDialog.show()
    }

    private fun onCountBtnClicked(i : Int) {
        val currentValue = binding.updateItemCountEdit.text.toString().toIntOrNull() ?: 0
        val afterValue = currentValue + i
        if (afterValue > 0)
            binding.updateItemCountEdit.setText(afterValue.toString())
    }

    private fun onItemUpdateBtnClicked() {
        viewModel.onItemUpdate(getItemEntity())
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val itemListFragment = parentFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":1") as ItemListFragment?
        itemListFragment?.onResume() // itemListFragment 인스턴스를 가져와서 getResponseOnItemLoad 메소드 호출
    }
}