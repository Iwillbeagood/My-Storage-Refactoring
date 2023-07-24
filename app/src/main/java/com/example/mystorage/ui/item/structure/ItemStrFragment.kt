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
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.entity.ItemGroup
import com.example.mystorage.databinding.FragmentItemStrBinding
import com.example.mystorage.ui.item.add.ItemAddViewModel
import com.example.mystorage.ui.item.structure.ItemStrViewModel.*
import com.example.mystorage.ui.item.structure.adapter.ItemStrAdapter
import com.example.mystorage.ui.main.MainActivity
import com.example.mystorage.utils.custom.CustomToast
import com.example.mystorage.utils.etc.LoadInfoForSpinner.infoToList
import com.example.mystorage.utils.etc.repeatOnStarted
import com.example.mystorage.utils.listener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemStrFragment : Fragment() {

    private lateinit var binding: FragmentItemStrBinding
    private val viewModel: ItemStrViewModel by viewModels()
    private val itemStrAdapter = ItemStrAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentItemStrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.itemStrAdapter = itemStrAdapter

        setRecyclerView()

        binding.strMenuBtn.setOnSingleClickListener { (activity as? MainActivity)?.showDrawer() }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            binding.strHint.visibility =
                if (!items.isNullOrEmpty()) View.GONE
                else View.VISIBLE

            itemStrAdapter.submitList(groupingItems(items))
        }

        repeatOnStarted {
            viewModel.eventFlow.collect { itemStrEvent -> handleEvent(itemStrEvent) }
        }
    }

    private fun handleEvent(event: ItemStrEvent) = when (event) {
        is ItemStrEvent.GetInfo -> setSearchSpinner(event.infoEntity)
    }

    private fun groupingItems(items: List<ItemEntity>): List<ItemGroup> =
        items.groupBy { it.item_place }
            .map { (place, items) ->
                ItemGroup(place.toString(), items)
            }

    private fun setRecyclerView() = with(binding) {
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context)
            adapter = itemStrAdapter
        }
    }


    private fun setSearchSpinner(infoEntity: InfoEntity) {
        val placeList = mutableListOf<String>()
        val spinner = binding.strPlaceSpinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, placeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.filterItemStrList(placeList.toList()[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        placeList.add("전체")
        placeList.addAll(infoToList(infoEntity))
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getInfoEntity()
    }
}
