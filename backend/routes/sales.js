const express = require('express');
const router = express.Router();
const SalesEntry = require('../models/SalesEntry');

// GET all sales
router.get('/', async (req, res) => {
  try {
    const sales = await SalesEntry.find().sort({ updatedAt: -1 });
    res.json(sales);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET active sales
router.get('/active', async (req, res) => {
  try {
    const sales = await SalesEntry.find({ status: 'ACTIVE' }).sort({ nextSalesDate: 1 });
    res.json(sales);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET completed sales
router.get('/completed', async (req, res) => {
  try {
    const sales = await SalesEntry.find({ status: 'COMPLETED' }).sort({ updatedAt: -1 });
    res.json(sales);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET sales by dealer
router.get('/dealer/:dealerId', async (req, res) => {
  try {
    const sales = await SalesEntry.find({ dealerId: parseInt(req.params.dealerId) }).sort({ updatedAt: -1 });
    res.json(sales);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET sale by localId
router.get('/:localId', async (req, res) => {
  try {
    const sale = await SalesEntry.findOne({ localId: parseInt(req.params.localId) });
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST create/upsert sale
router.post('/', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: req.body.localId },
      { ...req.body, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.status(201).json(sale);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// PUT update sale
router.put('/:localId', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: parseInt(req.params.localId) },
      { ...req.body, updatedAt: Date.now() },
      { new: true, runValidators: true }
    );
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// PATCH mark completed
router.patch('/:localId/complete', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: parseInt(req.params.localId) },
      { status: 'COMPLETED', updatedAt: Date.now() },
      { new: true }
    );
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// PATCH mark active
router.patch('/:localId/activate', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: parseInt(req.params.localId) },
      { status: 'ACTIVE', updatedAt: Date.now() },
      { new: true }
    );
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// DELETE sale
router.delete('/:localId', async (req, res) => {
  try {
    await SalesEntry.findOneAndDelete({ localId: parseInt(req.params.localId) });
    res.json({ message: 'Sale deleted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
