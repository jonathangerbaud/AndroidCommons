package fr.jonathangerbaud.ktx

import android.widget.TextView

fun TextView.setTextOrHide(text: String?)
{
    if (text.isNullOrBlank())
    {
        hide()
    }
    else
    {
        this.text = text
        show()
    }
}