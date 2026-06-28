package com.cocobiz.app.data.remote.api

import com.cocobiz.app.data.remote.dto.DealerDto
import com.cocobiz.app.data.remote.dto.SalesEntryDto
import com.cocobiz.app.data.remote.dto.SettingsDto
import com.cocobiz.app.data.remote.dto.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CocoBizApiService {

    // Dealers
    @GET("api/dealers")
    suspend fun getAllDealers(): List<DealerDto>

    @POST("api/dealers")
    suspend fun upsertDealer(@Body dealer: DealerDto): DealerDto

    @PUT("api/dealers/{localId}")
    suspend fun updateDealer(@Path("localId") localId: Long, @Body dealer: DealerDto): DealerDto

    @DELETE("api/dealers/{localId}")
    suspend fun deleteDealer(@Path("localId") localId: Long)

    @POST("api/dealers/bulk")
    suspend fun bulkUpsertDealers(@Body body: Map<String, @JvmSuppressWildcards List<DealerDto>>): Map<String, String>

    // Sales
    @GET("api/sales")
    suspend fun getAllSales(): List<SalesEntryDto>

    @POST("api/sales")
    suspend fun upsertSale(@Body sale: SalesEntryDto): SalesEntryDto

    @PUT("api/sales/{localId}")
    suspend fun updateSale(@Path("localId") localId: Long, @Body sale: SalesEntryDto): SalesEntryDto

    @PATCH("api/sales/{localId}/complete")
    suspend fun markSaleCompleted(@Path("localId") localId: Long): SalesEntryDto

    @PATCH("api/sales/{localId}/activate")
    suspend fun markSaleActive(@Path("localId") localId: Long): SalesEntryDto

    @DELETE("api/sales/{localId}")
    suspend fun deleteSale(@Path("localId") localId: Long)

    @POST("api/sales/bulk")
    suspend fun bulkUpsertSales(@Body body: Map<String, @JvmSuppressWildcards List<SalesEntryDto>>): Map<String, String>

    // Profile
    @GET("api/profile")
    suspend fun getProfile(): UserProfileDto

    @POST("api/profile")
    suspend fun saveProfile(@Body profile: UserProfileDto): UserProfileDto

    // Settings
    @GET("api/settings")
    suspend fun getSettings(): SettingsDto

    @POST("api/settings")
    suspend fun saveSettings(@Body settings: SettingsDto): SettingsDto
}
