package in.phoenix.myspends.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;

import java.util.HashMap;

import in.phoenix.myspends.R;
import in.phoenix.myspends.util.AppLog;

/**
 * Created by Charan.Br on 2/28/2017.
 */
final class FontCache {

    private static final HashMap<String, Typeface> fontMap = new HashMap<>();

    public static Typeface getFont(Context context, String fontName, int textStyle) {

        if (fontMap.containsKey(fontName + textStyle)) {
            return fontMap.get(fontName + textStyle);
        }

        if (fontName.equals(context.getString(R.string.font_noto_sans))) {

            Typeface typeface;
            switch (textStyle) {
                case Typeface.BOLD:
                    //typeface = Typeface.createFromAsset(context.getAssets(), "fonts/notosans_bold.ttf");
                    typeface = ResourcesCompat.getFont(context, R.font.notosans_bold);
                    fontMap.put(fontName + textStyle, typeface);
                    AppLog.d("FontCache", "Generating new typeface: BOLD");
                    return typeface;

                case Typeface.NORMAL:
                default:
                    textStyle = Typeface.NORMAL;
                    if (fontMap.containsKey(fontName + textStyle)) {
                        return fontMap.get(fontName + textStyle);
                    }

                    //typeface = Typeface.createFromAsset(context.getAssets(), "fonts/notosans_regular.ttf");
                    typeface = ResourcesCompat.getFont(context, R.font.notosans_regular);
                    fontMap.put(fontName + textStyle, typeface);
                    AppLog.d("FontCache", "Generating new typeface: NORMAL/Default");
                    return typeface;
            }
        }
        return null;
    }
}
