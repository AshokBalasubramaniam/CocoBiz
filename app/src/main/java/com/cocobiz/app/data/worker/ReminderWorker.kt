package com.cocobiz.app.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cocobiz.app.domain.repository.SalesRepository
import com.cocobiz.app.domain.repository.SettingsRepository
import com.cocobiz.app.util.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val salesRepository: SalesRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val settings = settingsRepository.getSettings().first()

            if (!settings.notificationEnabled) return Result.success()

            val reminderDays = settings.reminderDays.toLong()
            val upcomingSales = salesRepository.getUpcomingSales(reminderDays).first()

            upcomingSales.forEachIndexed { index, sale ->
                NotificationUtils.showReminderNotification(
                    context = applicationContext,
                    notificationId = (sale.id + 1000).toInt(),
                    dealerName = sale.dealerName,
                    daysRemaining = sale.remainingDays
                )
            }

            salesRepository.checkAndAutoCompleteSales()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "CocoBizReminderWork"
    }
}
