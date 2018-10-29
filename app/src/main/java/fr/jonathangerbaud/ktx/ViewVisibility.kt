package fr.jonathangerbaud.ktx

import android.view.View

fun View?.show() {
    this?.visibility = View.VISIBLE;
}

fun View?.hide() {
    this?.visibility = View.GONE;
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE;
}

fun View?.show(show: Boolean) {
    this?.visibility = if (show) View.VISIBLE else View.GONE
}

fun View?.hide(show: Boolean) {
    this?.visibility = if (show) View.GONE else View.VISIBLE
}

fun View?.invisible(show: Boolean) {
    this?.visibility = if (show) View.INVISIBLE else View.VISIBLE
}

fun View?.toggleVisibility() {
    if (this?.visibility == View.VISIBLE)
        visibility = View.GONE;
    else if (this?.visibility == View.GONE)
        visibility = View.VISIBLE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.isGone(): Boolean = visibility == View.GONE

fun View.isInvisible(): Boolean = visibility == View.INVISIBLE

fun View.isNotVisible(): Boolean = visibility != View.VISIBLE

class ViewVisibility
{
    companion object
    {
        fun show(vararg views: View?)
        {
            for (v in views)
                v.show()
        }

        fun hide(vararg views: View?)
        {
            for (v in views)
                v.hide()
        }

        fun invisible(vararg views: View?)
        {
            for (v in views)
                v.invisible()
        }
    }
}