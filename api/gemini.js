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
    const action = String(body.action || 'text').trim().toLowerCase();
    const prompt = String(body.prompt || '').trim();
    const platform = String(body.platform || 'Instagram Reels & YouTube Shorts').trim();
    const tone = String(body.tone || 'High-Energy Viral (Hinglish)').trim();
    const contentType = String(body.contentType || 'REEL_KIT').trim();
    const expectJson = body.expectJson !== false;
    const history = Array.isArray(body.history) ? body.history.slice(-6) : [];

    const imageBase64 = String(body.imageBase64 || '').trim();
    const imageMimeType = String(body.imageMimeType || 'image/jpeg').trim();

    if (!prompt && !imageBase64) {
      return res.status(400).json({ ok: false, error: 'Prompt or image is required.' });
    }

    // 1) Optional Real Google Veo Video Generation via Gemini API (predictLongRunning)
    if (action === 'veo') {
      const veoModels = ['veo-2.0-generate-001', 'veo-3.0-generate-preview'];
      let lastVeoErr = 'Veo video generation unavailable on this API key.';
      for (const veoModel of veoModels) {
        try {
          const instanceObj = { prompt: prompt || 'Cinematic 9:16 vertical short reel' };
          if (imageBase64) {
            instanceObj.image = { bytesBase64Encoded: imageBase64, mimeType: imageMimeType };
          }
          const startResp = await fetch(
            `https://generativelanguage.googleapis.com/v1beta/models/${veoModel}:predictLongRunning?key=${encodeURIComponent(apiKey)}`,
            {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                instances: [instanceObj],
                parameters: { aspectRatio: String(body.aspectRatio || '9:16') }
              })
            }
          );
          const startJson = await startResp.json();
          if (!startResp.ok || !startJson?.name) {
            lastVeoErr = startJson?.error?.message || `Veo HTTP ${startResp.status}`;
            continue;
          }
          const opName = startJson.name;
          for (let poll = 0; poll < 16; poll++) {
            await new Promise((r) => setTimeout(r, 3500));
            const pollResp = await fetch(
              `https://generativelanguage.googleapis.com/v1beta/${opName}?key=${encodeURIComponent(apiKey)}`
            );
            const pollJson = await pollResp.json();
            if (pollJson?.done) {
              const sample =
                pollJson?.response?.generateVideoResponse?.generatedSamples?.[0]?.video ||
                pollJson?.response?.videos?.[0] ||
                pollJson?.response?.generatedVideos?.[0]?.video ||
                null;
              if (sample?.bytesBase64Encoded) {
                return res.status(200).json({
                  ok: true,
                  model: veoModel,
                  mimeType: sample.mimeType || 'video/mp4',
                  videoBase64: sample.bytesBase64Encoded
                });
              }
              if (sample?.uri) {
                const sep = sample.uri.includes('?') ? '&' : '?';
                const vidResp = await fetch(`${sample.uri}${sep}key=${encodeURIComponent(apiKey)}`);
                if (vidResp.ok) {
                  const arrBuf = await vidResp.arrayBuffer();
                  return res.status(200).json({
                    ok: true,
                    model: veoModel,
                    mimeType: sample.mimeType || 'video/mp4',
                    videoBase64: Buffer.from(arrBuf).toString('base64')
                  });
                }
              }
              lastVeoErr = pollJson?.error?.message || 'Veo completed without video output.';
              break;
            }
          }
        } catch (veoErr) {
          lastVeoErr = veoErr?.message || String(veoErr);
        }
      }
      return res.status(503).json({ ok: false, error: lastVeoErr });
    }

    // 2) AI Image Generation & Photo-to-AI Transformation (Nano Banana 2.1 / 3.1 Flash Lite Image)
    if (action === 'image') {
      const imgModels = ['gemini-nano-banana-2.1', 'gemini-3.1-flash-lite-image', 'gemini-3-pro-image'];
      let lastImgErr = 'Image generation unavailable.';
      const parts = [];
      if (imageBase64) {
        parts.push({ inlineData: { mimeType: imageMimeType, data: imageBase64 } });
      }
      parts.push({ text: prompt || 'Cinematic 9:16 vertical film still' });

      for (const imgModel of imgModels) {
        try {
          const imgResp = await fetch(
            `https://generativelanguage.googleapis.com/v1beta/models/${imgModel}:generateContent?key=${encodeURIComponent(apiKey)}`,
            {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                contents: [{ role: 'user', parts }],
                generationConfig: { responseModalities: ['IMAGE', 'TEXT'] }
              })
            }
          );
          const imgJson = await imgResp.json();
          const outParts = imgJson?.candidates?.[0]?.content?.parts || [];
          const inline = outParts.find((p) => p?.inlineData?.data);
          if (imgResp.ok && inline?.inlineData?.data) {
            const mime = inline.inlineData.mimeType || 'image/png';
            return res.status(200).json({
              ok: true,
              model: imgModel,
              mimeType: mime,
              dataUrl: `data:${mime};base64,${inline.inlineData.data}`
            });
          }
          lastImgErr = imgJson?.error?.message || `Image HTTP ${imgResp.status}`;
        } catch (imgErr) {
          lastImgErr = imgErr?.message || String(imgErr);
        }
      }
      return res.status(502).json({ ok: false, error: lastImgErr });
    }

    const systemInstruction = String(
      body.systemInstruction ||
      'You are ViralBoost AI, an expert bilingual (Hindi + Hinglish + English) short-form video strategist and social growth engineer for Indian creators on Instagram Reels, YouTube Shorts, Facebook Reels, and Telegram.'
    );

    const isSceneMode = contentType === 'FLOW_4_SCENE_REEL';
    const structuredPrompt = expectJson
      ? [
          `Content Mode: ${contentType}`,
          `Target Platform: ${platform}`,
          `Tone / Style: ${tone}`,
          `User Request / Topic: ${prompt || 'Analyze photo for a viral Hindi/Hinglish Reel'}`,
          '',
          'Respond strictly as valid JSON with keys:',
          '{',
          '  "title": "catchy clickable bilingual Hindi/English title with emoji",',
          '  "hook": "3-second scroll-stopping opening line",',
          '  "script": "complete timestamped script [0-3s HOOK], [3-20s BODY], [20-30s CTA] with visual cues",',
          '  "cta": "engagement-driving comment/share call to action",',
          '  "hashtags": "space-separated string of 8-12 trending Hindi + English hashtags",',
          '  "tips": ["tip 1", "tip 2"]' + (isSceneMode ? ',' : ''),
          ...(isSceneMode
            ? [
                '  "voiceoverText": "smooth Hindi/Hinglish narration without bracket tags",',
                '  "scenes": [{"timeRange":"0-3s","caption":"short Hindi/Hinglish caption","visualPrompt":"photorealistic 9:16 visual prompt","cameraMotion":"zoom-in"}]'
              ]
            : []),
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
    const userParts = [];
    if (imageBase64) {
      userParts.push({ inlineData: { mimeType: imageMimeType, data: imageBase64 } });
    }
    userParts.push({ text: structuredPrompt });
    contents.push({ role: 'user', parts: userParts });

    const generationConfig = {
      temperature: 0.85,
      topP: 0.95,
      maxOutputTokens: 1400
    };
    if (expectJson) {
      generationConfig.responseMimeType = 'application/json';
    }

    const models = ['gemini-3.8-flash', 'gemini-3.5-flash-lite', 'gemini-2.5-flash'];
    let geminiResp = null;
    let rawData = null;
    let modelUsed = models[0];

    for (const modelName of models) {
      try {
        modelUsed = modelName;
        geminiResp = await fetch(
          `https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent?key=${encodeURIComponent(apiKey)}`,
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
        rawData = await geminiResp.json();
        if (geminiResp.ok && rawData?.candidates?.[0]?.content?.parts?.[0]?.text) {
          break;
        }
        if (![404, 429, 500, 503].includes(geminiResp.status)) {
          break;
        }
      } catch (fetchErr) {
        rawData = { error: { message: fetchErr?.message || 'Fetch error' } };
      }
    }

    if (!geminiResp || !geminiResp.ok) {
      return res.status(geminiResp?.status || 502).json({
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
        model: modelUsed,
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
      model: modelUsed,
      data: {
        title: String(parsed.title || `🔥 ${prompt}`),
        hook: String(parsed.hook || ''),
        script: String(parsed.script || text),
        voiceoverText: String(parsed.voiceoverText || parsed.script || text),
        cta: String(parsed.cta || ''),
        hashtags: Array.isArray(parsed.hashtags) ? parsed.hashtags.join(' ') : String(parsed.hashtags || '#ViralReels #ViralBoostAI #TrendingShorts'),
        tips: Array.isArray(parsed.tips) ? parsed.tips : [],
        scenes: Array.isArray(parsed.scenes) ? parsed.scenes : undefined,
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
