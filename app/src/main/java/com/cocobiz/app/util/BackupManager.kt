package com.cocobiz.app.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.cocobiz.app.data.remote.api.CocoBizApiService
import com.cocobiz.app.data.remote.dto.DealerDto
import com.cocobiz.app.data.remote.dto.SalesEntryDto
import com.cocobiz.app.data.remote.dto.UserProfileDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

sealed class BackupResult {
    data class Success(val details: String) : BackupResult()
    data class Error(val message: String) : BackupResult()
}

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: CocoBizApiService
) {
    companion object {
        const val BACKUP_FOLDER = "CocoBiz"
        private const val BACKUP_EXT = ".cocobak"
        private const val FORMAT_VERSION = 2
        private const val PASSPHRASE = "CocoBiz@2024#Secure!Backup"
        private const val IV_LEN = 12
        private val MAGIC = byteArrayOf(0x43, 0x4F, 0x43, 0x4F, 0x42, 0x41, 0x4B, 0x31)
        private val AES_KEY: SecretKeySpec by lazy {
            val bytes = MessageDigest.getInstance("SHA-256")
                .digest(PASSPHRASE.toByteArray(Charsets.UTF_8))
            SecretKeySpec(bytes, "AES")
        }
    }

    suspend fun backup(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val dealers = api.getAllDealers()
            val sales = api.getAllSales()
            val profile = runCatching { api.getProfile() }.getOrNull()

            val plaintext = buildJson(dealers, sales, profile).toByteArray(Charsets.UTF_8)
            val encrypted = encrypt(plaintext)

            val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "CocoBiz_Backup_$ts$BACKUP_EXT"
            saveToDownloads(fileName, encrypted)

            BackupResult.Success("Saved as $fileName\nin Downloads/$BACKUP_FOLDER\n(${dealers.size} dealers, ${sales.size} sales)")
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Backup failed")
        }
    }

    suspend fun restore(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            val encrypted = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@withContext BackupResult.Error("Cannot read the backup file")

            val json = String(decrypt(encrypted), Charsets.UTF_8)
            val root = JSONObject(json)

            val fileVersion = root.optInt("version", 1)
            if (fileVersion > FORMAT_VERSION) {
                return@withContext BackupResult.Error("This backup was created with a newer app version")
            }

            var dealerCount = 0
            var salesCount = 0

            root.optJSONArray("dealers")?.let { arr ->
                dealerCount = arr.length()
                val dtos = (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    DealerDto(
                        localId = o.getLong("id"),
                        dealerName = o.getString("dealerName"),
                        place = o.getString("place"),
                        phone = o.getString("phone"),
                        alternatePhone = o.optString("alternatePhone", ""),
                        email = o.optString("email", ""),
                        address = o.optString("address", ""),
                        notes = o.optString("notes", ""),
                        photoPath = o.optString("photoPath", ""),
                        createdAt = o.getLong("createdAt"),
                        updatedAt = o.getLong("updatedAt")
                    )
                }
                dtos.forEach { runCatching { api.upsertDealer(it) } }
            }

            root.optJSONArray("sales")?.let { arr ->
                salesCount = arr.length()
                val dtos = (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    SalesEntryDto(
                        localId = o.getLong("id"),
                        dealerId = o.getLong("dealerId"),
                        dealerName = o.getString("dealerName"),
                        dealerPlace = o.getString("dealerPlace"),
                        salesDate = o.getLong("salesDate"),
                        nextSalesDate = o.getLong("nextSalesDate"),
                        quantity = o.getDouble("quantity"),
                        rate = o.getDouble("rate"),
                        totalAmount = o.getDouble("totalAmount"),
                        coconutType = o.getString("coconutType"),
                        cycleDays = o.optInt("cycleDays", 60),
                        status = o.optString("status", "ACTIVE"),
                        notes = o.optString("notes", ""),
                        createdAt = o.getLong("createdAt"),
                        updatedAt = o.getLong("updatedAt")
                    )
                }
                if (dtos.isNotEmpty()) {
                    runCatching { api.bulkUpsertSales(mapOf("sales" to dtos)) }
                }
            }

            root.optJSONObject("profile")?.let { o ->
                val profileDto = UserProfileDto(
                    localId = o.getLong("id"),
                    businessName = o.optString("businessName", ""),
                    ownerName = o.optString("ownerName", ""),
                    phone = o.optString("phone", ""),
                    alternatePhone = o.optString("alternatePhone", ""),
                    email = o.optString("email", ""),
                    address = o.optString("address", ""),
                    city = o.optString("city", ""),
                    state = o.optString("state", ""),
                    pincode = o.optString("pincode", ""),
                    gstNumber = o.optString("gstNumber", ""),
                    logoPath = o.optString("logoPath", ""),
                    createdAt = o.getLong("createdAt"),
                    updatedAt = o.getLong("updatedAt")
                )
                runCatching { api.saveProfile(profileDto) }
            }

            BackupResult.Success("Restore complete!\n$dealerCount dealers, $salesCount sales synced to cloud.")
        } catch (e: Exception) {
            BackupResult.Error("Restore failed: ${e.message}")
        }
    }

    suspend fun resetAllData(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val sales = runCatching { api.getAllSales() }.getOrElse { emptyList() }
            sales.forEach { runCatching { api.deleteSale(it.localId) } }
            val dealers = runCatching { api.getAllDealers() }.getOrElse { emptyList() }
            dealers.forEach { runCatching { api.deleteDealer(it.localId) } }
            BackupResult.Success("All data deleted successfully")
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Reset failed")
        }
    }

    private fun buildJson(
        dealers: List<DealerDto>,
        sales: List<SalesEntryDto>,
        profile: UserProfileDto?
    ): String {
        val root = JSONObject()
        root.put("version", FORMAT_VERSION)
        root.put("app", "CocoBiz")
        root.put("createdAt", System.currentTimeMillis())

        val dealersArr = JSONArray()
        dealers.forEach { d ->
            dealersArr.put(JSONObject().apply {
                put("id", d.localId)
                put("dealerName", d.dealerName)
                put("place", d.place)
                put("phone", d.phone)
                put("alternatePhone", d.alternatePhone)
                put("email", d.email)
                put("address", d.address)
                put("notes", d.notes)
                put("photoPath", d.photoPath)
                put("createdAt", d.createdAt)
                put("updatedAt", d.updatedAt)
            })
        }
        root.put("dealers", dealersArr)

        val salesArr = JSONArray()
        sales.forEach { s ->
            salesArr.put(JSONObject().apply {
                put("id", s.localId)
                put("dealerId", s.dealerId)
                put("dealerName", s.dealerName)
                put("dealerPlace", s.dealerPlace)
                put("salesDate", s.salesDate)
                put("nextSalesDate", s.nextSalesDate)
                put("quantity", s.quantity)
                put("rate", s.rate)
                put("totalAmount", s.totalAmount)
                put("coconutType", s.coconutType)
                put("cycleDays", s.cycleDays)
                put("status", s.status)
                put("notes", s.notes)
                put("createdAt", s.createdAt)
                put("updatedAt", s.updatedAt)
            })
        }
        root.put("sales", salesArr)

        if (profile != null) {
            root.put("profile", JSONObject().apply {
                put("id", profile.localId)
                put("businessName", profile.businessName)
                put("ownerName", profile.ownerName)
                put("phone", profile.phone)
                put("alternatePhone", profile.alternatePhone)
                put("email", profile.email)
                put("address", profile.address)
                put("city", profile.city)
                put("state", profile.state)
                put("pincode", profile.pincode)
                put("gstNumber", profile.gstNumber)
                put("logoPath", profile.logoPath)
                put("createdAt", profile.createdAt)
                put("updatedAt", profile.updatedAt)
            })
        }

        return root.toString()
    }

    private fun encrypt(data: ByteArray): ByteArray {
        val iv = ByteArray(IV_LEN).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, AES_KEY, GCMParameterSpec(128, iv))
        val ciphertext = cipher.doFinal(data)
        return MAGIC + iv + ciphertext
    }

    private fun decrypt(data: ByteArray): ByteArray {
        val minSize = MAGIC.size + IV_LEN + 16
        if (data.size < minSize)
            throw IllegalArgumentException("File too small — not a CocoBiz backup")
        val magic = data.copyOfRange(0, MAGIC.size)
        if (!magic.contentEquals(MAGIC))
            throw IllegalArgumentException("Wrong file type — please choose a .cocobak file")
        val iv = data.copyOfRange(MAGIC.size, MAGIC.size + IV_LEN)
        val ciphertext = data.copyOfRange(MAGIC.size + IV_LEN, data.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, AES_KEY, GCMParameterSpec(128, iv))
        return cipher.doFinal(ciphertext)
    }

    @Suppress("DEPRECATION")
    private fun saveToDownloads(fileName: String, data: ByteArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download/$BACKUP_FOLDER")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
            ) ?: throw IOException("Cannot create file in Downloads/$BACKUP_FOLDER")
            context.contentResolver.openOutputStream(uri)?.use { it.write(data) }
                ?: throw IOException("Cannot write backup data")
        } else {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                BACKUP_FOLDER
            ).also { it.mkdirs() }
            File(dir, fileName).writeBytes(data)
        }
    }
}
