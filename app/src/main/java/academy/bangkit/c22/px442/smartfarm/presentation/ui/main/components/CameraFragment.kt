package academy.bangkit.c22.px442.smartfarm.presentation.ui.main.components

import academy.bangkit.c22.px442.smartfarm.R
import academy.bangkit.c22.px442.smartfarm.constant.DataStore
import academy.bangkit.c22.px442.smartfarm.databinding.FragmentCameraBinding
import academy.bangkit.c22.px442.smartfarm.ml.BlurImageModel
import academy.bangkit.c22.px442.smartfarm.ml.Model
import academy.bangkit.c22.px442.smartfarm.presentation.dialog.CustomProgressDialog
import academy.bangkit.c22.px442.smartfarm.presentation.dialog.SnackBarCustom
import academy.bangkit.c22.px442.smartfarm.presentation.ui.ObjectDetectionHelper
import academy.bangkit.c22.px442.smartfarm.presentation.utils.createCustomTempFile
import academy.bangkit.c22.px442.smartfarm.presentation.utils.reduceFileImage
import academy.bangkit.c22.px442.smartfarm.presentation.utils.uriToFile
import academy.bangkit.c22.px442.smartfarm.presentation.utils.uriToFile
import androidx.navigation.fragment.findNavController
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn.hasPermissions
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.random.Random

class CameraFragment : Fragment() {
    private val viewModel: CameraViewModel by activityViewModels()
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private val progressDialog: CustomProgressDialog by lazy { CustomProgressDialog(requireActivity()) }
    private val snackBar: SnackBarCustom by lazy { SnackBarCustom(requireActivity()) }

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private val isFrontFacing get() = lensFacing == CameraSelector.LENS_FACING_FRONT

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val myFile = File(currentPhotoPath)
//                val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), true)
                getFile = myFile
                viewModel.setImgBitmap(myFile)

                binding.placeHolder.isVisible = false
                binding.imgPreview.setImageBitmap(BitmapFactory.decodeFile(myFile.path))
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedImg: Uri = it.data?.data as Uri
                val myFile = uriToFile(selectedImg, requireActivity())
                val result = BitmapFactory.decodeFile(myFile.path)
                getFile = myFile
                viewModel.setImgBitmap(myFile)

                binding.placeHolder.isVisible = false
                binding.imgPreview.setImageBitmap(result)
            }
        }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            permission.entries.forEach {
                if (it.key == Manifest.permission.CAMERA && it.value == true) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.msg_camera_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (it.key == Manifest.permission.CAMERA && it.value == false) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.msg_camera_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    /*TFLite*/
    private lateinit var bitmapBuffer: Bitmap

    private val executor = Executors.newSingleThreadExecutor()
    private val permissionsRequestCode = Random.nextInt(0, 10000)

//    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
//    private val isFrontFacing get() = lensFacing == CameraSelector.LENS_FACING_FRONT

    private var pauseAnalysis = false
    private var imageRotationDegrees: Int = 0
    private val tfImageBuffer = TensorImage(DataType.UINT8)

    private val tfImageProcessor by lazy {
        val cropSize = minOf(bitmapBuffer.width, bitmapBuffer.height)
        ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(
                ResizeOp(
                    tfInputSize.height, tfInputSize.width, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR
                )
            )
            .add(Rot90Op(-imageRotationDegrees / 90))
            .add(NormalizeOp(0f, 1f))
            .build()
    }

    private val nnApiDelegate by lazy {
        NnApiDelegate()
    }

    private val tflite by lazy {
        Interpreter(
            FileUtil.loadMappedFile(requireActivity(), MODEL_PATH),
            Interpreter.Options().addDelegate(nnApiDelegate)
        )
    }
//    private val detector by lazy {
//        ObjectDetectionHelper(
//            tflite,
//            FileUtil.loadLabels(requireActivity(), LABELS_PATH)
//        )
//    }

    private val tfInputSize by lazy {
        val inputIndex = 0
        val inputShape = tflite.getInputTensor(inputIndex).shape()
        Size(inputShape[2], inputShape[1]) // Order of axis is: {1, height, width, 3}
    }
    /*TFLite*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view, savedInstanceState)
    }

    private fun initView(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            toolbar.inflateMenu(R.menu.post_menu)
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.toolbar_camera -> {
                        if (!allPermissionsGranted()) {
                            requestPermission.launch(REQUIRED_PERMISSIONS)
                        }
                        if (allPermissionsGranted()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.resolveActivity(requireActivity().packageManager)
                            createCustomTempFile(requireActivity().application).also {
                                val photoUri = FileProvider.getUriForFile(
                                    requireActivity(),
                                    DataStore.STORE_NAME,
                                    it
                                )
                                currentPhotoPath = it.absolutePath
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                                launcherIntentCamera.launch(intent)
                            }
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.msg_camera_denied),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    R.id.toolbar_gallery -> {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        val chooser = Intent.createChooser(intent, "Choose a Picture")
                        launcherIntentGallery.launch(chooser)
                    }
                }
                return@setOnMenuItemClickListener false
            }

            btnPost.setOnClickListener {
                progressDialog.show()
                if (getFile != null) {
//                    val file = reduceFileImage(getFile as File)


                    val model = Model.newInstance(requireActivity())
                    val tensorImage = TensorImage.fromBitmap(BitmapFactory.decodeFile(getFile?.path))
                    val inputFeature0 = TensorBuffer.createFixedSize(
                        intArrayOf(1, 300, 300, 3), DataType.FLOAT32
                    )

                    // Runs model inference and gets result.
                    val outputs = model.process(inputFeature0)
                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                    // Releases model resources if no longer used.
                    model.close()

                    // return true if image is valid, and false if not valid
                    Log.d("TAST", "outputFeature0: ${outputFeature0.buffer}")
                    Log.d("TAST", "outputFeature0: ${outputFeature0.dataType}")
                    Log.d("TAST", "outputFeature0: ${outputFeature0.shape}")
                    Log.d("TAST", "outputFeature0: ${outputFeature0.flatSize}")
                    Log.d("TAST", "outputFeature0: ${outputFeature0.floatArray}")
                    Log.d("TAST", "outputFeature0: ${outputFeature0.typeSize}")

                    progressDialog.hide()
                }
            }
        }

        viewModel.imgBitmap.observe(viewLifecycleOwner) {
            binding.btnPost.isEnabled = it !== null
            getFile = it
        }
    }

    override fun onResume() {
        super.onResume()

        // Request permissions each time the app resumes, since they can be revoked at any time
        if (!hasPermissions((requireActivity()))) {
            requestPermission.launch(REQUIRED_PERMISSIONS)
        } else {
//            bindCameraUseCases()
        }
    }

    /** Convenience method used to check if all permissions required by this app are granted */
    private fun hasPermissions(context: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        // Terminate all outstanding analyzing jobs (if there is any).
        executor.apply {
            shutdown()
            awaitTermination(1000, TimeUnit.MILLISECONDS)
        }

        // Release TFLite resources.
        tflite.close()
        nnApiDelegate.close()

        _binding = null
        super.onDestroy()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        private const val ACCURACY_THRESHOLD = 0.5f
        private const val MODEL_PATH = "model.tflite"
//        private const val LABELS_PATH = "coco_ssd_mobilenet_v1_1.0_labels.txt"
    }
}