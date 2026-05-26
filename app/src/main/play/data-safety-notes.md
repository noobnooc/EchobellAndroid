# Google Play Data Safety Notes

Use this as the source checklist when completing the Play Console Data safety form for the Android app.

## App-Level Answers

- The app collects user data.
- The app shares limited user data with service providers needed for app functionality, authentication email delivery, notification delivery, and subscription verification.
- Data is encrypted in transit.
- Users can request account deletion in the app from Settings > Account > Delete Account, or by contacting echobell@weelone.com.
- The app does not use collected data for third-party advertising.
- The app does not collect location, contacts, photos/videos, audio, files/docs, calendar, SMS, call logs, health, fitness, or browsing history data.

## Data Types To Declare

### Personal Info

- Name
  - Purpose: App functionality, account management
  - Required: Yes
  - Shared: No, except service providers
- Email address
  - Purpose: App functionality, account management, developer communications
  - Required: Yes
  - Shared: Yes, with email delivery and cloud service providers
- User IDs
  - Purpose: App functionality, account management
  - Required: Yes
  - Shared: No, except service providers

### Financial Info

- Purchase history
  - Purpose: App functionality, account management
  - Required: No, only when using premium subscriptions
  - Shared: Yes, with Google Play Billing / subscription verification services

### Messages

- Emails
  - Purpose: App functionality
  - Required: No, only when the user configures email-triggered alerts
  - Shared: No, except service providers

### App Activity

- Other user-generated content
  - Includes channel names, direct key names, notification templates, conditions, notes, webhook payloads, and email-trigger content.
  - Purpose: App functionality
  - Required: No, created by the user as part of using alerts
  - Shared: No, except service providers

### Device Or Other IDs

- Device or other IDs
  - Includes push notification tokens and app-generated device tokens.
  - Purpose: App functionality
  - Required: Yes for notification delivery
  - Shared: Yes, with notification delivery service providers

## Security Practices

- Data is encrypted in transit.
- Users can request data deletion.
- Do not claim an independent security review unless one has been completed.
