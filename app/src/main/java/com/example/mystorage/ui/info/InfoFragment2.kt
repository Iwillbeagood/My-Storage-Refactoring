package com.example.mystorage.ui.info

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.mystorage.R
import com.example.mystorage.databinding.FragmentInfo2Binding
import com.example.mystorage.utils.etc.Constants.TAG
import com.example.mystorage.utils.custom.CustomToast
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoFragment2 : Fragment() {
    private lateinit var binding : FragmentInfo2Binding

    lateinit var viewModel: InfoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info2, container, false)
        return binding.root
    }

    fun getSelectedChipValue() {
        Log.d(TAG, "InfoFragment2 - getSelectedChipValue() called")
        val chipGroupList = listOf(
            binding.livingRoomGroup,
            binding.kitchenGroup,
            binding.storageGroup,
            binding.roomGroup,
            binding.bathGroup
        )

        val selectedValueList = mutableListOf<String>()

        for (chipGroup in chipGroupList) {
            val selectedId = chipGroup.checkedChipId
            val selectedChip = chipGroup.findViewById<Chip>(selectedId)
            if (selectedId == View.NO_ID) {
                onChipSelectedError(findChipGroupType(chipGroup.id))
                return
            }
            val selectedText = selectedChip?.text?.toString() ?: "0"

            selectedValueList.add(selectedText)
        }

        viewModel.currentInfoUpdate(
            selectedValueList[0] == "있음",
            selectedValueList[1] == "있음",
            selectedValueList[2] == "있음"
        )

        viewModel.setRoomNames(selectedValueList[3].toInt())
        viewModel.setBathNames(selectedValueList[4].toInt())
    }

    private fun findChipGroupType(chipGroup: Int): String {
        return when(chipGroup) {
            R.id.room_group -> "방 개수를 입력해 주세요"
            R.id.bath_group -> "화장실 개수를 입력해 주세요"
            R.id.living_room_group -> "거실 유/무를 입력해 주세요"
            R.id.kitchen_group -> "주방 유/무를 입력해 주세요"
            R.id.storage_group -> "창고 유/무를 입력해 주세요"
            else -> ""
        }
    }

    private fun onChipSelectedError(message: String?) {
        CustomToast.createToast(requireActivity(), message.toString()).show()
    }
}