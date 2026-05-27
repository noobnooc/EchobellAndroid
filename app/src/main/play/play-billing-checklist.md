# Google Play Billing Release Checklist

Use this checklist before promoting an Android release with Premium subscriptions.

## App Catalog

The app queries the subscription products listed in `billing-products.properties`.

| Plan | Product ID | Base plan ID | Billing period |
| --- | --- | --- | --- |
| Monthly | `echobell.subscription.monthly` | `monthly` | `P1M` |
| Annual | `echobell.subscription.annual` | `annual` | `P1Y` |

In Play Console, create these under **Monetize with Play > Products > Subscriptions**. Each subscription must have at least one active base plan, country/region availability, and a price. Offers are optional.
The Android client selects offers by the base plan IDs above, so changing a base plan ID in Play Console also requires updating `billing-products.properties`.

## Play Console Checks

- The uploaded Android App Bundle declares `com.android.vending.BILLING`.
- The app is uploaded to an internal, closed, open, or production track before testing subscriptions. Sideloaded debug APKs can connect to Play Billing but normally cannot resolve unpublished subscription products.
- Both subscription products are active.
- Both base plans are active.
- Countries/regions are enabled for each active base plan.
- License testers or test track users are configured with the Google account used on the test device.
- Terms and privacy URLs are available in the app and listing:
  - `https://echobell.one/terms`
  - `https://echobell.one/privacy`

## Backend Verification

The Android app posts `productId` and `purchaseToken` to `/v1/subscription/report-google-subscription`.
The Echobell backend verifies the token with Android Publisher API, so production and staging environments that accept Google Play purchases need:

- `GOOGLE_PLAY_PACKAGE_NAME=one.echobell.echobellandroid`
- `GOOGLE_PLAY_CLIENT_EMAIL`
- `GOOGLE_PLAY_PRIVATE_KEY`
- `GOOGLE_PLAY_RTDN_AUDIENCE=https://service.echobell.one/v1/subscription/google-play-rtdn`
- `GOOGLE_PLAY_RTDN_SERVICE_ACCOUNT_EMAIL=echobell-google-play@echobell-android.iam.gserviceaccount.com`
- `GOOGLE_PLAY_RTDN_VERIFICATION_TOKEN`

The service account must have permission to read subscription purchase status for this app in Play Console.

Cloudflare production currently routes `api.echobell.one/*`, `hook.echobell.one/*`, and `service.echobell.one/*` to the `echobell-cloud` Worker. Keep those routes active before testing purchases.

## RTDN Setup

Use Google Cloud project `echobell-android` (`845167882289`) for Google Play RTDN.

Required Google Cloud APIs:

- Google Play Android Developer API: `androidpublisher.googleapis.com`
- Cloud Pub/Sub API: `pubsub.googleapis.com`
- IAM Service Account Credentials API: `iamcredentials.googleapis.com`

Pub/Sub resources:

- Topic: `projects/echobell-android/topics/echobell-google-play-rtdn`
- Push subscription: `echobell-google-play-rtdn-push`
- Push endpoint: `https://service.echobell.one/v1/subscription/google-play-rtdn?token=<GOOGLE_PLAY_RTDN_VERIFICATION_TOKEN>`
- OIDC service account: `echobell-google-play@echobell-android.iam.gserviceaccount.com`
- OIDC audience: `https://service.echobell.one/v1/subscription/google-play-rtdn`

IAM requirements:

- Grant `google-play-developer-notifications@system.gserviceaccount.com` `roles/pubsub.publisher` on the RTDN topic.
- Grant `service-845167882289@gcp-sa-pubsub.iam.gserviceaccount.com` `roles/iam.serviceAccountTokenCreator` on `echobell-google-play@echobell-android.iam.gserviceaccount.com` so Pub/Sub can mint OIDC tokens for authenticated push.

In Play Console, configure Real-time developer notifications for the app with topic `projects/echobell-android/topics/echobell-google-play-rtdn`, then send a test notification and confirm the Worker returns a `test` RTDN result.

## Local Gates

Run:

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Before uploading a release bundle, run:

```sh
./gradlew :app:checkPublishRelease
```

`checkPublishRelease` includes release signing, release lint, release bundle generation, and the local Play Billing catalog check.
It also checks the local Google Play listing assets for required icon, feature graphic, screenshot, and text limits.
Asset alt text is tracked in `asset-alt-text.properties`; use those values when uploading graphics in Play Console.
Release notes are tracked in `release-notes/en-US/default.txt`; paste them into the Play Console release notes field for this release.
