package io.github.freedomformyanmar.argus

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.squareup.moshi.Moshi
import io.github.freedomformyanmar.argus.user.User


class QrManager {

    companion object {
        private const val QR_WIDTH = 500
        private const val QR_HEIGHT = 500
    }

    private val moshi = Moshi.Builder().build()

    private val userJsonAdapter = moshi.adapter(User::class.java)

    fun encodeUserToString(user: User): String {
        return userJsonAdapter.toJson(user)
    }

    fun decodeUser(string: String): User {
        return userJsonAdapter.fromJson(string)!!
    }

    fun encodeBitmap(user: User): Bitmap {
        val width = QR_WIDTH
        val height = QR_HEIGHT
        val content = encodeUserToString(user)
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, width, height)
    }

    fun decodeBitmap(source: Bitmap): User {
        var decoded: String? = null

        val intArray = IntArray(source.width * source.height)
        source.getPixels(
            intArray, 0, source.width, 0, 0, source.width,
            source.height
        )
        val luminanceSource: LuminanceSource = RGBLuminanceSource(
            source.width,
            source.height, intArray
        )
        val bitmap = BinaryBitmap(HybridBinarizer(luminanceSource))

        val reader: Reader = QRCodeReader()
        try {
            val result: Result = reader.decode(bitmap)
            decoded = result.text
        } catch (e: NotFoundException) {
            e.printStackTrace()
        } catch (e: ChecksumException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }
        if (decoded == null) throw IllegalStateException()
        return decodeUser(decoded)
    }

}