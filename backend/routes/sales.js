const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const SalesEntry = require('../models/SalesEntry');

router.use(auth);

router.get('/', async (req, res) => {
  try {
    const sales = await SalesEntry.find({ userId: req.userId }).sort({ updatedAt: -1 });
    res.json(sales);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/active', async (req, res) => {
  try {
    const sales = await SalesEntry.find({ userId: req.userId, status: 'ACTIVE' }).sort({ nextSalesDate: 1 });
    res.json(sales);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/completed', async (req, res) => {
  try {
    const sales = await SalesEntry.find({ userId: req.userId, status: 'COMPLETED' }).sort({ updatedAt: -1 });
    res.json(sales);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/dealer/:dealerId', async (req, res) => {
  try {
    const sales = await SalesEntry.find({ userId: req.userId, dealerId: parseInt(req.params.dealerId) }).sort({ updatedAt: -1 });
    res.json(sales);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/:localId', async (req, res) => {
  try {
    const sale = await SalesEntry.findOne({ localId: parseInt(req.params.localId), userId: req.userId });
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: req.body.localId, userId: req.userId },
      { ...req.body, userId: req.userId, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.status(201).json(sale);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

router.put('/:localId', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: parseInt(req.params.localId), userId: req.userId },
      { ...req.body, updatedAt: Date.now() },
      { new: true, runValidators: true }
    );
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

router.patch('/:localId/complete', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: parseInt(req.params.localId), userId: req.userId },
      { status: 'COMPLETED', updatedAt: Date.now() },
      { new: true }
    );
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

router.patch('/:localId/activate', async (req, res) => {
  try {
    const sale = await SalesEntry.findOneAndUpdate(
      { localId: parseInt(req.params.localId), userId: req.userId },
      { status: 'ACTIVE', updatedAt: Date.now() },
      { new: true }
    );
    if (!sale) return res.status(404).json({ error: 'Sale not found' });
    res.json(sale);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

router.delete('/:localId', async (req, res) => {
  try {
    await SalesEntry.findOneAndDelete({ localId: parseInt(req.params.localId), userId: req.userId });
    res.json({ message: 'Sale deleted' });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// Bulk upsert for backup restore
router.post('/bulk', async (req, res) => {
  try {
    const { sales } = req.body;
    const ops = sales.map(s => ({
      updateOne: {
        filter: { localId: s.localId, userId: req.userId },
        update: { ...s, userId: req.userId, updatedAt: Date.now() },
        upsert: true
      }
    }));
    await SalesEntry.bulkWrite(ops);
    res.json({ message: `${sales.length} sales synced` });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
