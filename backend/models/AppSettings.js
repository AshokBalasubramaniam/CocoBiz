const mongoose = require('mongoose');

const appSettingsSchema = new mongoose.Schema({
  localId: { type: Number, default: 1, unique: true },
  reminderDays: { type: Number, default: 5 },
  notificationEnabled: { type: Boolean, default: true },
  emailEnabled: { type: Boolean, default: false },
  senderEmail: { type: String, default: '' },
  darkMode: { type: String, enum: ['LIGHT', 'DARK', 'SYSTEM'], default: 'SYSTEM' },
  backupEnabled: { type: Boolean, default: false },
  defaultCycleDays: { type: Number, default: 60 },
  updatedAt: { type: Number, default: Date.now }
});

module.exports = mongoose.model('AppSettings', appSettingsSchema);
