const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Otp = require('../models/Otp');
const { sendOtpEmail } = require('../services/emailService');

function generateOtp() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

function generateToken(userId, username) {
  return jwt.sign({ userId, username }, process.env.JWT_SECRET, { expiresIn: process.env.JWT_EXPIRY || '30d' });
}

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validatePhone(phone) {
  return /^[6-9]\d{9}$/.test(phone.replace(/\D/g, ''));
}

// POST /api/auth/register
router.post('/register', async (req, res) => {
  try {
    const { username, password, email, phone, businessName, ownerName } = req.body;

    if (!username || username.trim().length < 3)
      return res.status(400).json({ error: 'Username must be at least 3 characters' });
    if (!password || password.length < 6)
      return res.status(400).json({ error: 'Password must be at least 6 characters' });
    if (email && !validateEmail(email))
      return res.status(400).json({ error: 'Invalid email format' });
    if (phone && !validatePhone(phone))
      return res.status(400).json({ error: 'Invalid phone number (10 digits required)' });

    const existing = await User.findOne({ username: username.toLowerCase().trim() });
    if (existing) return res.status(409).json({ error: 'Username already taken' });

    const passwordHash = await bcrypt.hash(password, 12);
    const user = await User.create({
      username: username.toLowerCase().trim(),
      passwordHash, email: email || '', phone: phone || '',
      businessName: businessName || '', ownerName: ownerName || ''
    });

    const token = generateToken(user._id.toString(), user.username);
    res.status(201).json({
      token,
      user: { id: user._id, username: user.username, email: user.email, phone: user.phone, businessName: user.businessName, ownerName: user.ownerName }
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body;
    if (!username || !password)
      return res.status(400).json({ error: 'Username and password required' });

    const user = await User.findOne({ username: username.toLowerCase().trim() });
    if (!user) return res.status(401).json({ error: 'Invalid username or password' });

    const valid = await bcrypt.compare(password, user.passwordHash);
    if (!valid) return res.status(401).json({ error: 'Invalid username or password' });

    await User.findByIdAndUpdate(user._id, { lastLoginAt: Date.now() });
    const token = generateToken(user._id.toString(), user.username);
    res.json({
      token,
      user: { id: user._id, username: user.username, email: user.email, phone: user.phone, businessName: user.businessName, ownerName: user.ownerName, reminderChannel: user.reminderChannel, reminderFrequency: user.reminderFrequency }
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /api/auth/send-otp
router.post('/send-otp', async (req, res) => {
  try {
    const { identifier } = req.body; // email or phone
    if (!identifier) return res.status(400).json({ error: 'Email or phone required' });

    const user = await User.findOne({ $or: [{ email: identifier }, { phone: identifier }] });
    if (!user) return res.status(404).json({ error: 'No account found with this email/phone' });

    const otp = generateOtp();
    await Otp.deleteMany({ identifier });
    await Otp.create({ identifier, otp, expiresAt: new Date(Date.now() + 10 * 60 * 1000) });

    if (validateEmail(identifier)) {
      await sendOtpEmail(identifier, otp);
      res.json({ message: 'OTP sent to email', masked: identifier.replace(/(.{2}).+(@.+)/, '$1***$2') });
    } else {
      console.log(`OTP for ${identifier}: ${otp}`); // Log for SMS integration
      res.json({ message: 'OTP sent', masked: identifier.replace(/(\d{2})\d+(\d{2})/, '$1****$2') });
    }
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /api/auth/verify-otp
router.post('/verify-otp', async (req, res) => {
  try {
    const { identifier, otp, newPassword } = req.body;
    if (!identifier || !otp || !newPassword)
      return res.status(400).json({ error: 'All fields required' });
    if (newPassword.length < 6)
      return res.status(400).json({ error: 'Password must be at least 6 characters' });

    const record = await Otp.findOne({ identifier, otp });
    if (!record) return res.status(400).json({ error: 'Invalid or expired OTP' });
    if (record.expiresAt < new Date()) return res.status(400).json({ error: 'OTP expired' });

    const user = await User.findOne({ $or: [{ email: identifier }, { phone: identifier }] });
    if (!user) return res.status(404).json({ error: 'User not found' });

    const passwordHash = await bcrypt.hash(newPassword, 12);
    await User.findByIdAndUpdate(user._id, { passwordHash });
    await Otp.deleteMany({ identifier });

    const token = generateToken(user._id.toString(), user.username);
    res.json({ message: 'Password reset successful', token });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PATCH /api/auth/update-reminder  (protected)
const authMiddleware = require('../middleware/auth');
router.patch('/update-reminder', authMiddleware, async (req, res) => {
  try {
    const { reminderChannel, reminderFrequency } = req.body;
    const user = await User.findByIdAndUpdate(
      req.userId,
      { reminderChannel, reminderFrequency },
      { new: true }
    );
    res.json({ reminderChannel: user.reminderChannel, reminderFrequency: user.reminderFrequency });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// PATCH /api/auth/change-username (protected)
router.patch('/change-username', authMiddleware, async (req, res) => {
  try {
    const { newUsername, password } = req.body;
    if (!newUsername || newUsername.trim().length < 3)
      return res.status(400).json({ error: 'Username must be at least 3 characters' });

    const user = await User.findById(req.userId);
    const valid = await bcrypt.compare(password, user.passwordHash);
    if (!valid) return res.status(401).json({ error: 'Incorrect password' });

    const existing = await User.findOne({ username: newUsername.toLowerCase().trim() });
    if (existing) return res.status(409).json({ error: 'Username already taken' });

    await User.findByIdAndUpdate(req.userId, { username: newUsername.toLowerCase().trim() });
    res.json({ message: 'Username updated' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
