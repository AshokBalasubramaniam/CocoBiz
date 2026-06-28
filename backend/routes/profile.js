const express = require('express');
const router = express.Router();
const UserProfile = require('../models/UserProfile');

// GET profile
router.get('/', async (req, res) => {
  try {
    const profile = await UserProfile.findOne({ localId: 1 });
    if (!profile) return res.status(404).json({ error: 'Profile not found' });
    res.json(profile);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST save/update profile
router.post('/', async (req, res) => {
  try {
    const profile = await UserProfile.findOneAndUpdate(
      { localId: req.body.localId || 1 },
      { ...req.body, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.json(profile);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});

module.exports = router;
