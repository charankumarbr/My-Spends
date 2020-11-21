package in.phoenix.myspends.customview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.textfield.TextInputLayout;

import in.phoenix.myspends.R;
import in.phoenix.myspends.util.AppCrashLogger;

/**
 * Created by Charan.Br on 6/28/2018.
 */

public final class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }

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
            //AppLog.d("CustomEditText", fontName + "::" + textStyle + "::" + getText());
            try {
                setTypeface(FontCache.getFont(getContext(), fontName, textStyle));

            } catch (Resources.NotFoundException e) {
                AppCrashLogger.INSTANCE.reportException(e);
            }

            typedArray.recycle();
        }
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        final InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic != null && outAttrs.hintText == null) {
            // If we don't have a hint and our parent is a TextInputLayout, use it's hint for the
            // EditorInfo. This allows us to display a hint in 'extract mode'.
            ViewParent parent = getParent();
            while (parent instanceof View) {
                if (parent instanceof TextInputLayout) {
                    outAttrs.hintText = ((TextInputLayout) parent).getHint();
                    break;
                }
                parent = parent.getParent();
            }
        }
        return ic;
    }
}
