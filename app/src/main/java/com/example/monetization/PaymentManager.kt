package com.example.monetization

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object PaymentManager {

    /**
     * Builds the standard NPCI UPI Intent URI for direct deep linking into UPI apps.
     */
    fun createUpiPaymentUri(
        amountRupees: Int,
        planName: String,
        ownerUpiId: String = MonetizationConstants.OWNER_UPI_ID,
        ownerName: String = MonetizationConstants.OWNER_NAME,
        transactionRef: String = "VB${System.currentTimeMillis()}"
    ): String {
        val encodedPayeeName = URLEncoder.encode(ownerName, StandardCharsets.UTF_8.toString())
        val encodedNote = URLEncoder.encode("ViralBoost AI - $planName", StandardCharsets.UTF_8.toString())

        return "upi://pay" +
                "?pa=$ownerUpiId" +
                "&pn=$encodedPayeeName" +
                "&tn=$encodedNote" +
                "&am=$amountRupees.00" +
                "&cu=INR" +
                "&tr=$transactionRef"
    }

    /**
     * Attempts to launch UPI Intent across installed UPI apps (GPay, PhonePe, Paytm, BHIM, etc.).
     * Returns true if an activity handled the intent, false otherwise (triggering QR fallback).
     */
    fun launchUpiIntent(
        context: Context,
        amountRupees: Int,
        planName: String,
        ownerUpiId: String = MonetizationConstants.OWNER_UPI_ID,
        ownerName: String = MonetizationConstants.OWNER_NAME
    ): Boolean {
        val uriString = createUpiPaymentUri(amountRupees, planName, ownerUpiId, ownerName)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uriString)
        }

        return try {
            val chooser = Intent.createChooser(intent, "Pay via UPI App")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "No UPI app detected. Opening QR Code & UTR Verification...",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }

    /**
     * Validates whether a submitted UTR (Unique Transaction Reference) is a valid 12-digit number.
     */
    fun isValidUtr(utr: String): Boolean {
        val clean = utr.trim()
        return clean.length == 12 && clean.all { it.isDigit() }
    }
}
