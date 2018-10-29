package fr.jonathangerbaud.ktx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup?.inflate(layoutRes: Int): View
{
    return LayoutInflater.from(this?.context).inflate(layoutRes, this, false)
}