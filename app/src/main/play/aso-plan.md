# Google Play ASO Plan — Echobell Android

Source of truth for the Play store listing: positioning, keyword strategy, the
copy in `listings/<locale>/`, and the screenshot set in
`listings/<locale>/graphics/phone-screenshots/`.

---

## 1. How Google Play ranks, and what that means here

Play has no hidden keyword field. It indexes the **title (30 chars)**, the
**short description (80 chars)**, and the **full description (4,000 chars)**,
and matches semantically rather than on exact repetition. The practical rules
this listing is built on:

| Rule | Source | How this listing applies it |
| --- | --- | --- |
| The title carries the most ranking weight; lead with the primary keyword | Play ASO practice | Title is `Echobell: Webhook Push Alerts` — brand + the three head terms |
| Short description is the second-highest-impact field | Play ASO practice | Carries `webhook`, `push notification`, and the call-style differentiator |
| Roughly one exact match per ~250 chars; 2–3 % density; stuffing does not compound | Play ASO practice | Head terms recur once per section, never back to back |
| Only the first ~167 chars of the full description show before "more" | Play ASO practice | Opening line states what it does *and* the differentiator |
| Keywords must be **transcreated**, not translated | Play ASO practice | Each locale below has its own keyword set, not a translation of the English one |
| No competitor brand names, no `#1` / `best` / awards, no price or promo wording, no emoji in the title | [Play listing policy](https://support.google.com/googleplay/android-developer/answer/13393723) | Verified across all 18 locales — see §7 |

---

## 2. Positioning

**Category:** Tools / Productivity — webhook-driven push notifications.

**One-line promise:** send one HTTP request and your phone knows; critical
alerts can ring like a phone call.

**Primary audiences,** in rough order of search volume for this category:

1. Developers and SRE / ops — incidents, on-call, deploys, uptime
2. Self-hosters and homelab owners — NAS, sensors, cron, backups
3. Indie makers and small businesses — payments, orders, customer events
4. Traders — price alerts, strategy scripts

**The differentiator to lead with:** three alert levels, topped by a call-style
alert that rings. Generic "push notification" apps do not have this, and it is
the only feature in the set that a screenshot can sell instantly.

**What we do not claim:** Echobell is not a monitoring, automation or incident
platform. The full description says so explicitly in the closing line — it
sets accurate expectations and heads off one-star "it doesn't monitor anything"
reviews.

---

## 3. Title and short description decisions

| Field | Was | Now | Why |
| --- | --- | --- | --- |
| Title | `Echobell: Push Notifications` (28) | `Echobell: Webhook Push Alerts` (29) | `push notifications` is a very broad head term Echobell will not win. `webhook` is the high-intent term where it can rank, and the new title still indexes `push`, `alerts`, plus the phrases `webhook push`, `push alerts`, `webhook alerts` |
| Short desc | `Turn webhooks into instant alerts for incidents, deploys, trades, and more.` (75) | `Turn webhooks into push notifications. Critical alerts ring like a phone call.` (78) | Recovers `push notifications` (lost from the title) and adds the differentiator, which lifts conversion, not just ranking |

**Test this, do not assume it.** The title change is the single highest-variance
edit in this plan. Run it as a Play Console *store listing experiment* against
the current title before rolling it out globally, and hold the rest of the
listing constant while it runs.

---

## 4. Keyword map per locale

Primary terms belong in the title/short description; secondary terms are worked
into the full description body. None are exact translations of each other — each
set reflects how that market actually searches.

| Locale | Primary | Secondary |
| --- | --- | --- |
| en-US / en-GB | webhook, push alerts, push notifications | server monitoring, uptime alert, cron job, CI/CD, on-call, incident, self-hosted, home lab, price alert |
| zh-CN | Webhook, 推送通知, 告警 | 服务器监控, 宕机提醒, 定时任务, 部署通知, 值班, 自建服务, 家庭实验室, 价格提醒 |
| zh-TW | Webhook, 推播通知, 告警 | 伺服器監控, 當機提醒, 排程工作, 部署通知, 值班, 自架服務, 家庭實驗室, 價格提醒 |
| ja-JP | Webhook, プッシュ通知, アラート | サーバー監視, 死活監視, cron, デプロイ通知, オンコール, セルフホスト, ホームラボ, 価格アラート |
| ko-KR | 웹훅, 푸시 알림, 알림 | 서버 모니터링, 헬스 체크, cron, 배포 알림, 온콜, 셀프호스팅, 홈랩, 가격 알림 |
| de-DE | Webhook, Push-Benachrichtigung, Alarm | Server-Monitoring, Uptime, Cronjob, Deploy, Rufbereitschaft, Selfhosting, Homelab, Kurs-Alarm |
| fr-FR | webhook, notification push, alerte | supervision serveur, disponibilité, cron, déploiement, astreinte, auto-hébergement, homelab, alerte de prix |
| es-ES / es-419 | webhook, notificación push, alerta | monitorización de servidores, uptime, cron, despliegue, guardias, autoalojamiento, homelab, alerta de precio |
| pt-BR | webhook, notificação push, alerta | monitoramento de servidor, uptime, cron, deploy, plantão, self-hosted, homelab, alerta de preço |
| ru-RU | вебхук, пуш-уведомления, оповещения | мониторинг серверов, доступность, cron, деплой, дежурство, self-hosted, домашняя лаборатория, оповещение о цене |
| it-IT | webhook, notifiche push, avvisi | monitoraggio server, uptime, cron, deploy, reperibilità, self-hosting, homelab, avviso di prezzo |
| tr-TR | webhook, anlık bildirim, uyarı | sunucu izleme, erişilebilirlik, cron, dağıtım, nöbet, kendi sunucusu, homelab, fiyat uyarısı |
| pl-PL | webhook, powiadomienia push, alerty | monitoring serwerów, uptime, cron, wdrożenie, dyżur, self-hosted, homelab, alert cenowy |
| nl-NL | webhook, pushmeldingen, meldingen | servermonitoring, uptime, cronjob, deploy, piket, self-hosted, homelab, koersmelding |
| id | webhook, notifikasi push, notifikasi | pemantauan server, uptime, cron, deploy, on-call, self-hosted, homelab, notifikasi harga |
| vi | webhook, thông báo đẩy, cảnh báo | giám sát máy chủ, uptime, cron, triển khai, on-call, tự vận hành, homelab, cảnh báo giá |

---

## 5. Screenshots

Six phone screenshots per locale, 1080 × 1920 (9:16), 24-bit PNG — above Play's
recommended 1080p minimum and within the 2–8 per device type allowed.

**Order is deliberate.** Roughly 90 % of visitors never scroll past the third
screenshot, so the first three run *what it is → the differentiator → how it
works*:

| # | Slug | Screen | English caption |
| --- | --- | --- | --- |
| 1 | `01-inbox` | Records list | Every alert from every system, in one place |
| 2 | `02-call` | Incoming call alert | Critical alerts ring your phone like a call |
| 3 | `03-webhook` | Channel detail — trigger | One webhook URL connects any tool |
| 4 | `04-control` | Alert modes, templates, conditions | Templates, filters and three alert levels |
| 5 | `05-channels` | Channels list | Group alerts and share them with your team |
| 6 | `06-direct` | Direct keys | Fire a notification from any script or cron job |

Captions are localized in all 18 locales. Each is a benefit statement, not a
feature label, and none reference price, ranking or awards.

**Device UI language.** The app ships seven locales (`en`, `zh-Hans`,
`zh-Hant`, `ja`, `fr`, `de`, `es`), and for those the screenshots show the app
*and* the demo data in that language. The other eleven listings pair localized
captions with the English UI, because the app itself has no translation to show
— worth revisiting if any of those markets starts converting.

**How they were produced.** Captured on an Android emulator (1080 × 1920, API 36)
running a debug build pointed at a local mock API, with SystemUI demo mode
freezing the status bar at 10:41, full signal and no stray icons. Demo data is
realistic but fictional: no real tokens, customers or endpoints appear. Frames
are composited on the Echobell-orange dark backdrop.

---

## 6. Other listing assets

- **App icon** — `listing-icon.png`, 512 × 512, 32-bit PNG with alpha, under 1 MB. Current asset is compliant and unchanged.
- **Feature graphic** — two versions now exist:
  - `feature-graphic.jpg` at the `play/` root is the **existing** light-background graphic, left untouched so `:app:checkPlayListingAssets` keeps passing.
  - `listings/<locale>/graphics/feature-graphic/feature-graphic.jpg` is a **new localized set** (18 locales, 1024 × 500, JPEG, no alpha) matching the dark screenshot frames, with the tagline transcreated per locale. Swapping to these makes the listing read as one design system; the old graphic clashes with the new screenshots.
- **Alt text** — tracked in `asset-alt-text.properties` and enforced by `:app:checkPlayListingAssets`. Update `featureGraphic` if the localized set replaces the root graphic.
- **Tablet screenshots** — not yet produced. Play requires 7-inch and 10-inch sets for the tablet quality tier and for most featuring. Worth adding if tablet install share justifies it.
- **Promo video** — none. Optional, and Play shows it in place of the first screenshot, so only add one if it beats screenshot #1 on conversion.

---

## 7. Compliance checklist

Verified across all 18 locales:

- [x] Title ≤ 30 chars, short ≤ 80, full ≤ 4,000 (asserted by the generator that wrote these files)
- [x] No competitor brand names anywhere in the metadata
- [x] No `#1`, "best", "app of the year", or other ranking/award claims
- [x] No price, discount, or promotional wording ("free", "% off", "limited time")
- [x] No emoji, emoticons or special character sequences in any title
- [x] No all-caps except the brand, which is not capitalized
- [x] No user testimonials, attributed or otherwise
- [x] Screenshots contain no pricing, ranking or promotional overlays
- [x] Premium is described factually (what it unlocks), with no prices

---

## 8. Rollout order

| Tier | Locales | Rationale |
| --- | --- | --- |
| 1 — ship first | en-US, zh-CN, zh-TW, ja-JP, de-DE, fr-FR, es-ES | The app UI is localized, so screenshots and product match end to end |
| 2 — ship next | ko-KR, pt-BR, ru-RU, it-IT, tr-TR, pl-PL, nl-NL, id, vi, es-419, en-GB | High-value or high-volume Play markets for a developer tool; listing-only localization |

en-GB is nearly free (one spelling change) and es-419 splits Latin American
Spanish from Spain, which Play treats as separate locales with separate search
behaviour.

---

## 9. Measuring it

Play Console → *Store performance* is the only reliable feedback loop; treat
everything above as a hypothesis until it moves these:

1. **Store listing acquisition** — split by traffic source. Watch *Play Store search* impressions and the search → store-listing → install funnel per locale.
2. **Store listing experiments** — run the title change first (§3), on its own. Then, separately, screenshot order. Never both at once.
3. **Search terms report** — the terms actually bringing impressions. Feed real winners back into §4 and re-cut the full description; the keyword map above is a starting hypothesis, not measured data.
4. **Per-locale conversion rate** — a Tier-2 locale converting well is the signal to invest in translating the app UI for it and re-shooting its screenshots.

Re-review this plan after the first experiment concludes, or after any release
that adds a user-visible feature worth a new screenshot.
