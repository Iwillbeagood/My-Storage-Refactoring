package com.example.mystorage.utils.image

import android.Manifest
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File


object CameraGalleryManager {
    private lateinit var cameraPermission: ActivityResultLauncher<String>
    private lateinit var galleryPermission: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var cameraUriCallback: ((Uri?, String) -> Unit)? = null
    private var galleryUriCallback: ((Uri?, String) -> Unit)? = null

    private var photoUri : Uri? = null

    fun openCamera(activity: AppCompatActivity) {
        cameraPermission = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val photoFile = File.createTempFile("IMG_", ".jpg", activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                photoUri = FileProvider.getUriForFile(activity, "${activity.packageName}.provider", photoFile)
                cameraLauncher = activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                    if (isSuccess) {
                        cameraUriCallback?.invoke(photoUri, "")
                    } else {
                        cameraUriCallback?.invoke(null, "Failed to take picture")
                    }
                }
                cameraLauncher.launch(photoUri)
            } else {
                cameraUriCallback?.invoke(null, "Camera permission must be approved")
            }
        }

        cameraPermission.launch(Manifest.permission.CAMERA)
    }

    fun openGallery(activity: AppCompatActivity) {
        galleryPermission = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                galleryLauncher = activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if (uri != null) {
                        galleryUriCallback?.invoke(uri, "")
                    } else {
                        galleryUriCallback?.invoke(null, "Failed to get image from gallery")
                    }
                }
                galleryLauncher.launch("image/*")
            } else {
                galleryUriCallback?.invoke(null, "Storage permission must be approved")
            }
        }

        galleryPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun setCameraUriCallback(callback: (Uri?, String) -> Unit) {
        cameraUriCallback = callback
    }

    fun setGalleryUriCallback(callback: (Uri?, String) -> Unit) {
        galleryUriCallback = callback
    }
}
