package com.example.mystorage.ui.info

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystorage.R
import com.example.mystorage.databinding.FragmentInfo3Binding
import com.example.mystorage.ui.info.InfoViewModel.*
import com.example.mystorage.ui.info.adapter.InfoNameAdapter
import com.example.mystorage.ui.main.MainActivity
import com.example.mystorage.utils.etc.ActivityUtil.goToNextActivity
import com.example.mystorage.utils.etc.Constants.TAG
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.listener.setOnSingleClickListener
import com.example.mystorage.utils.etc.repeatOnStarted
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.log

@AndroidEntryPoint
class InfoFragment3 : Fragment() {
    private lateinit var binding: FragmentInfo3Binding

    lateinit var viewModel: InfoViewModel
    private val roomNameAdapter = InfoNameAdapter()
    private val bathNameAdapter = InfoNameAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_info3, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.roomNameAdapter = roomNameAdapter
        viewModel.bathNameAdapter = bathNameAdapter

        binding.roomNamesRv.apply {
            this.layoutManager = LinearLayoutManager(context)
            adapter = roomNameAdapter
        }

        binding.bathNamesRv.apply {
            this.layoutManager = LinearLayoutManager(context)
            adapter = bathNameAdapter
        }

        binding.etcAddBtn.setOnSingleClickListener { onEtcAddBtnClicked() }
        binding.saveInfoBtn.setOnSingleClickListener { onSaveInfoBtnClicked() }

        repeatOnStarted {
            viewModel.eventFlow.collect { infoEvent -> handleEvent(infoEvent) }
        }
    }

    private fun handleEvent(event: InfoEvent) = when (event) {
        is InfoEvent.Success -> {
            showToast(requireActivity(), event.message)
            goToNextActivity(requireActivity(), MainActivity())
        }
        is InfoEvent.Error -> showToast(requireActivity(), event.message)
        is InfoEvent.GetRoomNames -> roomNameAdapter.submitList(event.roomNames)
        is InfoEvent.GetBathNames -> bathNameAdapter.submitList(event.bathNames)
    }

    private fun onEtcAddBtnClicked() {
        val string = binding.etcEdit.text
        if (string.isNullOrEmpty()) {
            return
        } else {
            binding.etcChipGroup.addView(Chip(requireContext()).apply {
                text = string
                isCloseIconVisible = true
                setOnCloseIconClickListener { binding.etcChipGroup.removeView(this) }
            })
        }
        binding.etcEdit.setText("")
    }

    private fun onSaveInfoBtnClicked() =
        viewModel.onInsertInfo(getJsonStringFromChipGroup(binding.etcChipGroup))


    private fun getJsonStringFromChipGroup(chipGroup: ChipGroup): String {
        val jsonArray = JSONArray()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            jsonArray.put(chip.text.toString())
        }

        return JSONObject().put("etc_name", jsonArray).toString()
    }
}