package com.kinnerapriyap.sample

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kinnerapriyap.sugar.Shergil
import com.kinnerapriyap.sugar.choice.MimeType

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "sample"

        val getIntent = registerForActivityResult(Shergil.create()
            .mimeTypes(MimeType.IMAGES)
            .showDisallowedMimeTypes(true)
            .allowOnlyLocalStorage(false)
            .numOfColumns(3)
            .theme(R.style.Shergil)
            .allowPreview(true)
            .maxSelectable(3)
            .forActivityContract()
        ) { uris: List<Uri> ->
            findViewById<TextView>(R.id.hello).text =
                uris.toString().replace(", ", "\n")
        }

        findViewById<TextView>(R.id.hello).setOnClickListener {
            getIntent.launch(null)
        }
    }
}
