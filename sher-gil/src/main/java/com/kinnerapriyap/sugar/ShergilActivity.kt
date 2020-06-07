package com.kinnerapriyap.sugar

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryFragment
import com.kinnerapriyap.sugar.mediagallery.MediaGalleryFragmentListener
import com.kinnerapriyap.sugar.mediagallery.album.MediaGalleryAlbumCursorAdapter
import com.kinnerapriyap.sugar.resultlauncher.ResultLauncherHandler
import kotlinx.android.synthetic.main.activity_shergil.*
import java.util.ArrayList

internal class ShergilActivity :
    AppCompatActivity(),
    AdapterView.OnItemSelectedListener,
    MediaGalleryFragmentListener {

    private lateinit var observer: ResultLauncherHandler

    private val viewModel: ShergilViewModel by viewModels()

    private lateinit var mediaGalleryAlbumCursorAdapter: MediaGalleryAlbumCursorAdapter

    companion object {
        const val RESULT_URIS = "resultUris"
        private const val MEDIA_GALLERY_FRAGMENT_TAG = "mediaGalleryFragmentTag"
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is MediaGalleryFragment) {
            fragment.setMediaGalleryFragmentListener(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Shergil)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shergil)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        observer = ResultLauncherHandler(this, ::setPermissionResult)

        applyButton.setOnClickListener { setShergilResult() }
        askPermissionAndOpenGallery()
    }

    private fun askPermissionAndOpenGallery() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ->
                observer.askPermission()
            else -> {
                openMediaGalleryFragment()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun setShergilResult() {
        val resultIntent =
            Intent().apply {
                putParcelableArrayListExtra(
                    RESULT_URIS,
                    viewModel.getSelectedMediaUriList() as? ArrayList
                )
            }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun setPermissionResult(allowed: Boolean) {
        if (allowed) {
            openMediaGalleryFragment()
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun setToolbarSpinner() {
        mediaGalleryAlbumCursorAdapter =
            MediaGalleryAlbumCursorAdapter(this, viewModel.fetchAlbumCursor())
                .also { adapter ->
                    adapter.setDropDownViewResource(R.layout.album_spinner_item)
                }
        albumSpinner.adapter = mediaGalleryAlbumCursorAdapter
        albumSpinner.onItemSelectedListener = this
    }

    private fun openMediaGalleryFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                MediaGalleryFragment.newInstance(),
                MEDIA_GALLERY_FRAGMENT_TAG
            )
            .commit()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        setSelectedSpinnerName(null)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cursor = mediaGalleryAlbumCursorAdapter.getItem(position) as? Cursor
        val bucketDisplayName =
            mediaGalleryAlbumCursorAdapter.convertToString(cursor).toString()
        setSelectedSpinnerName(bucketDisplayName)
    }

    private fun setSelectedSpinnerName(bucketDisplayName: String?) {
        val mediaGalleryFragment: MediaGalleryFragment? =
            supportFragmentManager.findFragmentByTag(MEDIA_GALLERY_FRAGMENT_TAG) as? MediaGalleryFragment
        mediaGalleryFragment?.setSelectedSpinnerName(bucketDisplayName)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}
