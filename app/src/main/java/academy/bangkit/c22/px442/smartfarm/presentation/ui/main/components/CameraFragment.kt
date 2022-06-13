package academy.bangkit.c22.px442.smartfarm.presentation.ui.main.components

import academy.bangkit.c22.px442.smartfarm.R
import academy.bangkit.c22.px442.smartfarm.constant.DataStore
import academy.bangkit.c22.px442.smartfarm.databinding.FragmentCameraBinding
import academy.bangkit.c22.px442.smartfarm.ml.Model
import academy.bangkit.c22.px442.smartfarm.presentation.dialog.CustomProgressDialog
import academy.bangkit.c22.px442.smartfarm.presentation.dialog.SnackBarCustom
import academy.bangkit.c22.px442.smartfarm.presentation.utils.JavaHelper
import academy.bangkit.c22.px442.smartfarm.presentation.utils.PestisidaHelper.Companion.pestisidaFromResult
import academy.bangkit.c22.px442.smartfarm.presentation.utils.createCustomTempFile
import academy.bangkit.c22.px442.smartfarm.presentation.utils.uriToFile
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File

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

    var image: Bitmap? = null
    val imageSize = 300

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                viewModel.setImgBitmap(myFile)

                binding.placeHolder.isVisible = false

                image = BitmapFactory.decodeFile(myFile.path)
                binding.imgPreview.setImageBitmap(image)

                image = Bitmap.createScaledBitmap(image!!, imageSize, imageSize, false)
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedImg: Uri = it.data?.data as Uri
                val myFile = uriToFile(selectedImg, requireActivity())
                getFile = myFile
                viewModel.setImgBitmap(myFile)

                binding.placeHolder.isVisible = false

                image = BitmapFactory.decodeFile(myFile.path)
                binding.imgPreview.setImageBitmap(image)

                image = Bitmap.createScaledBitmap(image!!, imageSize, imageSize, false)
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
                if (image != null) {
                    val model = Model.newInstance(requireActivity().applicationContext)

                    // Creates inputs for reference.
                    val inputFeature0 = TensorBuffer.createFixedSize(
                        intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32
                    )

                    // Work here
                    val byteBuffer = JavaHelper.imageToByteBuffer(image, imageSize)

                    inputFeature0.loadBuffer(byteBuffer)

                    // Runs model inference and gets result.
                    val outputs = model.process(inputFeature0)
                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

                    // Last here
                    val confidences = outputFeature0.floatArray
                    // find the index of the class with the biggest confidence.
                    // find the index of the class with the biggest confidence.
                    var maxPos = 0
                    var maxConfidence = 0f
                    for (i in confidences.indices) {
                        if (confidences[i] > maxConfidence) {
                            maxConfidence = confidences[i]
                            maxPos = i
                        }
                    }
                    val classes = arrayOf("Bacterialblight", "Blast", "Brownspot", "Tungro")

                    binding.txtResult.text = classes[maxPos]

                    binding.txtPestisida.text = pestisidaFromResult(classes[maxPos])[0]
                    binding.txtDesc.text = pestisidaFromResult(classes[maxPos])[1]

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
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        private const val ACCURACY_THRESHOLD = 0.5f
        private const val MODEL_PATH = "model.tflite"
        private const val LABELS_PATH = "labels.txt"
    }
}