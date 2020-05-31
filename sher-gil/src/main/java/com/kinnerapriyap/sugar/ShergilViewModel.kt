package com.kinnerapriyap.sugar

import android.app.Application
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellDisplayModel
import com.kinnerapriyap.sugar.mediagallery.cell.MediaCellUpdateModel
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryHandler
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ShergilViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val mediaGalleryHandler by lazy {
        MediaGalleryHandler(getApplication<Application>().contentResolver)
    }

    /**
     * Providing [Dispatchers.Main] in coroutineContext as default
     * to use [launch], which is an extension function of [CoroutineScope],
     * without specifying the thread each time
     * [Dispatchers.IO] or [Dispatchers.Default] may be used
     * when required to change the thread
     */
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val mediaCellDisplayModels =
        MutableLiveData<MutableList<MediaCellDisplayModel>>()
            .apply { value = mutableListOf() }

    fun getMediaCellDisplayModels(): LiveData<MutableList<MediaCellDisplayModel>> =
        mediaCellDisplayModels

    private val updatedMediaCellPosition =
        MutableLiveData<MediaCellUpdateModel>()
            .apply { value = MediaCellUpdateModel(-1, false) }

    fun getUpdatedMediaCellPosition(): LiveData<MediaCellUpdateModel> = updatedMediaCellPosition

    fun setMediaChecked(displayModel: MediaCellDisplayModel) {
        updatedMediaCellPosition.value =
            MediaCellUpdateModel(displayModel.position, !displayModel.isChecked)
        mediaCellDisplayModels.value =
            if (mediaCellDisplayModels.value?.contains(displayModel) == true) {
                mediaCellDisplayModels.value?.map {
                    val isChecked =
                        if (it == displayModel) {
                            !it.isChecked
                        } else it.isChecked
                    it.copy(isChecked = isChecked)
                }
            } else {
                val new = mediaCellDisplayModels.value
                new?.add(displayModel.copy(isChecked = !displayModel.isChecked))
                new
            }?.toMutableList()
    }

    fun fetchMediaCursor(bucketDisplayName: String? = null): Cursor? =
        if (bucketDisplayName.isNullOrBlank()) {
            mediaGalleryHandler.fetchMedia()
        } else {
            mediaGalleryHandler.fetchMediaByAlbum(bucketDisplayName.toString())
        }

    fun fetchAlbumCursor(): Cursor? = mediaGalleryHandler.fetchAlbum()

}