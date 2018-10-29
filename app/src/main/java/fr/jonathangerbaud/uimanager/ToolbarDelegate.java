package fr.jonathangerbaud.uimanager;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import fr.jonathangerbaud.R;

import java.lang.ref.WeakReference;

public class ToolbarDelegate
{
    private WeakReference<AppCompatActivity> activity;
    private WeakReference<Toolbar>           toolbar;

    public ToolbarDelegate(AppCompatActivity activity, Toolbar toolbar)
    {
        this.activity = new WeakReference<>(activity);
        this.toolbar = new WeakReference<>(toolbar);

        customizeToolbar(toolbar);

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onNavigationClick());
    }

    public ToolbarDelegate title(String title)
    {
        if (activity.get() != null)
            activity.get().getSupportActionBar().setTitle(title);

        return this;
    }

    public ToolbarDelegate title(@StringRes int titleRes)
    {
        if (activity.get() != null)
            activity.get().getSupportActionBar().setTitle(titleRes);

        return this;
    }

    public ToolbarDelegate drawer()
    {
        if (toolbar.get() != null)
            toolbar.get().setNavigationOnClickListener(null);

        if (activity.get() != null)
            activity.get().getSupportActionBar().setHomeAsUpIndicator(getDefaultHomeAsUpIndicator());

        return this;
    }

    public ToolbarDelegate drawer(@DrawableRes int drawableRes)
    {
        if (toolbar.get() != null)
            toolbar.get().setNavigationOnClickListener(null);

        if (activity.get() != null)
            activity.get().getSupportActionBar().setHomeAsUpIndicator(drawableRes);

        return this;
    }

    public ToolbarDelegate icon(@DrawableRes int drawableRes)
    {
        if (activity.get() != null)
            activity.get().getSupportActionBar().setHomeAsUpIndicator(drawableRes);

        return this;
    }

    public ToolbarDelegate backgroundColor(@ColorInt int color)
    {
        if (toolbar.get() != null)
            toolbar.get().setBackgroundColor(color);
        else if (activity.get() != null)
            activity.get().getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        return this;
    }

    private void onNavigationClick()
    {
        Activity activity = this.activity.get();

        if (activity != null)
            activity.finish();
    }


    // Configuration options to be applied by subclass

    protected void customizeToolbar(Toolbar toolbar)
    {

    }

    protected int getDefaultHomeAsUpIndicator()
    {
        return R.drawable.fr_jg_ic_toolbar_drawer_icon;
    }
}
