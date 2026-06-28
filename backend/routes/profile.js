const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const UserProfile = require('../models/UserProfile');

router.use(auth);

router.get('/', async (req, res) => {
  try {
    const profile = await UserProfile.findOne({ userId: req.userId });
    if (!profile) return res.status(404).json({ error: 'Profile not found' });
    res.json(profile);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/', async (req, res) => {
  try {
    const profile = await UserProfile.findOneAndUpdate(
      { userId: req.userId },
      { ...req.body, userId: req.userId, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.json(profile);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

module.exports = router;
