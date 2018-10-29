package fr.jonathangerbaud.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.*;
import androidx.core.content.ContextCompat;
import fr.jonathangerbaud.BaseApp;

public class ResUtils
{
    private static Context getContext()
    {
        return BaseApp.Companion.get();
    }

    private static Resources getRes()
    {
        return getContext().getResources();
    }

    public static float getDimension(@DimenRes int resId)
    {
        return getRes().getDimension(resId);
    }

    public static int getDimensionPxSize(@DimenRes int resId)
    {
        return getRes().getDimensionPixelSize(resId);
    }

    public static boolean getBooleanString(@BoolRes int resId)
    {
        return getRes().getBoolean(resId);
    }

    public static String getString(@StringRes int resId)
    {
        return getRes().getString(resId);
    }

    public static String getString(@StringRes int resId, Object... args)
    {
        return getRes().getString(resId, args);
    }

    public static String[] getStringArray(@ArrayRes int resId)
    {
        return getRes().getStringArray(resId);
    }

    public static int[] getIntArray(@ArrayRes int resId)
    {
        return getRes().getIntArray(resId);
    }

    public static int getColor(@ColorRes int resId)
    {
        return ContextCompat.getColor(getContext(), resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId)
    {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    public static AssetManager getAssetManager(@ArrayRes int resId)
    {
        return getRes().getAssets();
    }

    public static int getDrawableByName(String name)
    {
        return getRes().getIdentifier(name, "drawable", getContext().getPackageName());
    }

    public static String getStringByName(String name)
    {
        return getString(getRes().getIdentifier(name, "string", getContext().getPackageName()));
    }
}
