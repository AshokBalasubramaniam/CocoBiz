const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true, trim: true, lowercase: true },
  passwordHash: { type: String, required: true },
  email: { type: String, trim: true, lowercase: true, default: '' },
  phone: { type: String, trim: true, default: '' },
  businessName: { type: String, default: '' },
  ownerName: { type: String, default: '' },
  reminderChannel: { type: String, enum: ['EMAIL', 'WHATSAPP', 'BOTH', 'NONE'], default: 'EMAIL' },
  reminderFrequency: { type: String, enum: ['HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY'], default: 'DAILY' },
  createdAt: { type: Number, default: Date.now },
  lastLoginAt: { type: Number }
});

module.exports = mongoose.model('User', userSchema);
