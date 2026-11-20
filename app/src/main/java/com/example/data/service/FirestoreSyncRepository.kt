package com.example.data.service

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.LeaderboardCreator
import com.example.data.local.PaymentRecord
import com.example.data.local.ProductOrderRecord
import com.example.data.local.SupportTicket
import com.example.data.local.UserProfile
import com.example.monetization.MonetizationConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

/**
 * Mandatory structured JSON logger for Firestore permission/operation diagnostics.
 */
fun handleFirestoreError(exception: Throwable, operationType: OperationType, path: String?): String {
    val causes = generateSequence(exception) { it.cause }
    val rootError = causes.firstOrNull { it is FirebaseFirestoreException }
        ?: causes.firstOrNull { it !is CancellationException }
        ?: exception

    val currentUser = try {
        FirebaseAuth.getInstance().currentUser
    } catch (_: Throwable) {
        null
    }

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", rootError.message ?: rootError.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    try {
        Log.e("FirestoreError", "Firestore Error: $jsonString")
    } catch (_: Throwable) {
        println("FirestoreError: $jsonString")
    }
    return jsonString
}

/**
 * Cloud Firestore backend synchronization using the provisioned named database
 * `R.string.firestore_database_id`, zero-trust security rules parity, and
 * structured error handling.
 */
class FirestoreSyncRepository(private val context: Context) {

    private val db: FirebaseFirestore? by lazy {
        try {
            val databaseId = context.getString(R.string.firestore_database_id)
            if (databaseId.isBlank()) null else FirebaseFirestore.getInstance(databaseId)
        } catch (e: Throwable) {
            Log.w("FirestoreSync", "Firestore instance unavailable: ${e.message}")
            null
        }
    }

    private fun currentUid(): String? {
        return try {
            FirebaseAuth.getInstance().currentUser?.uid
        } catch (_: Throwable) {
            null
        }
    }

    fun isSignedIn(): Boolean = currentUid() != null

    suspend fun syncUserProfileAndLeaderboard(profile: UserProfile): Result<Unit> {
        val firestore = db ?: return Result.success(Unit)
        val uid = currentUid() ?: return Result.success(Unit)
        val userPath = "users/$uid"
        val lbPath = "leaderboard/$uid"
        return try {
            val safeName = profile.username.trim().ifBlank { "ViralCreator" }.take(80)
            val safeCoins = profile.coins.coerceAtLeast(0)
            val safeDiamonds = profile.diamonds.coerceAtLeast(0)
            val now = System.currentTimeMillis()

            val userMap = mapOf(
                "uid" to uid,
                "displayUserId" to profile.displayUserId,
                "username" to safeName,
                "email" to profile.email.trim(),
                "niche" to profile.niche.trim(),
                "coins" to safeCoins,
                "diamonds" to safeDiamonds,
                "lifetimeDiamonds" to profile.lifetimeDiamonds.coerceAtLeast(0),
                "bonusRupees" to profile.bonusRupees,
                "streakDays" to profile.streakDays.coerceAtLeast(1),
                "isVip" to profile.isVip,
                "planTier" to profile.currentPlanTier,
                "vipExpiryDate" to profile.vipExpiryDate,
                "role" to if (profile.isOwner) "admin" else "user",
                "updatedAt" to now
            )

            // Preserve existing role if document already exists so rule `!isAdmin() => role unchanged` passes
            val docRef = firestore.document(userPath)
            val existing = docRef.get().await()
            val payload = if (existing.exists()) {
                val existingRole = existing.getString("role") ?: "user"
                userMap + ("role" to existingRole)
            } else {
                userMap + ("role" to "user")
            }
            docRef.set(payload).await()

            val handle = "@${safeName.lowercase().replace(Regex("[^a-z0-9_]"), "_").take(24).ifBlank { "creator" }}"
            val viralScore = safeCoins + (safeDiamonds * 10)
            val lbMap = mapOf(
                "uid" to uid,
                "name" to safeName,
                "handle" to handle,
                "niche" to profile.niche.trim().ifBlank { "Shorts & Reels" },
                "coins" to safeCoins,
                "diamonds" to safeDiamonds,
                "streakDays" to profile.streakDays.coerceAtLeast(1),
                "viralScore" to viralScore,
                "isVip" to profile.isVip,
                "updatedAt" to now
            )
            firestore.document(lbPath).set(lbMap).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userPath)
            Result.failure(e)
        }
    }

    suspend fun fetchCloudUserProfile(): Result<Map<String, Any>?> {
        val firestore = db ?: return Result.success(null)
        val uid = currentUid() ?: return Result.success(null)
        val path = "users/$uid"
        return try {
            val snap = firestore.document(path).get().await()
            if (snap.exists()) Result.success(snap.data) else Result.success(null)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    fun observeCloudLeaderboard(): Flow<List<LeaderboardCreator>> = callbackFlow {
        val firestore = db
        if (firestore == null || !isSignedIn()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val path = "leaderboard"
        val registration: ListenerRegistration = firestore.collection(path)
            .orderBy("viralScore", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val creators = snapshot.documents.mapIndexed { index, doc ->
                        val data = doc.data ?: emptyMap()
                        val uidHash = kotlin.math.abs(doc.id.hashCode())
                        val coinsVal = (data["coins"] as? Number)?.toInt() ?: 0
                        val diamondsVal = (data["diamonds"] as? Number)?.toInt() ?: 0
                        val streakVal = (data["streakDays"] as? Number)?.toInt() ?: 1
                        val scoreVal = (data["viralScore"] as? Number)?.toInt() ?: (coinsVal + diamondsVal * 10)
                        LeaderboardCreator(
                            id = if (uidHash > 1000) uidHash else 1000 + index,
                            name = data["name"] as? String ?: "Creator",
                            handle = data["handle"] as? String ?: "@creator",
                            niche = data["niche"] as? String ?: "Shorts & Reels",
                            diamonds = diamondsVal,
                            streakDays = streakVal,
                            viralScore = scoreVal,
                            isVip = data["isVip"] as? Boolean ?: false,
                            rank = index + 1,
                            coins = coinsVal
                        )
                    }
                    trySend(creators)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun submitPayment(record: PaymentRecord): Result<String> {
        val firestore = db ?: return Result.success("")
        val uid = currentUid() ?: return Result.success("")
        val path = "payments"
        return try {
            val payload = mapOf(
                "userId" to uid,
                "userName" to record.userName.ifBlank { "Creator" },
                "planName" to record.planName.take(140),
                "amountRupees" to record.amountRupees.coerceAtLeast(0),
                "utrNumber" to record.utrNumber.trim().take(40),
                "paymentMethod" to record.paymentMethod,
                "status" to "PENDING",
                "ownerUpiId" to MonetizationConstants.OWNER_UPI_ID,
                "timestamp" to record.timestamp,
                "adminNote" to record.adminNote
            )
            val docRef = firestore.collection(path).document()
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun submitSupportTicket(ticket: SupportTicket): Result<String> {
        val firestore = db ?: return Result.success("")
        val uid = currentUid() ?: return Result.success("")
        val path = "tickets"
        return try {
            val payload = mapOf(
                "userId" to uid,
                "userName" to ticket.userName.ifBlank { "Creator" },
                "userEmail" to ticket.userEmail,
                "subject" to ticket.subject.take(160),
                "category" to ticket.category,
                "message" to ticket.message.take(2000),
                "status" to "OPEN",
                "adminReply" to "",
                "createdAt" to ticket.createdAt
            )
            val docRef = firestore.collection(path).document()
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun recordAffiliateOrder(order: ProductOrderRecord): Result<String> {
        val firestore = db ?: return Result.success("")
        val uid = currentUid() ?: return Result.success("")
        val path = "affiliate_orders"
        return try {
            val payload = mapOf(
                "userId" to uid,
                "productId" to order.productId,
                "productTitle" to order.productTitle.take(160),
                "platform" to order.platform,
                "priceRupees" to order.priceRupees.coerceAtLeast(0),
                "affiliateTag" to order.affiliateTag.ifBlank { MonetizationConstants.AFFILIATE_TAG },
                "diamondsRewarded" to order.diamondsRewarded,
                "status" to order.status,
                "timestamp" to order.timestamp
            )
            val docRef = firestore.collection(path).document()
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }
}
