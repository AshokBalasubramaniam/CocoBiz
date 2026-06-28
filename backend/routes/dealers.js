const express = require('express');
const router = express.Router();
const Dealer = require('../models/Dealer');

// GET all dealers
router.get('/', async (req, res) => {
  try {
    const dealers = await Dealer.find().sort({ updatedAt: -1 });
    res.json(dealers);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET dealer by localId
router.get('/:localId', async (req, res) => {
  try {
    const dealer = await Dealer.findOne({ localId: parseInt(req.params.localId) });
    if (!dealer) return res.status(404).json({ error: 'Dealer not found' });
    res.json(dealer);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST create dealer
router.post('/', async (req, res) => {
  try {
    const dealer = await Dealer.findOneAndUpdate(
      { localId: req.body.localId },
      { ...req.body, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.status(201).json(dealer);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// PUT update dealer
router.put('/:localId', async (req, res) => {
  try {
    const dealer = await Dealer.findOneAndUpdate(
      { localId: parseInt(req.params.localId) },
      { ...req.body, updatedAt: Date.now() },
      { new: true, runValidators: true }
    );
    if (!dealer) return res.status(404).json({ error: 'Dealer not found' });
    res.json(dealer);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

// DELETE dealer
router.delete('/:localId', async (req, res) => {
  try {
    await Dealer.findOneAndDelete({ localId: parseInt(req.params.localId) });
    res.json({ message: 'Dealer deleted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
