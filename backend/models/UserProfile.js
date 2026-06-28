const mongoose = require('mongoose');

const userProfileSchema = new mongoose.Schema({
  localId: { type: Number, default: 1, unique: true },
  businessName: { type: String, default: '' },
  ownerName: { type: String, default: '' },
  phone: { type: String, default: '' },
  alternatePhone: { type: String, default: '' },
  email: { type: String, default: '' },
  address: { type: String, default: '' },
  city: { type: String, default: '' },
  state: { type: String, default: '' },
  pincode: { type: String, default: '' },
  gstNumber: { type: String, default: '' },
  logoPath: { type: String, default: '' },
  createdAt: { type: Number, default: Date.now },
  updatedAt: { type: Number, default: Date.now }
});

module.exports = mongoose.model('UserProfile', userProfileSchema);
