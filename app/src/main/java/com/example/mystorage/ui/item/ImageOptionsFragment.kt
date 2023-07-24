package com.example.mystorage.ui.item

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.mystorage.R
import com.example.mystorage.databinding.FragmentImageOptionsBinding
import com.example.mystorage.utils.converter.BitmapSizeConverter
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.image.ConvertUriToBitmap.uriToBitmap
import com.example.mystorage.utils.listener.ImageSelectionListener
import com.example.mystorage.utils.listener.setOnSingleClickListener

class ImageOptionsFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 500)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
    }

    private lateinit var binding: FragmentImageOptionsBinding

    private var photoUri: Uri? = null

    private var imageSelectionListener: ImageSelectionListener? = null

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var cameraUriCallback: ((Uri?, String) -> Unit)? = null
    private var galleryUriCallback: ((Uri?, String) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap?
                setBitmapImage(bitmap)
            } else {
                showToast(requireActivity(), "사진 촬영에 실패했습니다")
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                setUriImage(uri)
            } else {
                showToast(requireActivity(), "갤러리에서 사진를 가져오는데 실패했습니다")
            }
        }

        binding.cameraLayout.setOnSingleClickListener {
            openCamera()
        }

        binding.galleryLayout.setOnSingleClickListener {
            openGallery()
        }
    }

    private fun setBitmapImage(bitmap: Bitmap?) {
        if (bitmap != null) {
            val scaledBitmap = BitmapSizeConverter.convertBitmapSize(bitmap)
            imageSelectionListener?.onImageSelected(scaledBitmap)
        } else {
            showToast(requireActivity(), "사진 촬영에 실패했습니다")
        }
    }

    private fun setUriImage(uri: Uri) {
        val bitmap = uriToBitmap(uri, requireContext())
        val scaledBitmap = BitmapSizeConverter.convertBitmapSize(bitmap!!)
        imageSelectionListener?.onImageSelected(scaledBitmap)
        dismiss()
    }

    private fun openCamera() {
        cameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun openGallery() {
        galleryPermission.launch(arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        } else {
            cameraUriCallback?.invoke(null, "카메라 권한이 필요합니다")
        }
    }

    private val galleryPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        result.forEach {
            if (it.value)
                galleryUriCallback?.invoke(null, "저장소 접근 권한이 필요합니다")
        }

        galleryLauncher.launch("image/*")
    }

    fun setImageSelectionListener(listener: ImageSelectionListener) {
        imageSelectionListener = listener
    }
}
