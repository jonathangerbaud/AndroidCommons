package fr.jonathangerbaud.ktx

import fr.jonathangerbaud.BaseApp

private val metrics = BaseApp.get().getDisplayMetrics()

fun deviceWidth(): Int =  metrics.widthPixels
fun deviceHeight(): Int =  metrics.heightPixels