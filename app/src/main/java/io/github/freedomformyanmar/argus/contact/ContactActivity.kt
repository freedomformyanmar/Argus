package io.github.freedomformyanmar.argus.contact

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.github.freedomformyanmar.argus.QrManager
import io.github.freedomformyanmar.argus.databinding.ActivityContactListBinding
import io.github.freedomformyanmar.argus.db.DbProvider
import io.github.freedomformyanmar.argus.helper.viewBinding
import io.github.freedomformyanmar.argus.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class ContactActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityContactListBinding::inflate)

    private val database by lazy {
        DbProvider.getInstance(this)
    }

    private val qrManager by lazy {
        QrManager()
    }

    private val contactAdapter by lazy {
        ContactAdapter(onDeleteClick = {
            database.contactTableQueries.deleteByNumber(it.number)
        })
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        lifecycleScope.launch {
            if (uri != null) {
                val bitmap = withContext(Dispatchers.Default) {
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            uri
                        )
                    } else {
                        val source = ImageDecoder.createSource(contentResolver, uri)
                        ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)
                    }
                }

                val user = qrManager.decodeBitmap(bitmap)
                database.contactTableQueries.insertOrReplace(user.number, user.secretCode)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.title = "ပေးပို့မည့် နံပါတ်စာရင်း"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            rvContact.layoutManager =
                LinearLayoutManager(this@ContactActivity, RecyclerView.VERTICAL, false)
            rvContact.adapter = contactAdapter

            fabCamera.setOnClickListener {
                IntentIntegrator(this@ContactActivity).initiateScan()
            }
            fabGallery.setOnClickListener {
                getImage.launch("image/*")
            }

            rvContact.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        fabGallery.hide()
                        fabCamera.hide()
                    } else {
                        fabGallery.show()
                        fabCamera.show()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }

        lifecycleScope.launch {
            database.contactTableQueries.getAll().asFlow().mapToList().collect { contactTableList ->
                binding.tvEmptyInstruction.isVisible = contactTableList.isEmpty()
                val userList = withContext(Dispatchers.Default) {
                    contactTableList.map { User(it.phoneNumber, it.secretCode) }
                }
                contactAdapter.submitList(userList)
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

    // Get the results:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                try {
                    val user = qrManager.decodeUser(result.contents)
                    database.contactTableQueries.insertOrReplace(user.number, user.secretCode)
                } catch (ioException: IOException) {
                    Toast.makeText(this, "Invalid QR", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}