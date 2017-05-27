package in.charanbr.expensetracker.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.util.AppLog;

/**
 * Created by Charan.Br on 2/28/2017.
 */

public final class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        if (isInEditMode())
            return;

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomTextView);
        if (null != typedArray) {

            String fontName = typedArray.getString(R.styleable.CustomTextView_fontName);
            int textStyle = Typeface.NORMAL;
            if (null != attributeSet) {
                textStyle = attributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/android",
                        "textStyle", Typeface.NORMAL);
            }
            //AppLog.d("CustomTextView", fontName + "::" + textStyle);
            setTypeface(FontCache.getFont(getContext(), fontName, textStyle));

            typedArray.recycle();
        }
    }
}
