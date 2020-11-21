package in.phoenix.myspends.customview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

import in.phoenix.myspends.R;
import in.phoenix.myspends.util.AppCrashLogger;

public final class CustomCheckBox extends AppCompatCheckBox {
    public CustomCheckBox(Context context) {
        super(context);
        init(null);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode())
            return;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFont);
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
            //AppLog.d("CustomCheckBox", fontName + "::" + textStyle);
            try {
                setTypeface(FontCache.getFont(getContext(), fontName, textStyle));

            } catch (Resources.NotFoundException e) {
                AppCrashLogger.INSTANCE.reportException(e);
            }
            typedArray.recycle();
        }
    }
}
