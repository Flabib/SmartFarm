package academy.bangkit.c22.px442.smartfarm.presentation.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import academy.bangkit.c22.px442.smartfarm.R
import academy.bangkit.c22.px442.smartfarm.constant.InputType
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class CustomTextInputLayout  : TextInputLayout {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){ init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        isHintEnabled = false
        boxBackgroundMode = BOX_BACKGROUND_NONE
    }

    private fun init(){
        addOnEditTextAttachedListener {
            it.editText?.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s.isNotEmpty()){
                        when{
                            s.length < 6 && it.editText!!.inputType == InputType.INPUT_TYPE_PASSWORD -> {
                                isErrorEnabled = true
                                error = context.getString(R.string.msg_error_pass)
                            }
                            s.length < 3 && it.editText!!.inputType == InputType.INPUT_TYPE_PERSON_NAME -> {
                                isErrorEnabled = true
                                error = context.getString(R.string.msg_error_name)
                            }
                            !s.toString().lowercase(Locale.getDefault()).contains("@") && it.editText!!.inputType == InputType.INPUT_TYPE_EMAIL -> {
                                isErrorEnabled = true
                                error = context.getString(R.string.msg_error_email)
                            }
                            else -> {
                                isErrorEnabled = false
                                error = null
                            }
                        }
                    }
                }

            })
        }
    }
}