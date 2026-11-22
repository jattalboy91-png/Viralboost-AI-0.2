const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write protected collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("leaderboard").get());
  await assertFails(
    unauthDb.collection("payments").doc("pay_1").set({
      userId: ALICE_UID,
      planName: "VIP Pass",
      amountRupees: 49,
      utrNumber: "123456789012",
      status: "PENDING",
      timestamp: Date.now(),
    })
  );
});

test("User profile isolation and privilege escalation guard", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      uid: ALICE_UID,
      displayUserId: "74829156",
      username: "AliceCreator",
      email: "alice@example.com",
      niche: "Shorts & Reels",
      coins: 250,
      diamonds: 10,
      lifetimeDiamonds: 10,
      bonusRupees: 50,
      streakDays: 1,
      isVip: false,
      planTier: "FREE",
      vipExpiryDate: 0,
      role: "user",
      updatedAt: Date.now(),
    })
  );

  // Self update succeeds when role stays 'user'
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      coins: 350,
      diamonds: 15,
      updatedAt: Date.now(),
    })
  );

  // Role escalation must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      role: "admin",
    })
  );

  // Cross-user read/write must fail
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ coins: 99999 }));
});

test("Leaderboard allows authenticated read and owner-scoped write", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set({
      uid: ALICE_UID,
      name: "AliceCreator",
      handle: "@alicecreator",
      niche: "Shorts & Reels",
      coins: 500,
      diamonds: 25,
      streakDays: 4,
      viralScore: 750,
      isVip: false,
      updatedAt: Date.now(),
    })
  );

  await assertSucceeds(bobDb.collection("leaderboard").get());
  await assertFails(
    bobDb.collection("leaderboard").doc(ALICE_UID).update({
      coins: 0,
    })
  );
});

test("Payments allow PENDING creation by owner, admin approval, and reject forged APPROVED creation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "jattalboy91@gmail.com",
    email_verified: true,
  }).firestore();

  // Normal user cannot submit pre-APPROVED payment
  await assertFails(
    aliceDb.collection("payments").doc("pay_forged").set({
      userId: ALICE_UID,
      userName: "AliceCreator",
      planName: "Mega VIP Pass",
      amountRupees: 179,
      utrNumber: "123456789012",
      paymentMethod: "UPI_INTENT",
      status: "APPROVED",
      ownerUpiId: "Amit2653@ptyes",
      timestamp: Date.now(),
    })
  );

  // Valid PENDING submission succeeds
  await assertSucceeds(
    aliceDb.collection("payments").doc("pay_1").set({
      userId: ALICE_UID,
      userName: "AliceCreator",
      planName: "Mega VIP Pass",
      amountRupees: 179,
      utrNumber: "123456789012",
      paymentMethod: "UPI_INTENT",
      status: "PENDING",
      ownerUpiId: "Amit2653@ptyes",
      timestamp: Date.now(),
    })
  );

  // Scoped query for Alice succeeds; Bob cannot read Alice's payment
  await assertSucceeds(aliceDb.collection("payments").where("userId", "==", ALICE_UID).get());
  await assertFails(bobDb.collection("payments").doc("pay_1").get());
  await assertFails(aliceDb.collection("payments").doc("pay_1").update({ status: "APPROVED" }));

  // Admin can approve payment
  await assertSucceeds(
    adminDb.collection("payments").doc("pay_1").update({
      status: "APPROVED",
      adminNote: "Verified UTR",
    })
  );
});

test("Support tickets and affiliate orders are strictly scoped by userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("tickets").doc("t_1").set({
      userId: ALICE_UID,
      userName: "AliceCreator",
      userEmail: "alice@example.com",
      subject: "VIP Activation Question",
      category: "Payments",
      message: "Please verify my UTR 123456789012",
      status: "OPEN",
      adminReply: "",
      createdAt: Date.now(),
    })
  );
  await assertSucceeds(aliceDb.collection("tickets").where("userId", "==", ALICE_UID).get());
  await assertFails(bobDb.collection("tickets").doc("t_1").get());

  await assertSucceeds(
    aliceDb.collection("affiliate_orders").doc("ord_1").set({
      userId: ALICE_UID,
      productId: "prod_1",
      productTitle: "Wireless Creator Mic",
      platform: "Amazon",
      priceRupees: 799,
      affiliateTag: "5665937",
      diamondsRewarded: 3,
      status: "CONFIRMED",
      timestamp: Date.now(),
    })
  );
  await assertSucceeds(aliceDb.collection("affiliate_orders").where("userId", "==", ALICE_UID).get());
  await assertFails(bobDb.collection("affiliate_orders").doc("ord_1").get());
});

test("appConfig/settings allows public read and admin-only write", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "jattalboy91@gmail.com",
    email_verified: true,
  }).firestore();

  await assertFails(
    aliceDb.collection("appConfig").doc("settings").set({
      unityGameId: "999",
    })
  );

  await assertSucceeds(
    adminDb.collection("appConfig").doc("settings").set({
      unityGameId: "800427608",
      unityBannerPlacementId: "BP_Banner_Android",
      unityInterstitialPlacementId: "BP_Interstitial_Android",
      unityRewardedPlacementId: "BP_Rewarded_Android",
      affiliateTag: "5665937",
      welcomeBonusRupees: 50,
      updatedAt: Date.now(),
    })
  );

  await assertSucceeds(unauthDb.collection("appConfig").doc("settings").get());
});

test("communityCreators enforces real-user ownership and follow increment rules", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertFails(
    unauthDb.collection("communityCreators").doc("cr_unauth").set({
      userId: "fake",
      name: "Fake",
      handle: "@fake",
      platform: "YouTube",
      followersCount: 0,
      createdAt: Date.now(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("communityCreators").doc("cr_alice").set({
      userId: ALICE_UID,
      name: "AliceCreator",
      handle: "@alice_reels",
      platform: "Instagram",
      niche: "Shorts & Reels",
      url: "https://instagram.com/alice_reels",
      followersCount: 0,
      spotlight: false,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    })
  );

  await assertSucceeds(unauthDb.collection("communityCreators").get());
  await assertSucceeds(
    bobDb.collection("communityCreators").doc("cr_alice").update({
      followersCount: 1,
      updatedAt: Date.now(),
    })
  );
  await assertFails(
    bobDb.collection("communityCreators").doc("cr_alice").update({
      handle: "@hijacked",
    })
  );
  await assertFails(bobDb.collection("communityCreators").doc("cr_alice").delete());
  await assertSucceeds(aliceDb.collection("communityCreators").doc("cr_alice").delete());
});

