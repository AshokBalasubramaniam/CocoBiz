const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const Dealer = require('../models/Dealer');

router.use(auth);

router.get('/', async (req, res) => {
  try {
    const dealers = await Dealer.find({ userId: req.userId }).sort({ updatedAt: -1 });
    res.json(dealers);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.get('/:localId', async (req, res) => {
  try {
    const dealer = await Dealer.findOne({ localId: parseInt(req.params.localId), userId: req.userId });
    if (!dealer) return res.status(404).json({ error: 'Dealer not found' });
    res.json(dealer);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/', async (req, res) => {
  try {
    const dealer = await Dealer.findOneAndUpdate(
      { localId: req.body.localId, userId: req.userId },
      { ...req.body, userId: req.userId, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.status(201).json(dealer);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

router.put('/:localId', async (req, res) => {
  try {
    const dealer = await Dealer.findOneAndUpdate(
      { localId: parseInt(req.params.localId), userId: req.userId },
      { ...req.body, updatedAt: Date.now() },
      { new: true, runValidators: true }
    );
    if (!dealer) return res.status(404).json({ error: 'Dealer not found' });
    res.json(dealer);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

router.delete('/:localId', async (req, res) => {
  try {
    await Dealer.findOneAndDelete({ localId: parseInt(req.params.localId), userId: req.userId });
    res.json({ message: 'Dealer deleted' });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// Bulk upsert for backup restore
router.post('/bulk', async (req, res) => {
  try {
    const { dealers } = req.body;
    const ops = dealers.map(d => ({
      updateOne: {
        filter: { localId: d.localId, userId: req.userId },
        update: { ...d, userId: req.userId, updatedAt: Date.now() },
        upsert: true
      }
    }));
    await Dealer.bulkWrite(ops);
    res.json({ message: `${dealers.length} dealers synced` });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

module.exports = router;
