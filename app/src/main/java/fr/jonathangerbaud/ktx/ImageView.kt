package fr.jonathangerbaud.ktx

import android.widget.ImageView

fun ImageView.setImageResourceOrHide(res: Int)
{
    if (res <= 0)
    {
        hide()
    }
    else
    {
        setImageResource(res)
        show()
    }
}