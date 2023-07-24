package com.example.mystorage.ui.info

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.mystorage.R
import com.example.mystorage.databinding.FragmentInfo1Binding

class InfoFragment1 : Fragment() {
    private lateinit var binding: FragmentInfo1Binding
    private val handler = Handler()

    private var appContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInfo1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler.postDelayed({
            startAnimation(binding.textAnim1)
        }, 0)

        handler.postDelayed({
            startAnimation(binding.textAnim2)
        }, 1500)

        handler.postDelayed({
            startAnimation(binding.textAnim3)
        }, 3000)

        handler.postDelayed({
            startAnimation(binding.textAnim4)
        }, 4500)
    }

    private fun startAnimation(textView: TextView) {
        appContext?.let { context ->
            val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            textView.startAnimation(anim)
            textView.visibility = TextView.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()
        appContext = null
    }
}