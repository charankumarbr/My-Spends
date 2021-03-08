package `in`.phoenix.myspends.ui.viewmodel

import `in`.phoenix.myspends.database.DBResponse
import `in`.phoenix.myspends.model.WAEntity
import `in`.phoenix.myspends.repo.WARepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Charan on March 07, 2021
 */
class WAViewModel: ViewModel() {

    private val _waEntries: MutableLiveData<DBResponse<List<WAEntity>>> by lazy {
        MutableLiveData<DBResponse<List<WAEntity>>>()
    }
    val observeWAEntries: LiveData<DBResponse<List<WAEntity>>> by lazy { _waEntries }

    fun getAllWAEntries() {
        WARepository.getWhatsAppEntries(_waEntries)
    }

    private val _waAddEntry: MutableLiveData<DBResponse<WAEntity>> by lazy {
        MutableLiveData<DBResponse<WAEntity>>()
    }
    val observeWAAddEntry: LiveData<DBResponse<WAEntity>> by lazy { _waAddEntry }

    fun saveWAEntry(code: String, mobileNumber: String) {
        WARepository.addEntry(code, mobileNumber, _waAddEntry)
    }

    private val _waDeleteEntry: MutableLiveData<DBResponse<Int>> by lazy {
        MutableLiveData<DBResponse<Int>>()
    }
    val observeWADeleteEntry: LiveData<DBResponse<Int>> by lazy { _waDeleteEntry }
    fun deleteEntry(waEntity: WAEntity, position: Int) {
        WARepository.deleteEntry(waEntity, position, _waDeleteEntry)
    }

    override fun onCleared() {
        super.onCleared()
        WARepository.dispose()
    }
}