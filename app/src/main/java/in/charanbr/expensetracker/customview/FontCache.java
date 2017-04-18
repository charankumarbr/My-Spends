package in.charanbr.expensetracker.customview;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

import in.charanbr.expensetracker.R;

/**
 * Created by Charan.Br on 2/28/2017.
 */
public final class FontCache {

    private static HashMap<String, Typeface> fontMap = new HashMap<>();

    public static Typeface getFont(Context context, String fontName, int textStyle) {

        if (fontName.equals(context.getString(R.string.font_noto_sans))) {

            Typeface typeface;
            switch (textStyle) {
                case Typeface.BOLD:
                    if (fontMap.containsKey(fontName + textStyle)) {
                        return fontMap.get(fontName + textStyle);
                    }

                    typeface = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Bold.ttf");
                    fontMap.put(fontName + textStyle, typeface);
                    return typeface;

                case Typeface.NORMAL:
                default:
                    if (fontMap.containsKey(fontName + textStyle)) {
                        return fontMap.get(fontName + textStyle);
                    }

                    typeface = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Regular.ttf");
                    fontMap.put(fontName + textStyle, typeface);
                    return typeface;
            }


        }
        return null;
    }
}
