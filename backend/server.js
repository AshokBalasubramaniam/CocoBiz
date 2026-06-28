require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const dealersRouter = require('./routes/dealers');
const salesRouter = require('./routes/sales');
const profileRouter = require('./routes/profile');
const settingsRouter = require('./routes/settings');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

mongoose.connect(process.env.MONGODB_URI)
  .then(() => console.log('Connected to MongoDB Atlas - CocoBiz'))
  .catch(err => { console.error('MongoDB connection error:', err); process.exit(1); });

app.use('/api/dealers', dealersRouter);
app.use('/api/sales', salesRouter);
app.use('/api/profile', profileRouter);
app.use('/api/settings', settingsRouter);

app.get('/health', (req, res) => res.json({ status: 'ok', db: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected' }));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`CocoBiz backend running on port ${PORT}`);
});
