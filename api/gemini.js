/**
 * ViralBoost AI — Serverless Google Gemini API Proxy (/api/gemini)
 * Reads GEMINI_API_KEY from server environment (Vercel / Node.js).
 */
module.exports = async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ ok: false, error: 'Method not allowed. Use POST.' });
  }

  const apiKey = process.env.GEMINI_API_KEY || process.env.GOOGLE_API_KEY || '';
  if (!apiKey) {
    return res.status(503).json({
      ok: false,
      code: 'MISSING_SERVER_KEY',
      error: 'Server GEMINI_API_KEY is not configured. Add your Gemini API key in Vercel Environment Variables or in the app AI Key Settings.'
    });
  }

  try {
    const body = typeof req.body === 'string' ? JSON.parse(req.body || '{}') : (req.body || {});
    const prompt = String(body.prompt || '').trim();
    const platform = String(body.platform || 'Instagram Reels & YouTube Shorts').trim();
    const tone = String(body.tone || 'High-Energy Viral (Hinglish)').trim();
    const contentType = String(body.contentType || 'REEL_KIT').trim();
    const expectJson = body.expectJson !== false;
    const history = Array.isArray(body.history) ? body.history.slice(-6) : [];

    if (!prompt) {
      return res.status(400).json({ ok: false, error: 'Prompt is required.' });
    }

    const systemInstruction = String(
      body.systemInstruction ||
      'You are ViralBoost AI, an expert bilingual (Hindi + Hinglish + English) short-form video strategist and social growth engineer for Indian creators on Instagram Reels, YouTube Shorts, Facebook Reels, and Telegram.'
    );

    const structuredPrompt = expectJson
      ? [
          `Content Mode: ${contentType}`,
          `Target Platform: ${platform}`,
          `Tone / Style: ${tone}`,
          `User Request / Topic: ${prompt}`,
          '',
          'Respond strictly as valid JSON with keys:',
          '{',
          '  "title": "catchy clickable bilingual Hindi/English title with emoji",',
          '  "hook": "3-second scroll-stopping opening line",',
          '  "script": "complete timestamped script [0-3s HOOK], [3-20s BODY], [20-30s CTA] with visual cues",',
          '  "cta": "engagement-driving comment/share call to action",',
          '  "hashtags": "space-separated string of 8-12 trending Hindi + English hashtags",',
          '  "tips": ["tip 1", "tip 2"]',
          '}'
        ].join('\n')
      : prompt;

    const contents = [];
    for (const msg of history) {
      if (msg && msg.text) {
        contents.push({
          role: msg.sender === 'USER' ? 'user' : 'model',
          parts: [{ text: String(msg.text).slice(0, 1200) }]
        });
      }
    }
    contents.push({ role: 'user', parts: [{ text: structuredPrompt }] });

    const generationConfig = {
      temperature: 0.85,
      topP: 0.95,
      maxOutputTokens: 1200
    };
    if (expectJson) {
      generationConfig.responseMimeType = 'application/json';
    }

    const geminiResp = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${encodeURIComponent(apiKey)}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          systemInstruction: { parts: [{ text: systemInstruction }] },
          contents,
          generationConfig
        })
      }
    );

    const rawData = await geminiResp.json();
    if (!geminiResp.ok) {
      return res.status(geminiResp.status).json({
        ok: false,
        error: rawData?.error?.message || 'Gemini API upstream error'
      });
    }

    const text = rawData?.candidates?.[0]?.content?.parts?.[0]?.text || '';
    if (!text) {
      return res.status(502).json({ ok: false, error: 'Empty response from Gemini API' });
    }

    if (!expectJson) {
      return res.status(200).json({
        ok: true,
        mode: 'LIVE_GEMINI_API',
        model: 'gemini-2.5-flash',
        data: { rawText: text }
      });
    }

    let parsed;
    try {
      const cleaned = text.replace(/^```json\s*/i, '').replace(/```\s*$/i, '').trim();
      parsed = JSON.parse(cleaned);
    } catch (_) {
      parsed = {
        title: `🔥 ${prompt.slice(0, 60)}`,
        hook: text.split('\n')[0] || prompt,
        script: text,
        cta: 'कमेंट में 🔥 लिखें और अभी सेव करें!',
        hashtags: '#ViralReels #TrendingShorts #ViralBoostAI #ReelsIndia #CreatorGrowth',
        tips: ['First 2 seconds mein bold Hindi+English hook caption dikhayein']
      };
    }

    return res.status(200).json({
      ok: true,
      mode: 'LIVE_GEMINI_API',
      model: 'gemini-2.5-flash',
      data: {
        title: String(parsed.title || `🔥 ${prompt}`),
        hook: String(parsed.hook || ''),
        script: String(parsed.script || text),
        cta: String(parsed.cta || ''),
        hashtags: String(parsed.hashtags || '#ViralReels #ViralBoostAI #TrendingShorts'),
        tips: Array.isArray(parsed.tips) ? parsed.tips : [],
        rawText: text
      }
    });
  } catch (err) {
    return res.status(500).json({
      ok: false,
      error: err?.message || 'Internal proxy error'
    });
  }
};
