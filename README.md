# Echobell Android

Native Android client for Echobell notifications, channel subscriptions, direct keys, and call-style alerts.

## Local Development

Build and test the app from the repository root:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

To point a debug build at a local `echobell-cloud` server running on the host machine:

```sh
./gradlew :app:assembleDebug \
  -PECHOBELL_DEBUG_API_BASE_URL=http://10.0.2.2:8787 \
  -PECHOBELL_DEBUG_HOOK_BASE_URL=http://10.0.2.2:8787
```

## Release

Google Play uploads should use the release Android App Bundle. Before uploading, run the stricter publish check. It requires release signing credentials, checks Play Billing and store-listing assets, runs release lint, and builds the release bundle:

```sh
./gradlew :app:checkPublishRelease
```

Google Play Billing product IDs and base plan IDs are tracked in `app/src/main/play/billing-products.properties`. Store-listing assets and release notes live under `app/src/main/play/`. The Play Console and backend setup checklist lives in `app/src/main/play/play-billing-checklist.md`.

By default, local release signing is read from ignored files under `.local-signing/`:

```properties
storeFile=.local-signing/echobell-upload.jks
storePassword=...
keyAlias=echobell-upload
keyPassword=...
```

Keep this directory out of source control. Gradle properties or environment variables can still override the local file when needed:

```sh
ECHOBELL_RELEASE_STORE_FILE=/absolute/path/upload-keystore.jks
ECHOBELL_RELEASE_STORE_PASSWORD=...
ECHOBELL_RELEASE_KEY_ALIAS=...
ECHOBELL_RELEASE_KEY_PASSWORD=...
```

Call-style alerts use high-priority `CallStyle` notifications and a ringtone channel. The Play release does not declare `USE_FULL_SCREEN_INTENT`, avoiding the restricted full-screen intent policy path for non-dialer, non-alarm apps.
