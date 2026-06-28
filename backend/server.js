require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const { startReminderScheduler } = require('./services/reminderScheduler');

const authRouter = require('./routes/auth');
const dealersRouter = require('./routes/dealers');
const salesRouter = require('./routes/sales');
const profileRouter = require('./routes/profile');
const settingsRouter = require('./routes/settings');

const app = express();
const PORT = process.env.PORT || 4000;

app.use(cors());
app.use(express.json({ limit: '10mb' }));

mongoose.connect(process.env.MONGODB_URI)
  .then(() => {
    console.log('Connected to MongoDB Atlas - CocoBiz');
    startReminderScheduler();
  })
  .catch(err => { console.error('MongoDB connection error:', err.message); process.exit(1); });

app.use('/api/auth', authRouter);
app.use('/api/dealers', dealersRouter);
app.use('/api/sales', salesRouter);
app.use('/api/profile', profileRouter);
app.use('/api/settings', settingsRouter);

app.get('/health', (req, res) => res.json({
  status: 'ok',
  db: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected'
}));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`CocoBiz backend running on port ${PORT}`);
});
