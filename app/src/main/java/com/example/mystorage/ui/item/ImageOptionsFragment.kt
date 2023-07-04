package com.example.mystorage.ui.item

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mystorage.R
import com.example.mystorage.databinding.FragmentImageOptionsBinding
import com.example.mystorage.ui.item.add.ItemAddViewModel
import com.example.mystorage.utils.converter.BitmapSizeConverter
import com.example.mystorage.utils.custom.CustomToast
import com.example.mystorage.utils.custom.CustomToast.showToast
import com.example.mystorage.utils.image.ConvertUriToBitmap
import com.example.mystorage.utils.image.ConvertUriToBitmap.uriToBitmap
import com.example.mystorage.utils.image.DecodeFileUtil
import com.example.mystorage.utils.listener.setOnSingleClickListener
import java.io.File

class ImageOptionsFragment : DialogFragment() {

    private lateinit var binding: FragmentImageOptionsBinding
    private val viewModel: ItemAddViewModel by viewModels()

    private var photoUri: Uri? = null

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var cameraUriCallback: ((Uri?, String) -> Unit)? = null
    private var galleryUriCallback: ((Uri?, String) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageOptionsBinding.inflate(inflater, container, false)


        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                cameraUriCallback?.invoke(photoUri, "")
            } else {
                cameraUriCallback?.invoke(null, "사진 촬영에 실패했습니다")
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                galleryUriCallback?.invoke(uri, "")
            } else {
                galleryUriCallback?.invoke(null, "갤러리에서 사진를 가져오는데 실패했습니다")
            }
        }

        binding.cameraLayout.setOnSingleClickListener {
            setCameraUriCallback { uri: Uri?, message: String ->
                if (uri != null) {
                    val bitmap = uriToBitmap(uri, requireContext())
                    val scaledBitmap = BitmapSizeConverter.convertBitmapSize(bitmap!!)
                    viewModel.onItemImageAdd(scaledBitmap)
                    dismiss()
                } else {
                    showToast(requireActivity(), message)
                }
            }
            openCamera()
        }

        binding.galleryLayout.setOnSingleClickListener {
            setGalleryUriCallback { uri: Uri?, message: String ->
                if (uri != null) {
                    val bitmap = uriToBitmap(uri, requireContext())
                    val scaledBitmap = BitmapSizeConverter.convertBitmapSize(bitmap!!)
                    viewModel.onItemImageAdd(scaledBitmap)
                    dismiss()
                } else {
                    showToast(requireActivity(), message)
                }
            }
            openGallery()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 500)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
    }

    private fun openCamera() {
        cameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun openGallery() {
        galleryPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val photoFile = File.createTempFile("IMG_", ".jpg", activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
            photoUri = FileProvider.getUriForFile(requireActivity(), "${activity?.packageName}.provider", photoFile)
            cameraLauncher.launch(photoUri)
        } else {
            cameraUriCallback?.invoke(null, "카메라 권한이 필요합니다")
        }
    }

    private val galleryPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            galleryUriCallback?.invoke(null, "저장소 접근 권한이 필요합니다")
        }
    }

    private fun setCameraUriCallback(callback: (Uri?, String) -> Unit) {
        cameraUriCallback = callback
    }

    private fun setGalleryUriCallback(callback: (Uri?, String) -> Unit) {
        galleryUriCallback = callback
    }
}
