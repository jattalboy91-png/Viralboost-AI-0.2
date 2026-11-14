package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testUpiPaymentUriWithCustomCredentials() {
    val uri = com.example.monetization.PaymentManager.createUpiPaymentUri(
      amountRupees = 199,
      planName = "VIP Pro Monthly",
      ownerUpiId = "customowner@upi",
      ownerName = "Custom Owner Name",
      transactionRef = "REF12345"
    )
    assertTrue(uri.contains("pa=customowner@upi"))
    assertTrue(uri.contains("am=199.00"))
    assertTrue(uri.contains("tr=REF12345"))
  }

  @Test
  fun testUtrValidation() {
    assertTrue(com.example.monetization.PaymentManager.isValidUtr("123456789012"))
    assertFalse(com.example.monetization.PaymentManager.isValidUtr("12345"))
    assertFalse(com.example.monetization.PaymentManager.isValidUtr("12345678901A"))
  }

  @Test
  fun testAppConfigDefaults() {
    val config = com.example.data.local.AppConfig()
    assertEquals("Amit2653@ptyes", config.ownerUpiId)
    assertEquals("5665937", config.affiliateTag)
    assertEquals(2, config.dailyStreakReward)
    assertEquals(5, config.rewardedAdPayout)
  }

  @Test
  fun testGeminiSocialContentServiceGeneratesStructuredOutput() = kotlinx.coroutines.runBlocking {
    val service = com.example.data.service.GeminiSocialContentService()
    val result = service.generateSocialContent(
      prompt = "10x Productivity Hacks",
      platform = "Instagram Reels",
      tone = "High Energy & Viral",
      contentType = "Viral 60s Script"
    )
    assertNotNull(result)
    assertTrue("Hook should not be blank", result.hook.isNotBlank())
    assertTrue("Caption should not be blank", result.bodyCaption.isNotBlank())
    assertTrue("Hashtags should not be empty", result.hashtags.isNotEmpty())
    assertTrue("First hashtag should start with #", result.hashtags.first().startsWith("#"))
  }
}

