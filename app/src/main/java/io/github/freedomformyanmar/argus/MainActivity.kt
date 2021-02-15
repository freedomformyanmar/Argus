package io.github.freedomformyanmar.argus

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.aungkyawpaing.mmphonenumber.MyanmarPhoneNumberUtils
import com.aungkyawpaing.mmphonenumber.normalizer.MyanmarPhoneNumberNormalizer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.freedomformyanmar.argus.appupdate.AppUpdateManager
import io.github.freedomformyanmar.argus.contact.ContactActivity
import io.github.freedomformyanmar.argus.databinding.ActivityMainBinding
import io.github.freedomformyanmar.argus.db.DbProvider
import io.github.freedomformyanmar.argus.encoder.RandomString
import io.github.freedomformyanmar.argus.encoder.SmsEncoderDecoder
import io.github.freedomformyanmar.argus.helper.Intents
import io.github.freedomformyanmar.argus.helper.viewBinding
import io.github.freedomformyanmar.argus.user.User
import io.github.freedomformyanmar.argus.user.UserCache
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.time.Instant


class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val userCache by lazy {
        UserCache(this)
    }

    private val database by lazy {
        DbProvider.getInstance(this)
    }

    @SuppressLint("MissingPermission")
    private val readPhoneStateContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                showPhoneNumberSelection()
            } else {
                showPhoneNumberInput()
            }
        }

    private val requestSmsBroadcastContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted.not()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("SMS Permission လိုအပ်ပါသည်")
                    .setMessage("Settings ထဲမှာ permission ပေးပေးပါ")
                    .setPositiveButton("OK") { _, _ -> }
                    .setOnDismissListener {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkIfLoggedIn()
        requestSmsBroadcastContract.launch(Manifest.permission.RECEIVE_SMS)

        val myServiceIntent = Intent(this, KeepAliveService::class.java)
        ContextCompat.startForegroundService(this, myServiceIntent)


        binding.apply {
            buttonAlert.setOnClickListener {
                sendSms()
            }
            fabShare.setOnClickListener {
                startActivity(Intent(this@MainActivity, ShareActivity::class.java))
            }
            fabManageContact.setOnClickListener {
                startActivity(Intent(this@MainActivity, ContactActivity::class.java))
            }
        }

        launchAppUpdate()
    }

    private val requestSendSmsContract =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            lifecycleScope.launch {
                val allContacts = database.contactTableQueries.getAll().executeAsList()
                val smsManager = SmsManager.getDefault()
                val instant = Instant.now()
                allContacts.forEach {
                    val encodedString = SmsEncoderDecoder.encodeSos(it.secretCode, instant)
                    try {
                        smsManager.sendTextMessage(it.phoneNumber, null, encodedString, null, null)
                    } catch (exception : Exception) {
                        Timber.e(exception)
                    }
                }

                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle("ပို့ပြီးပါပြီ")
                    .setMessage("သင်တတ်နိုင်သလောက် အချိန်ဆွဲထားပါ")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }

    private fun sendSms() {
        binding.buttonAlert.playAnimation()
        requestSendSmsContract.launch(Manifest.permission.SEND_SMS)
    }

    private fun checkIfLoggedIn() {
        lifecycleScope.launch {
            userCache.userFlow().collect { user ->
                if (user == null) {
                    readPhoneStateContract.launch(Manifest.permission.READ_PHONE_STATE)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showPhoneNumberSelection() {
        val subscriptionManager =
            this.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val validSubscriptionInfo = subscriptionManager.activeSubscriptionInfoList.filter {
            it.number.isNotBlank()
        }

        val possibleNumbers = validSubscriptionInfo.map {
            it.number
        }
        val selectionItems = validSubscriptionInfo.map {
            "${it.carrierName} - ${it.number}"
        }.toTypedArray().plus("တစ်ခြားနံပါတ်")

        if (possibleNumbers.isEmpty()) {
            showPhoneNumberInput()
            return
        }

        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle("အသုံးပြုမည့် ဖုန်းနံပါတ်ရွေးချယ်ပေးပါ")
            .setItems(selectionItems) { dialog, which ->
                if (which == selectionItems.lastIndex) {
                    showPhoneNumberInput()
                } else {
                    processNumber(possibleNumbers[which])
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun showPhoneNumberInput() {
        val inputEditText = EditText(this@MainActivity).apply {
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams = lp
            inputType = InputType.TYPE_CLASS_PHONE
        }


        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle("ယခုဖုန်း၏ ဖုန်းနံပါတ် ရိုက်ထည့်ပေးပါ (တစ်ခြားဖုန်းတစ်လုံးရှိ နံပါတ်မရပါ)")
            .setView(inputEditText)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                val inputNumber = inputEditText.text.toString()
                if (MyanmarPhoneNumberUtils.isValidMyanmarPhoneNumber(inputNumber))
                    processNumber(inputNumber)
                else showPhoneNumberInput()
            }
            .show().also {
                it.setCancelable(false)
                it.setCanceledOnTouchOutside(false)
            }
    }

    private fun processNumber(number: String) {
        val normalizedNumber = MyanmarPhoneNumberNormalizer().normalize(number)
        val secretCode = RandomString().nextString()
        lifecycleScope.launch {
            userCache.saveUser(
                User(
                    number = normalizedNumber,
                    secretCode = secretCode
                )
            )
        }
    }

    private val appUpdateManager by lazy {
        AppUpdateManager(this)
    }

    private var hasRelaxedUpdateShownBefore = false

    private fun launchAppUpdate() {
        lifecycleScope.launch {
            when (val appUpdateResult = appUpdateManager.checkForUpdate()) {
                is AppUpdateManager.UpdateResult.ForcedUpdate -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(R.string.update_required)
                        .setMessage(R.string.update_required_message)
                        .setPositiveButton(R.string.do_update) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            kotlin.runCatching {
                                startActivity(Intents.viewUrl(appUpdateResult.updateLink))
                            }
                            finish()
                        }
                        .create()
                        .also { dialog ->
                            dialog.setCancelable(false)
                            dialog.setCanceledOnTouchOutside(false)
                            dialog.setOnShowListener {
                                val positiveButton =
                                    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                positiveButton.setTextColor(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        R.color.secondaryColor
                                    )
                                )
                            }
                        }
                        .show()
                }
                is AppUpdateManager.UpdateResult.RelaxedUpdate -> {
                    if (!hasRelaxedUpdateShownBefore) {
                        val relaxedUpdateSheet = RelaxedAppUpdateBottomSheet()
                        relaxedUpdateSheet.onOkayClick = {
                            startActivity(Intents.viewUrl(appUpdateResult.updateLink))
                            relaxedUpdateSheet.dismiss()
                        }
                        relaxedUpdateSheet.onCancelClick = {
                            relaxedUpdateSheet.dismiss()
                        }
                        relaxedUpdateSheet.show(supportFragmentManager, "Relaxed_Update")
                        hasRelaxedUpdateShownBefore = true
                    }
                }
            }
        }
    }
}