const express = require('express');
const router = express.Router();
const AppSettings = require('../models/AppSettings');

// GET settings
router.get('/', async (req, res) => {
  try {
    const settings = await AppSettings.findOne({ localId: 1 });
    if (!settings) return res.status(404).json({ error: 'Settings not found' });
    res.json(settings);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST save/update settings
router.post('/', async (req, res) => {
  try {
    const settings = await AppSettings.findOneAndUpdate(
      { localId: req.body.localId || 1 },
      { ...req.body, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.json(settings);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

module.exports = router;
