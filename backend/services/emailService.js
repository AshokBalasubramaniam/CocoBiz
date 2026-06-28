const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
  host: process.env.EMAIL_HOST,
  port: parseInt(process.env.EMAIL_PORT || '587'),
  secure: false,
  auth: { user: process.env.EMAIL_USER, pass: process.env.EMAIL_PASS }
});

async function sendOtpEmail(to, otp) {
  await transporter.sendMail({
    from: `"CocoBiz" <${process.env.EMAIL_USER}>`,
    to,
    subject: 'CocoBiz - Password Reset OTP',
    html: `
      <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;padding:24px;border:1px solid #e0e0e0;border-radius:8px;">
        <h2 style="color:#2E7D32;">CocoBiz Password Reset</h2>
        <p>Your OTP code is:</p>
        <h1 style="letter-spacing:8px;color:#1B5E20;font-size:36px;">${otp}</h1>
        <p style="color:#666;">Valid for 10 minutes. Do not share this code.</p>
      </div>`
  });
}

async function sendReminderEmail(to, salesList, businessName) {
  const rows = salesList.map(s =>
    `<tr>
      <td style="padding:8px;border-bottom:1px solid #eee;">${s.dealerName}</td>
      <td style="padding:8px;border-bottom:1px solid #eee;">${s.dealerPlace}</td>
      <td style="padding:8px;border-bottom:1px solid #eee;">${s.remainingDays} days</td>
      <td style="padding:8px;border-bottom:1px solid #eee;">₹${s.totalAmount.toLocaleString()}</td>
    </tr>`
  ).join('');

  await transporter.sendMail({
    from: `"CocoBiz" <${process.env.EMAIL_USER}>`,
    to,
    subject: `CocoBiz - ${salesList.length} Sale${salesList.length > 1 ? 's' : ''} Due Soon`,
    html: `
      <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;padding:24px;">
        <h2 style="color:#2E7D32;">🌴 CocoBiz Harvesting Reminder</h2>
        <p>Hello <strong>${businessName}</strong>,</p>
        <p>The following sales are due soon:</p>
        <table style="width:100%;border-collapse:collapse;">
          <thead><tr style="background:#E8F5E9;">
            <th style="padding:8px;text-align:left;">Dealer</th>
            <th style="padding:8px;text-align:left;">Place</th>
            <th style="padding:8px;text-align:left;">Due In</th>
            <th style="padding:8px;text-align:left;">Amount</th>
          </tr></thead>
          <tbody>${rows}</tbody>
        </table>
        <p style="margin-top:16px;color:#666;font-size:12px;">CocoBiz — Coconut Business Manager</p>
      </div>`
  });
}

module.exports = { sendOtpEmail, sendReminderEmail };
