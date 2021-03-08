package `in`.phoenix.myspends.util

import `in`.phoenix.myspends.model.Currency
import androidx.annotation.NonNull
import java.util.*

/**
 * Created by Charan on May 16, 2020
 */
object KotUtil {

    @JvmStatic
    fun filterCurrency(@NonNull searchTerm: String, allCurrencies: ArrayList<Currency>?):
            ArrayList<Currency>? {
        return if (allCurrencies != null) {
            val searchTermInLower = searchTerm.toLowerCase()
            allCurrencies.filter { currency ->
                currency.currencyCode.toLowerCase(Locale.ENGLISH).contains(searchTermInLower) ||
                        currency.currencyName.toLowerCase(Locale.ENGLISH).contains(searchTermInLower) ||
                        currency.currencySymbol.toLowerCase(Locale.ENGLISH).contains(searchTermInLower)
            } as ArrayList<Currency>

        } else {
            null
        }
    }
}