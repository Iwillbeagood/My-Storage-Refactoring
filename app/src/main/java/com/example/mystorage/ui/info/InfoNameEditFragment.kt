package com.example.mystorage.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystorage.databinding.FragmentInfoNameEditBinding
import com.example.mystorage.ui.info.InfoNameEditViewModel.*
import com.example.mystorage.ui.info.adapter.InfoNameEditAdapter
import com.example.mystorage.utils.etc.DialogSetUtil.setDialogWindow
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.listener.setOnSingleClickListener
import com.example.mystorage.utils.etc.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoNameEditFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        setDialogWindow(dialog!!)
    }

    private lateinit var binding : FragmentInfoNameEditBinding
    private val infoNameEditAdapter = InfoNameEditAdapter()
    private val viewModel: InfoNameEditViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInfoNameEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.infoNameEditAdapter = infoNameEditAdapter

        binding.infoNameEditBack.setOnSingleClickListener { dismiss() }
        binding.infoNameEditBtn.setOnSingleClickListener { viewModel.onInsertInfo() }

        binding.infoNameEditRv.apply {
            this.layoutManager = LinearLayoutManager(context)
            adapter = infoNameEditAdapter
        }

        repeatOnStarted {
            viewModel.eventFlow.collect { itemAddEvent -> handleEvent(itemAddEvent) }
        }
    }

    private fun handleEvent(event: InfoNameEditEvent) = when (event) {
        is InfoNameEditEvent.Success -> {
            showToast(requireActivity(), event.message)
            dismiss()
        }
        is InfoNameEditEvent.Error -> showToast(requireActivity(), event.message)
        is InfoNameEditEvent.GetInfoNameList -> infoNameEditAdapter.submitList(event.infoNameList)
    }
}