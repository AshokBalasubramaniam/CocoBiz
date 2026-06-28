const mongoose = require('mongoose');

const dealerSchema = new mongoose.Schema({
  localId: { type: Number, required: true },
  userId: { type: String, required: true, index: true },
  dealerName: { type: String, required: true },
  place: { type: String, default: '' },
  phone: { type: String, default: '' },
  alternatePhone: { type: String, default: '' },
  email: { type: String, default: '' },
  address: { type: String, default: '' },
  notes: { type: String, default: '' },
  photoPath: { type: String, default: '' },
  createdAt: { type: Number, default: Date.now },
  updatedAt: { type: Number, default: Date.now }
});

dealerSchema.index({ localId: 1, userId: 1 }, { unique: true });
module.exports = mongoose.model('Dealer', dealerSchema);
