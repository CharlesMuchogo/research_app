package com.charlesmuchogo.research.presentation.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePicker(private val context: Context) {

    private lateinit var takePicture: ManagedActivityResultLauncher<Uri, Boolean>
    private lateinit var cropImageLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    private lateinit var permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    @Composable
    fun RegisterPicker(onImagePicked: (ByteArray) -> Unit) {

        photoUri = remember { mutableStateOf(createImageUri()) }.value

        val (currentPhotoFile, setPhotoFile) = remember { mutableStateOf<File?>(null) }
        val (currentPhotoUri, setPhotoUri) = remember { mutableStateOf<Uri?>(null) }


        permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true) {
                val (file, uri) = createImageFile()
                setPhotoFile(file)
                setPhotoUri(uri)
                takePicture.launch(photoUri)
            } else {
                Toast.makeText(context, "Allow Camera permissions to continue", Toast.LENGTH_LONG).show()
            }
        }


        takePicture = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success) {
                    currentPhotoUri?.let { uri ->
                        startCrop(uri)
                    }
                }
            }
        )

        cropImageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val croppedUri = UCrop.getOutput(intent)
                    croppedUri?.let {
                        // Convert cropped image to ByteArray and pass it back
                        val inputStream = context.contentResolver.openInputStream(it)
                        val byteArray = inputStream?.readBytes()
                        byteArray?.let { croppedBytes ->
                            onImagePicked(croppedBytes)
                        }
                    }
                }
            }
        }
    }


    private fun createImageFile(): Pair<File, Uri> {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        return Pair(file, uri)
    }


    private fun createImageUri(): Uri {
        val imageName = "captured_image_${System.currentTimeMillis()}.jpg"
        val imageFile = File(context.cacheDir, imageName)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }


    fun captureImage() {
        if (allPermissionsGranted()) {
            val (file, uri) = createImageFile()
            photoFile = file
            photoUri = uri
            takePicture.launch(photoUri)
        } else {
            // Request permissions
            permissionLauncher.launch(arrayOf(
                Manifest.permission.CAMERA
            ))
        }
    }

    private fun allPermissionsGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(context.cacheDir, "croppedImage.png"))

        val options = UCrop.Options()
        options.setCompressionQuality(100)
        options.setMaxBitmapSize(10000)


        options.setCompressionFormat(Bitmap.CompressFormat.PNG)

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(4f, 3f)
            .withMaxResultSize(300, 300)
            .withOptions(options)
            .getIntent(context)

        cropImageLauncher.launch(uCrop)
    }

    private fun getImageUriFromByteArray(byteArray: ByteArray): Uri {
        val tempFile = File.createTempFile("capturedImage", ".png", context.cacheDir)
        tempFile.writeBytes(byteArray)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }
}

