package fr.jonathangerbaud.ktx

import android.os.Handler
import android.os.Looper

fun runAsync(runnable: Runnable) = Thread(runnable).start()
fun runAsync(action: () -> Unit) = runAsync(Runnable(action))

fun runOnUiThread(action: () -> Unit) {
    if (isMainLooperAlive()) {
        action()
    } else {
        Handler(Looper.getMainLooper()).post(Runnable(action))
    }
}

fun runDelayed(delayMillis: Long, runnable: Runnable) = Handler().postDelayed(runnable, delayMillis)
fun runDelayed(delayMillis: Long, action: () -> Unit) = runDelayed(delayMillis, Runnable(action))

fun runDelayedOnUiThread(delayMillis: Long, runnable: Runnable) = Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis)
fun runDelayedOnUiThread(delayMillis: Long, action: () -> Unit) = runDelayedOnUiThread(delayMillis, Runnable(action))

private fun isMainLooperAlive() = Looper.myLooper() == Looper.getMainLooper()