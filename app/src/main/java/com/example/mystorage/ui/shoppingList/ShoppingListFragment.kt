package com.example.mystorage.ui.shoppingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystorage.R
import com.example.mystorage.databinding.FragmentShoppingListBinding
import com.example.mystorage.ui.shoppingList.ShoppingListViewModel.ShoppingListEvent
import com.example.mystorage.ui.shoppingList.adapter.ShoppingListAdapter
import com.example.mystorage.utils.custom.CustomToast
import com.example.mystorage.utils.etc.CustomDecoration
import com.example.mystorage.utils.etc.DialogSetUtil
import com.example.mystorage.utils.etc.repeatOnStarted
import com.example.mystorage.utils.listener.ShoppingListClickListener
import com.example.mystorage.utils.listener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingListFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        DialogSetUtil.setDialogWindow(dialog!!)
    }

    lateinit var binding: FragmentShoppingListBinding
    private val viewModel: ShoppingListViewModel by viewModels()
    private val shoppingListAdapter = ShoppingListAdapter { position, selected ->
        viewModel.onShoppingListClicked(position, selected)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.shoppingListAdapter = shoppingListAdapter

        binding.shoppingListBackBtn.setOnSingleClickListener { dismiss() }
        binding.shoppingCompleteBtn.setOnSingleClickListener { viewModel.onShoppingCompleted() }

        setRV()

        repeatOnStarted {
            viewModel.eventFlow.collect { itemAddEvent -> handleEvent(itemAddEvent) }
        }

        viewModel.usedItems.observe(viewLifecycleOwner) { items ->
            val visibility = if (items.isNotEmpty()) View.GONE else View.VISIBLE

            binding.shoppingListEmptyHint.visibility = visibility
            binding.shoppingListEmptyImg.visibility = visibility

            shoppingListAdapter.submitList(items)
        }
    }

    private fun setRV() {
        val dividerColor = ContextCompat.getColor(requireContext(), R.color.line)
        val customDecoration = CustomDecoration(2f, 20f, dividerColor)

        binding.shoppingListRV.apply {
            this.layoutManager = LinearLayoutManager(context)
            adapter = shoppingListAdapter
            addItemDecoration(customDecoration)
        }
    }

    private fun handleEvent(event: ShoppingListEvent) = when (event) {
        is ShoppingListEvent.Success -> {
            CustomToast.showToast(requireActivity(), event.message)
            dismiss()
        }
        is ShoppingListEvent.Error -> CustomToast.showToast(requireActivity(), event.message)
    }
}