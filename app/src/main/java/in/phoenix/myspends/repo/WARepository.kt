package `in`.phoenix.myspends.repo

import `in`.phoenix.myspends.MySpends
import `in`.phoenix.myspends.database.AppDatabaseProvider
import `in`.phoenix.myspends.database.DBConstants
import `in`.phoenix.myspends.database.DBResponse
import `in`.phoenix.myspends.database.WAHistoryDao
import `in`.phoenix.myspends.model.WAEntity
import `in`.phoenix.myspends.util.AppLog
import `in`.phoenix.myspends.util.WhatsAppUtil
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Created by Charan on March 07, 2021
 */
object WARepository {

    private val waHistoryDao: WAHistoryDao by lazy {
        AppDatabaseProvider.getAppDatabase(MySpends.APP_CONTEXT).waHistoryDao()
    }

    fun getWhatsAppEntries(waEntries: MutableLiveData<DBResponse<List<WAEntity>>>): Disposable {
        return waHistoryDao.getCount()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { entryCount ->
                    AppLog.d("WARepository", "getWhatsAppEntries1:${Thread.currentThread().name}")
                    entryCount > 0
                }
                .map { canQueryForEntries ->
                    AppLog.d("WARepository", "getWhatsAppEntries2:${Thread.currentThread().name}")
                    if (canQueryForEntries) {
                        waHistoryDao.getAll()
                    }
                    else {
                        emptyList<WAEntity>()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    AppLog.d("WARepository", "getWhatsAppEntries3:${Thread.currentThread().name}")
                    waEntries.postValue(DBResponse.Success(data))
                }, {
                    AppLog.d("WARepository", "getWhatsAppEntries4:${Thread.currentThread().name}")
                    AppLog.d("WARepository", "getWhatsAppEntries", it)
                    waEntries.postValue(DBResponse.Failed(it.message!!))
                })
    }

    fun addEntry(code: String, mobileNumber: String,
                 waAddEntry: MutableLiveData<DBResponse<WAEntity>>): Disposable {
        val waEntity = WAEntity(code = code, number = mobileNumber)
        return waHistoryDao.findMatching(code, mobileNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { entryCount ->
                    entryCount == 0
                }
                .map { matchNotFound ->
                    if (matchNotFound) {
                        val count = waHistoryDao.getCount().blockingGet()
                        if (count < DBConstants.MAX_WA_ENTRY) {
                            waEntity.addedOn = WhatsAppUtil.entryTimeStamp()
                            waEntity.id = waHistoryDao.insert(waEntity)
                            waEntity.id
                        } else {
                            -100L
                        }
                    }
                    else {
                        -1L
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    when (data) {
                        -1L -> {
                            //waAddEntry.postValue(DBResponse.Failed("Already present."))
                            waAddEntry.postValue(DBResponse.Success(waEntity))
                        }
                        -100L -> {
                            //waAddEntry.postValue(DBResponse.Failed("Reached max entries."))
                            waAddEntry.postValue(DBResponse.Success(waEntity))
                        }
                        else -> {
                            waEntity.id = data
                            waAddEntry.postValue(DBResponse.Success(waEntity))
                        }
                    }
                }, {
                    waAddEntry.postValue(DBResponse.Failed(it.message!!))
                })
    }

    fun deleteEntry(waEntity: WAEntity, position: Int,
                    waDeleteEntry: MutableLiveData<DBResponse<Int>>): Disposable {
        return waHistoryDao.delete(waEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    when (data) {
                        0 -> {
                            waDeleteEntry.postValue(DBResponse.Failed("Could not delete."))
                        }
                        else -> {
                            waDeleteEntry.postValue(DBResponse.Success(position))
                        }
                    }
                }, {
                    waDeleteEntry.postValue(DBResponse.Failed(it.message!!))
                })
    }
}