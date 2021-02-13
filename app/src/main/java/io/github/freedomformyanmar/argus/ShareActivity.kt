package io.github.freedomformyanmar.argus

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.freedomformyanmar.argus.databinding.ActivityShareBinding
import io.github.freedomformyanmar.argus.helper.viewBinding
import io.github.freedomformyanmar.argus.user.UserCache
import kotlinx.coroutines.*

class ShareActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityShareBinding::inflate)

    private val userCache by lazy {
        UserCache(this)
    }

    private val qrManager by lazy {
        QrManager()
    }

    private var qrCodeBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            val user = userCache.getUser() ?: return@launch
            qrCodeBitmap = withContext(Dispatchers.Default) {
                qrManager.encodeBitmap(user)
            }
            binding.qrCode.setImageBitmap(qrCodeBitmap)
        }

        binding.buttonSaveGallery.setOnClickListener {
            saveImageToGallery()
        }
    }

    private fun saveImageToGallery() {
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                if (qrCodeBitmap != null)

                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    qrCodeBitmap,
                    "ArgusContact",
                    ""
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ShareActivity, "Saved to Gallery", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}