package com.example.mystorage.ui.item.structure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.entity.ItemGroup
import com.example.mystorage.databinding.FragmentItemStrBinding
import com.example.mystorage.ui.item.structure.ItemStrViewModel.ItemStrEvent
import com.example.mystorage.ui.item.structure.adapter.ItemStrAdapter
import com.example.mystorage.ui.main.MainActivity
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.listener.setOnSingleClickListener
import com.example.mystorage.utils.etc.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemStrFragment : Fragment() {

    private lateinit var binding: FragmentItemStrBinding
    private val viewModel: ItemStrViewModel by viewModels()
    private var placeList = mutableSetOf<String>()
    private val itemStrAdapter = ItemStrAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentItemStrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.tag = "list_str_fragment_tag"

        viewModel.itemStrAdapter = itemStrAdapter

        setRecyclerView()

        binding.strMenuBtn.setOnSingleClickListener { (activity as? MainActivity)?.showDrawer() }

        repeatOnStarted {
            viewModel.eventFlow.collect { itemAddEvent -> handleEvent(itemAddEvent) }
        }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            binding.strHint.visibility =
                if (!items.isNullOrEmpty()) View.GONE
                else View.VISIBLE

            setSearchSpinner()
            itemStrAdapter.submitList(groupingItems(items))
        }
    }

    private fun groupingItems(items: List<ItemEntity>): List<ItemGroup> =
        items.groupBy { it.item_place }
            .map { (place, items) ->
                ItemGroup(place.toString(), items)
            }

    private fun handleEvent(event: ItemStrEvent) = when (event) {
        is ItemStrEvent.Success -> showToast(requireActivity(), event.message)
        is ItemStrEvent.Error -> showToast(requireActivity(), event.message)
      }

    private fun setRecyclerView() = with(binding) {
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context)
            adapter = itemStrAdapter
        }
    }

    private fun setSearchSpinner() {
        val spinner = binding.placeSpinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, placeList.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.filterItemStrList(placeList.toList()[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
