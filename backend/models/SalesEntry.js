const mongoose = require('mongoose');

const salesEntrySchema = new mongoose.Schema({
  localId: { type: Number, required: true, unique: true },
  dealerId: { type: Number, required: true },
  dealerName: { type: String, default: '' },
  dealerPlace: { type: String, default: '' },
  salesDate: { type: Number, required: true },
  nextSalesDate: { type: Number, required: true },
  quantity: { type: Number, default: 0 },
  rate: { type: Number, default: 0 },
  totalAmount: { type: Number, default: 0 },
  coconutType: { type: String, enum: ['TONNAGE', 'SINGLE_PIECE'], default: 'TONNAGE' },
  cycleDays: { type: Number, default: 60 },
  status: { type: String, enum: ['ACTIVE', 'COMPLETED'], default: 'ACTIVE' },
  notes: { type: String, default: '' },
  createdAt: { type: Number, default: Date.now },
  updatedAt: { type: Number, default: Date.now }
});

module.exports = mongoose.model('SalesEntry', salesEntrySchema);
