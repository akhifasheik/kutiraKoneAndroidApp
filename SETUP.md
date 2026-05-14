# Kutira-Kone — Firebase, Maps, and Gemini setup

This document complements the generated Android project. Complete these steps on a Mac or Windows machine with Android Studio **Hedgehog (2023.1.1)** or newer.

## 1. Firebase project

1. Open [Firebase console](https://console.firebase.google.com/) → **Add project** → name it (for example `kutira-kone`).
2. Register an **Android app** with package name **`com.kutira.kone`** (must match `applicationId`).
3. Download **`google-services.json`** and place it in the **project root** next to `app/` (replace the template file committed for CI/local compile).
4. In Android Studio: **File → Sync Project with Gradle Files**.

### SHA keys (phone auth / Play integrity)

1. From the project directory run (debug):

   ```bash
   ./gradlew signingReport
   ```

   Or use Android Studio **Gradle → Tasks → android → signingReport**.
2. Copy **SHA-1** (and **SHA-256** if requested) into Firebase **Project settings → Your apps → Android app → Add fingerprint**.
3. Re-download `google-services.json` if Firebase prompts you after adding fingerprints.

### Enable Firebase products

- **Authentication** → Sign-in method → **Phone** → Enable.
- **Firestore Database** → Create database (start in **test mode** only for quick demos; deploy the provided `firestore.rules` for production-shaped security).
- **Storage** → Get started; deploy `storage.rules` from this repo.
- **Cloud Messaging**: no extra toggle for client FCM; for **server-triggered** pushes (trade events while app is killed), add **Cloud Functions** (see below).

### Firestore indexes

Deploy `firestore.indexes.json` with Firebase CLI:

```bash
firebase deploy --only firestore:indexes
```

Or create the suggested composite indexes when the Firebase console shows an error link from the app.

## 2. Google Maps

1. In [Google Cloud Console](https://console.cloud.google.com/) select the same GCP project linked to Firebase (or create a Maps key in a dedicated project).
2. **APIs & Services → Library** → enable **Maps SDK for Android**.
3. **Credentials → Create credentials → API key** (restrict it to **Android apps** with package `com.kutira.kone` and your SHA-1).
4. Copy the key into **`local.properties`** (see `local.properties.example`):

   ```properties
   MAPS_API_KEY=YOUR_KEY_HERE
   ```

5. The build injects this into `AndroidManifest.xml` via `manifestPlaceholders` in `app/build.gradle.kts`.

### Manifest / permissions

- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` are declared for Fused Location and Maps.
- Runtime permission prompts are implemented for customers (map/marketplace) and for vendor upload (camera + location).

## 3. Gemini (Design ideas)

1. Open [Google AI Studio](https://aistudio.google.com/) or Cloud Console and create an API key with access to **Generative Language API**.
2. Add to **`local.properties`**:

   ```properties
   GEMINI_API_KEY=YOUR_KEY_HERE
   ```

3. The Gradle script maps this to **`BuildConfig.GEMINI_API_KEY`** (see `app/build.gradle.kts`). **Do not** commit real keys.
4. Endpoint and model name are centralized in `GeminiConstants.kt`.

## 4. FCM: server-side pushes (recommended for production)

The app includes `KutiraFirebaseMessagingService` and stores FCM tokens under `users/{uid}.fcmToken`.  
**Clients cannot reliably push to other users’ devices** without a trusted server.

Typical approach:

1. Deploy **Cloud Functions** that listen to Firestore `trade_requests` writes and send FCM to `receiverId` / `senderId` device tokens.
2. For “new nearby fabric”, use a scheduled function or geohash-based triggers; plain Firestore cannot do native geo radius queries without extra structure.

Data message `type` values handled in the service:

- `trade_request` / `trade_received`
- `trade_accepted`
- `nearby_fabric`

## 5. Build & run

1. Copy `local.properties.example` → `local.properties` and set `sdk.dir`, `GEMINI_API_KEY`, `MAPS_API_KEY`.
2. Replace `google-services.json` with your Firebase Android config.
3. **Run** on a device or emulator with Google Play services (needed for Maps, FCM, and phone auth testing).

## 6. Phone authentication testing tips

- Use a **real device** or an emulator image **with Google Play** for best results.
- Ensure the **SHA fingerprints** match the build you install (debug vs release).
- For OTP delivery issues, verify the phone number format (`+countryCode number`) and Firebase Auth quotas.

---

For architecture notes: the app uses **MVVM**, **Hilt**, **Navigation-Compose**, **Firestore offline persistence** (enabled in `KutiraKoneApplication`), **Coil**, **Maps Compose**, and **Material 3** with dark mode following system settings.
