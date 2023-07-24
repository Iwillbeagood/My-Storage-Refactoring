package com.example.mystorage.ui.info.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.data.entity.InfoName
import com.example.mystorage.databinding.RecyclerInfoBinding
import com.example.mystorage.utils.diffcallbacks.InfoNameDiffCallbacks


class InfoNameAdapter(
) : ListAdapter<InfoName, InfoNameAdapter.ViewHolder>(InfoNameDiffCallbacks()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: RecyclerInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: InfoName) = with(binding) {
            this.info = info
            executePendingBindings()
        }
    }
}