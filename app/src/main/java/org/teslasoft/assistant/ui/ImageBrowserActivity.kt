/**************************************************************************
 * Copyright (c) 2023 Dmytro Ostapenko. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************/

package org.teslasoft.assistant.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.teslasoft.assistant.R
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Base64

class ImageBrowserActivity : FragmentActivity() {

    private var image: ImageView? = null
    private var btnDownload: FloatingActionButton? = null
    private var fileContents: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_imageview)

        window.navigationBarColor = 0xFF000000.toInt()
        window.statusBarColor = 0xFF000000.toInt()

        image = findViewById(R.id.image)
        btnDownload = findViewById(R.id.btn_download)

        val attacher = PhotoViewAttacher(image)
        attacher.update()

        val b: Bundle? = intent.extras

        if (b != null) {
            CoroutineScope(Dispatchers.Main).launch {
                load()
            }
        } else {
            finish()
        }
    }

    private fun load() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("tmp", MODE_PRIVATE)
        val url: String? = sharedPreferences.getString("tmp", null)

        if (url != null) {
            Glide.with(this).load(Uri.parse(url)).into(image!!)

            btnDownload?.setOnClickListener {
                val fileEncoded = url.replace("data:image/png;base64,", "")
                fileContents = Base64.getDecoder().decode(fileEncoded)

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/png"
                    putExtra(Intent.EXTRA_TITLE, "exported.png")
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/storage/emulated/0/Pictures/SpeakGPT/exported.png"))
                }
                fileSaveIntentLauncher.launch(intent)
            }
        } else {
            finish()
        }
    }

    private val fileSaveIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    writeToFile(uri)
                }
            }
        }
    }

    private fun writeToFile(uri: Uri) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    it.write(
                        fileContents
                    )
                }
            }
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: IOException) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}