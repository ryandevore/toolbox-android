package uu.toolbox.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.widget.TextView;

import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

public class UUTextView
{
    public static void setFontFromStyle(
            @NonNull final TextView tv,
            @Nullable final AttributeSet attrs,
            @StyleableRes int[] styelableSet,
            final int fontNameId)
    {
        try
        {
            if (attrs != null)
            {
                Context ctx = tv.getContext();
                TypedArray a = ctx.obtainStyledAttributes(attrs, styelableSet);
                String fontName = a.getString(fontNameId);

                if (UUString.isNotEmpty(fontName))
                {
                    Typeface myTypeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + fontName);
                    tv.setTypeface(myTypeface);
                }

                a.recycle();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUTextView.class, "setFontFromStyle", ex);
        }
    }
}
