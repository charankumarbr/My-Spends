package in.phoenix.myspends.customview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.crashlytics.android.Crashlytics;

import in.phoenix.myspends.R;

/**
 * Created by Charan.Br on 2/28/2017.
 */

public final class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode())
            return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);
        if (null != typedArray) {

            String fontName = typedArray.getString(R.styleable.CustomFont_fontName);
            if (null == fontName) {
                fontName = "fontNotoSans";
            }
            int textStyle = Typeface.NORMAL;
            if (null != attrs) {
                textStyle = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android",
                        "textStyle", Typeface.NORMAL);
            }
            //AppLog.d("CustomTextView", fontName + "::" + textStyle);
            try {
                setTypeface(FontCache.getFont(getContext(), fontName, textStyle));

            } catch (Resources.NotFoundException e) {
                Crashlytics.logException(e);
            }

            typedArray.recycle();
        }
    }
}
