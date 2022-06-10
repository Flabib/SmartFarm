package academy.bangkit.c22.px442.smartfarm.base

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import academy.bangkit.c22.px442.smartfarm.presentation.dialog.CustomProgressDialog
import academy.bangkit.c22.px442.smartfarm.presentation.dialog.SnackBarCustom

abstract class BaseFragment<VB: ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {
    private var _binding : VB? = null
    protected val binding get() = _binding!!
    protected val progressDialog: CustomProgressDialog by lazy { CustomProgressDialog(requireContext()) }
    protected val snackBar: SnackBarCustom by lazy { SnackBarCustom(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    abstract fun initView(view: View, savedInstanceState: Bundle?)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun playAnimation(view: View) {
        ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}