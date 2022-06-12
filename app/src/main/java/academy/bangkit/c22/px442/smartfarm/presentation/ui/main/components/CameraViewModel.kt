package academy.bangkit.c22.px442.smartfarm.presentation.ui.main.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class CameraViewModel() : ViewModel() {
    private var _imgBitmap = MutableLiveData<File>()
    val imgBitmap: LiveData<File>
        get() = _imgBitmap

    fun setImgBitmap(file: File) {
        _imgBitmap.postValue(file)
    }
}