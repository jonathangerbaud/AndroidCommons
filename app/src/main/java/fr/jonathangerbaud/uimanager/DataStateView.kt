package fr.jonathangerbaud.uimanager


import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.view.MarginLayoutParamsCompat
import fr.jonathangerbaud.R
import fr.jonathangerbaud.ktx.ViewVisibility
import fr.jonathangerbaud.ktx.getDimensionPixelsSize
import fr.jonathangerbaud.ktx.hide
import fr.jonathangerbaud.ktx.show

open class DataStateView : LinearLayout
{


    private var progressBar: ProgressBar? = null
    private var image: ImageView? = null
    private var text: TextView? = null
    private var retryButton: Button? = null

    @StringRes
    private var emptyMessage: Int = 0
    @DrawableRes
    private var emptyImage: Int = 0
    @StringRes
    private var errorMessage: Int = 0
    @DrawableRes
    private var errorImage: Int = 0

    constructor(context: Context) : super(context)
    {
        // Necessary constructor...
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    {
        // Necessary constructor...
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
        // Necessary constructor...
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes
    defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    {
        // Necessary constructor...
    }

    init
    {
        orientation = LinearLayout.VERTICAL

        val params = ViewGroup.MarginLayoutParams(
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val margin = context.getDimensionPixelsSize(R.dimen.dp_32)

        //params.topMargin = margin
        MarginLayoutParamsCompat.setMarginStart(params, margin)
        MarginLayoutParamsCompat.setMarginEnd(params, margin)
        layoutParams = params

        LayoutInflater.from(context).inflate(R.layout.fr_jg_inflate_datastateview, this, true)

        progressBar = findViewById(R.id.progress)
        image = findViewById(R.id.image)
        text = findViewById(R.id.error_label)
        retryButton = findViewById(R.id.retry_button)

        //ViewCompat.setBackgroundTintList(retryButton!!, ColorStateList.valueOf(Session.getPrimaryColor()))

        //ToDo define attrs in XML

        emptyMessage = DEFAULT_EMPTY_MESSAGE
        emptyImage = DEFAULT_EMPTY_IMAGE
        errorMessage = DEFAULT_ERROR_MESSAGE
        errorImage = DEFAULT_ERROR_IMAGE
    }

    fun setStateLoading()
    {
        progressBar.show()
        ViewVisibility.hide(image, text, retryButton)
    }

    fun setStateEmpty()
    {
        progressBar.hide()

        if (emptyImage != 0)
        {
            image?.setImageResource(emptyImage)
            image.show()
        }
        else
        {
            image.hide()
        }

        text?.setText(emptyMessage)
        text.show()

        retryButton.hide()
    }

    fun setStateError()
    {
        progressBar.hide()

        if (errorImage != 0)
        {
            image?.setImageResource(errorImage)
            image.show()
        }
        else
        {
            image.hide()
        }

        text?.setText(errorMessage)
        text.show()

        retryButton.show()
    }

    fun setEmptyImage(@DrawableRes resId: Int): DataStateView
    {
        emptyImage = resId
        return this
    }

    fun setErrorImage(@DrawableRes resId: Int): DataStateView
    {
        errorImage = resId
        return this
    }

    fun setEmptyMessage(@StringRes resId: Int): DataStateView
    {
        emptyMessage = resId
        return this
    }

    fun setErrorMessage(@StringRes resId: Int): DataStateView
    {
        errorMessage = resId
        return this
    }

    fun setErrorButtonText(@StringRes resId: Int): DataStateView
    {
        retryButton?.setText(resId)
        return this
    }

    fun setRetryCallback(callback: RetryCallback?)
    {
        if (retryButton != null)
        {
            retryButton?.setOnClickListener {
                callback?.retry()
            }
        }
    }

    companion object
    {
        @StringRes
        private val DEFAULT_EMPTY_MESSAGE = R.string.jg_empty
        @DrawableRes
        private val DEFAULT_EMPTY_IMAGE = 0
        @StringRes
        private val DEFAULT_ERROR_MESSAGE = R.string.jg_error_generic
        @DrawableRes
        private val DEFAULT_ERROR_IMAGE = 0
    }
}