package `in`.phoenix.myspends.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Parcelable


/**
 * Created by Charan on November 21, 2020
 */
object WhatsAppUtil {

    private val ALLOWED_PACKAGE_NAMES = arrayOf("com.whatsapp","com.whatsapp.w4b")

    fun generateCustomChooserIntent(context: Context, codeAndNumber: String?): Intent? {

        if ((codeAndNumber == null) || codeAndNumber.trim().isEmpty()) {
            return null
        }

        val url = "https://api.whatsapp.com/send?phone=".plus(codeAndNumber)
        val fakeUri: Uri = Uri.parse(url)
        val realUri: Uri = Uri.parse(url)
        val shareIntent = Intent(Intent.ACTION_VIEW, fakeUri)
        val resInfo: List<ResolveInfo>
        resInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_ALL)
        } else {
            context.packageManager.queryIntentActivities(shareIntent, 0)
        }

        if (resInfo.isNotEmpty()) {
            val targetedShareIntents: MutableList<Intent> = removeCurrentApp(context, realUri, resInfo)
            if (targetedShareIntents.isNotEmpty()) {
                // pass new Intent to create no chooser in first row
                val chooserIntent = Intent.createChooser(
                        targetedShareIntents[0], "Text on WhatsApp")
                targetedShareIntents.removeAt(0)

                // pass extra intent chooser
                chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toTypedArray<Parcelable>())
                return chooserIntent
            }
        }
        return null
    }

    private fun removeCurrentApp(context: Context, realUri: Uri, resInfo: List<ResolveInfo>): MutableList<Intent> {
        val targetedShareIntents: MutableList<Intent> = ArrayList()
        val currentPackageName: String = context.packageName
        for (resolveInfo in resInfo) {
            // do not include my app in intent chooser dialog
            if (resolveInfo.activityInfo == null) {
                continue
            }
            val packageName = resolveInfo.activityInfo.packageName
            if (isWhatsApp(packageName)) {
                val intent = Intent(Intent.ACTION_VIEW, realUri)
                intent.setClassName(
                        resolveInfo.activityInfo.applicationInfo.packageName,
                        resolveInfo.activityInfo.name)
                intent.setPackage(packageName)
                targetedShareIntents.add(intent)
            }
        }
        return targetedShareIntents
    }

    fun isWhatsApp(packageName: String): Boolean {
        return ALLOWED_PACKAGE_NAMES.contains(packageName)
    }

}