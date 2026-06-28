const axios = require('axios');

async function sendOtpSms(phone, otp) {
  const apiKey = process.env.FAST2SMS_API_KEY;
  if (!apiKey || apiKey === 'your_fast2sms_api_key') {
    console.log(`[NO-SMS-CONFIG] OTP for ${phone}: ${otp}`);
    return;
  }

  // Strip country code if present (Fast2SMS needs 10-digit Indian number)
  const cleaned = phone.replace(/\D/g, '').replace(/^91/, '').slice(-10);

  const response = await axios.post(
    'https://www.fast2sms.com/dev/bulkV2',
    {
      route: 'otp',
      variables_values: otp,
      flash: 0,
      numbers: cleaned
    },
    {
      headers: { authorization: apiKey }
    }
  );

  if (!response.data.return) {
    throw new Error(response.data.message || 'SMS sending failed');
  }
}

module.exports = { sendOtpSms };
