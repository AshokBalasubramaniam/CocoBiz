const cron = require('node-cron');
const User = require('../models/User');
const SalesEntry = require('../models/SalesEntry');
const { sendReminderEmail } = require('./emailService');

const lastSentMap = new Map(); // userId_frequency -> lastSentTimestamp

function shouldSendNow(userId, frequency) {
  const key = `${userId}_${frequency}`;
  const last = lastSentMap.get(key) || 0;
  const now = Date.now();
  const intervals = {
    HOURLY: 60 * 60 * 1000,
    DAILY: 24 * 60 * 60 * 1000,
    WEEKLY: 7 * 24 * 60 * 60 * 1000,
    MONTHLY: 30 * 24 * 60 * 60 * 1000
  };
  return now - last >= (intervals[frequency] || intervals.DAILY);
}

async function sendWhatsApp(phone, message) {
  if (!process.env.TWILIO_ACCOUNT_SID || !process.env.TWILIO_AUTH_TOKEN) return;
  try {
    const twilio = require('twilio');
    const client = twilio(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);
    await client.messages.create({
      from: process.env.TWILIO_WHATSAPP_FROM,
      to: `whatsapp:+91${phone.replace(/\D/g, '')}`,
      body: message
    });
  } catch (err) {
    console.error('WhatsApp send error:', err.message);
  }
}

function startReminderScheduler() {
  // Run every hour
  cron.schedule('0 * * * *', async () => {
    try {
      const users = await User.find({ reminderChannel: { $ne: 'NONE' } });
      const nowMs = Date.now();
      const sevenDaysMs = 7 * 24 * 60 * 60 * 1000;

      for (const user of users) {
        if (!shouldSendNow(user._id.toString(), user.reminderFrequency)) continue;

        const upcomingSales = await SalesEntry.find({
          userId: user._id.toString(),
          status: 'ACTIVE',
          nextSalesDate: { $lte: nowMs + sevenDaysMs }
        });

        if (upcomingSales.length === 0) continue;

        const salesList = upcomingSales.map(s => ({
          dealerName: s.dealerName,
          dealerPlace: s.dealerPlace,
          totalAmount: s.totalAmount,
          remainingDays: Math.max(0, Math.ceil((s.nextSalesDate - nowMs) / (24 * 60 * 60 * 1000)))
        }));

        const channel = user.reminderChannel;

        if ((channel === 'EMAIL' || channel === 'BOTH') && user.email) {
          try {
            await sendReminderEmail(user.email, salesList, user.businessName || user.username);
          } catch (e) { console.error('Email reminder error:', e.message); }
        }

        if ((channel === 'WHATSAPP' || channel === 'BOTH') && user.phone) {
          const msg = `🌴 CocoBiz Reminder\n${salesList.length} sale(s) due soon:\n` +
            salesList.map(s => `• ${s.dealerName} (${s.dealerPlace}) - ${s.remainingDays} days - ₹${s.totalAmount}`).join('\n');
          await sendWhatsApp(user.phone, msg);
        }

        lastSentMap.set(`${user._id}_${user.reminderFrequency}`, Date.now());
      }
    } catch (err) {
      console.error('Reminder scheduler error:', err.message);
    }
  });

  console.log('Reminder scheduler started');
}

module.exports = { startReminderScheduler };
