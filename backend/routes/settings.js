const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const AppSettings = require('../models/AppSettings');

router.use(auth);

router.get('/', async (req, res) => {
  try {
    const settings = await AppSettings.findOne({ userId: req.userId });
    if (!settings) return res.status(404).json({ error: 'Settings not found' });
    res.json(settings);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

router.post('/', async (req, res) => {
  try {
    const settings = await AppSettings.findOneAndUpdate(
      { userId: req.userId },
      { ...req.body, userId: req.userId, updatedAt: Date.now() },
      { upsert: true, new: true, runValidators: true }
    );
    res.json(settings);
  } catch (err) { res.status(400).json({ error: err.message }); }
});

module.exports = router;
