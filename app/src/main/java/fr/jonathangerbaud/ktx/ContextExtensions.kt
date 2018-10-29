package fr.jonathangerbaud.ktx


import android.app.Activity
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.Serializable


fun Context.getAnimation(animResId: Int): XmlResourceParser =
    resources().getAnimation(animResId)

fun Context.getBoolean(booleanResId: Int): Boolean =
    resources().getBoolean(booleanResId)

fun Context.getColor(colorResId: Int): Int =
    ContextCompat.getColor(this, colorResId)

fun Context.getDimension(dimenResId: Int): Float =
    resources().getDimension(dimenResId)

fun Context.getDimensionPixelsSize(dimenResId: Int): Int =
    resources().getDimensionPixelSize(dimenResId)

fun Context.getDisplayMetrics(): DisplayMetrics =
    resources().getDisplayMetrics()

fun Context.getIntArray(id: Int): IntArray =
    resources().getIntArray(id)

fun Context.getInteger(id: Int): Int =
    resources().getInteger(id)

fun Context.getLayout(id: Int): XmlResourceParser =
    resources().getLayout(id)

fun Context.getQuantityString(id: Int, quantity: Int): String =
    resources().getQuantityString(id, quantity)

fun Context.getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String =
    resources().getQuantityString(id, quantity, formatArgs)

fun Context.getQuantityText(id: Int, quantity: Int): CharSequence =
    resources().getQuantityText(id, quantity)

fun Context.getStringArray(arrayResId: Int): Array<String> =
    resources().getStringArray(arrayResId)

fun Context.typefaceFromAssets(assetPathResId: Int): Typeface =
    typefaceFromAssets(getString(assetPathResId))

fun Context.typefaceFromAssets(assetPath: String): Typeface =
    Typeface.createFromAsset(assets, assetPath)

/*
 * -----------------------------------------------------------------------------
 *  Private functions
 * -----------------------------------------------------------------------------
 */
private fun Context.resources(): Resources = this.getResources()


fun Context.browse(url: String, newTask: Boolean = false): Boolean
{
    var url = url

    if (!url.startsWith("http://") || !url.startsWith("https://"))
        url = "https://$url"

    return try
    {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    }
    catch (e: ActivityNotFoundException)
    {
        e.printStackTrace()
        false
    }
}

fun Context.share(text: String, subject: String = ""): Boolean
{
    return try
    {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, null))
        true
    }
    catch (e: ActivityNotFoundException)
    {
        e.printStackTrace()
        false
    }
}

fun Context.email(email: String, subject: String = "", text: String = ""): Boolean
{
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)
    if (intent.resolveActivity(packageManager) != null)
    {
        startActivity(intent)
        return true
    }
    return false

}

fun Context.makeCall(number: String): Boolean
{
    return try
    {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    }
    catch (e: Exception)
    {
        e.printStackTrace()
        false
    }
}

fun Context.sendSMS(number: String, text: String = ""): Boolean
{
    return try
    {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(intent)
        true
    }
    catch (e: Exception)
    {
        e.printStackTrace()
        false
    }
}





inline fun <reified T: Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
    Internals.internalStartActivity(this, T::class.java, params)

inline fun <reified T: Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) =
    Internals.internalStartActivity(activity!!, T::class.java, params)

inline fun <reified T: Activity> Activity.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) =
    Internals.internalStartActivityForResult(this, T::class.java, requestCode, params)

inline fun <reified T: Activity> Fragment.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) =
    startActivityForResult(Internals.createIntent(activity!!, T::class.java, params), requestCode)

inline fun <reified T: Service> Context.startService(vararg params: Pair<String, Any?>) =
    Internals.internalStartService(this, T::class.java, params)

inline fun <reified T: Service> Fragment.startService(vararg params: Pair<String, Any?>) =
    Internals.internalStartService(activity!!, T::class.java, params)

inline fun <reified T : Service> Context.stopService(vararg params: Pair<String, Any?>) =
    Internals.internalStopService(this, T::class.java, params)

inline fun <reified T : Service> Fragment.stopService(vararg params: Pair<String, Any?>) =
    Internals.internalStopService(activity!!, T::class.java, params)

inline fun <reified T: Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent =
    Internals.createIntent(this, T::class.java, params)

inline fun <reified T: Any> Fragment.intentFor(vararg params: Pair<String, Any?>): Intent =
    Internals.createIntent(activity!!, T::class.java, params)

/**
 * Add the [Intent.FLAG_ACTIVITY_CLEAR_TASK] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.clearTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }

/**
 * Add the [Intent.FLAG_ACTIVITY_CLEAR_TOP] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.clearTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }

/**
 * Add the [Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
@Deprecated(message = "Deprecated in Android", replaceWith = ReplaceWith("org.jetbrains.anko.newDocument"))
inline fun Intent.clearWhenTaskReset(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET) }

/**
 * Add the [Intent.FLAG_ACTIVITY_NEW_DOCUMENT] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.newDocument(): Intent = apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    } else {
        @Suppress("DEPRECATION")
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    }
}

/**
 * Add the [Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.excludeFromRecents(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }

/**
 * Add the [Intent.FLAG_ACTIVITY_MULTIPLE_TASK] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.multipleTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK) }

/**
 * Add the [Intent.FLAG_ACTIVITY_NEW_TASK] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.newTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

/**
 * Add the [Intent.FLAG_ACTIVITY_NO_ANIMATION] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.noAnimation(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) }

/**
 * Add the [Intent.FLAG_ACTIVITY_NO_HISTORY] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.noHistory(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }

/**
 * Add the [Intent.FLAG_ACTIVITY_SINGLE_TOP] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
inline fun Intent.singleTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }






object Internals
{
    @JvmStatic
    fun <T> createIntent(ctx: Context, clazz: Class<out T>, params: Array<out Pair<String, Any?>>): Intent
    {
        val intent = Intent(ctx, clazz)
        if (params.isNotEmpty()) fillIntentArguments(intent, params)
        return intent
    }

    @JvmStatic
    fun internalStartActivity(
        ctx: Context,
        activity: Class<out Activity>,
        params: Array<out Pair<String, Any?>>
    )
    {
        ContextCompat.startActivity(ctx, createIntent(ctx, activity, params), null)
    }

    @JvmStatic
    fun internalStartActivityForResult(
        act: Activity,
        activity: Class<out Activity>,
        requestCode: Int,
        params: Array<out Pair<String, Any?>>
    )
    {
        act.startActivityForResult(createIntent(act, activity, params), requestCode)
    }

    @JvmStatic
    fun internalStartService(
        ctx: Context,
        service: Class<out Service>,
        params: Array<out Pair<String, Any?>>
    ): ComponentName? = ctx.startService(createIntent(ctx, service, params))

    @JvmStatic
    fun internalStopService(
        ctx: Context,
        service: Class<out Service>,
        params: Array<out Pair<String, Any?>>
    ): Boolean = ctx.stopService(createIntent(ctx, service, params))

    @JvmStatic
    private fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>)
    {
        params.forEach {
            val value = it.second
            when (value)
            {
                null            -> intent.putExtra(it.first, null as Serializable?)
                is Int          -> intent.putExtra(it.first, value)
                is Long         -> intent.putExtra(it.first, value)
                is CharSequence -> intent.putExtra(it.first, value)
                is String       -> intent.putExtra(it.first, value)
                is Float        -> intent.putExtra(it.first, value)
                is Double       -> intent.putExtra(it.first, value)
                is Char         -> intent.putExtra(it.first, value)
                is Short        -> intent.putExtra(it.first, value)
                is Boolean      -> intent.putExtra(it.first, value)
                is Serializable -> intent.putExtra(it.first, value)
                is Bundle       -> intent.putExtra(it.first, value)
                is Parcelable   -> intent.putExtra(it.first, value)
                is Array<*>     -> when
                {
                    value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<String>()       -> intent.putExtra(it.first, value)
                    value.isArrayOf<Parcelable>()   -> intent.putExtra(it.first, value)
                    else                            -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
                }
                is IntArray     -> intent.putExtra(it.first, value)
                is LongArray    -> intent.putExtra(it.first, value)
                is FloatArray   -> intent.putExtra(it.first, value)
                is DoubleArray  -> intent.putExtra(it.first, value)
                is CharArray    -> intent.putExtra(it.first, value)
                is ShortArray   -> intent.putExtra(it.first, value)
                is BooleanArray -> intent.putExtra(it.first, value)
                else            -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            return@forEach
        }
    }
}