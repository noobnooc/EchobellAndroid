package one.echobell.echobellandroid.data

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import one.echobell.echobellandroid.BuildConfig

private val SUBSCRIPTION_PRODUCT_IDS = listOf(
    BuildConfig.GOOGLE_PLAY_MONTHLY_SUBSCRIPTION_ID,
    BuildConfig.GOOGLE_PLAY_ANNUAL_SUBSCRIPTION_ID,
)
private val SUBSCRIPTION_BASE_PLAN_IDS = mapOf(
    BuildConfig.GOOGLE_PLAY_MONTHLY_SUBSCRIPTION_ID to BuildConfig.GOOGLE_PLAY_MONTHLY_BASE_PLAN_ID,
    BuildConfig.GOOGLE_PLAY_ANNUAL_SUBSCRIPTION_ID to BuildConfig.GOOGLE_PLAY_ANNUAL_BASE_PLAN_ID,
)

data class BillingProduct(
    val productId: String,
    val basePlanId: String,
    val title: String,
    val description: String,
    val price: String,
    val period: String,
    val offerToken: String?,
    val details: ProductDetails,
)

class GoogleBillingManager(
    context: Context,
    private val obfuscatedAccountId: String?,
    private val onPurchaseReady: (
        productId: String,
        purchaseToken: String,
        acknowledge: (onAcknowledged: (Boolean) -> Unit) -> Unit,
        finishProcessing: (success: Boolean) -> Unit,
    ) -> Unit,
    private val onMessage: (String) -> Unit,
) : PurchasesUpdatedListener {
    var products = mutableStateOf<List<BillingProduct>>(emptyList())
        private set
    var loading = mutableStateOf(false)
        private set
    var available = mutableStateOf(false)
        private set

    private var pendingUserInitiatedRestore = false
    private val processingPurchaseTokens = mutableSetOf<String>()
    private val processedPurchaseTokens = mutableSetOf<String>()

    private val billingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(this)
        .enableAutoServiceReconnection()
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    fun start() {
        if (billingClient.isReady) {
            queryProducts()
            return
        }
        loading.value = true
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                available.value = false
                loading.value = false
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                available.value = result.responseCode == BillingClient.BillingResponseCode.OK
                if (available.value) {
                    queryProducts()
                    restorePurchases(userInitiated = pendingUserInitiatedRestore)
                    pendingUserInitiatedRestore = false
                } else {
                    loading.value = false
                    if (pendingUserInitiatedRestore) {
                        onMessage(result.debugMessage.ifBlank { "Google Play is unavailable." })
                        pendingUserInitiatedRestore = false
                    }
                }
            }
        })
    }

    fun launchPurchase(activity: Activity, product: BillingProduct) {
        val offerToken = product.offerToken
        if (offerToken == null) {
            onMessage("No Google Play offer is available for this product.")
            return
        }

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product.details)
            .setOfferToken(offerToken)
            .build()

        val paramsBuilder = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))

        obfuscatedAccountId
            ?.takeIf { it.isNotBlank() }
            ?.let(paramsBuilder::setObfuscatedAccountId)

        val result = billingClient.launchBillingFlow(activity, paramsBuilder.build())
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            onMessage(result.debugMessage.ifBlank { "Google Play purchase flow could not be opened." })
        }
    }

    fun restorePurchases(userInitiated: Boolean = false) {
        if (!billingClient.isReady) {
            pendingUserInitiatedRestore = pendingUserInitiatedRestore || userInitiated
            start()
            return
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchases.isEmpty() && userInitiated) {
                    onMessage("No active Google Play subscription was found.")
                } else {
                    purchases.forEach(::handlePurchase)
                }
            } else if (userInitiated) {
                onMessage(result.debugMessage.ifBlank { "Google Play purchases could not be restored." })
            }
        }
    }

    fun dispose() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> purchases.orEmpty().forEach(::handlePurchase)
            BillingClient.BillingResponseCode.USER_CANCELED -> Unit
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> restorePurchases(userInitiated = true)
            else -> onMessage(result.debugMessage.ifBlank { "Google Play purchase failed." })
        }
    }

    private fun queryProducts() {
        loading.value = true
        val query = QueryProductDetailsParams.newBuilder()
            .setProductList(
                SUBSCRIPTION_PRODUCT_IDS.map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(query) { result, productDetailsResult ->
            loading.value = false
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                onMessage(result.debugMessage.ifBlank { "Google Play products are unavailable." })
                return@queryProductDetailsAsync
            }

            products.value = productDetailsResult.productDetailsList
                .sortedBy { SUBSCRIPTION_PRODUCT_IDS.indexOf(it.productId).takeIf { index -> index >= 0 } ?: Int.MAX_VALUE }
                .mapNotNull { details ->
                    val expectedBasePlanId = SUBSCRIPTION_BASE_PLAN_IDS[details.productId]
                    val offer = details.subscriptionOfferDetails
                        ?.firstOrNull { it.basePlanId == expectedBasePlanId }
                        ?: return@mapNotNull null
                    val paidPhase = offer
                        ?.pricingPhases
                        ?.pricingPhaseList
                        ?.lastOrNull()

                    BillingProduct(
                        productId = details.productId,
                        basePlanId = offer.basePlanId,
                        title = details.name,
                        description = details.description,
                        price = paidPhase?.formattedPrice
                            ?: "",
                        period = paidPhase?.billingPeriod?.toDisplayPeriod().orEmpty(),
                        offerToken = offer?.offerToken,
                        details = details,
                    )
                }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            onMessage("Google Play purchase is pending. Premium unlocks after payment is complete.")
            return
        }
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        val productId = purchase.products.firstOrNull() ?: return
        val purchaseToken = purchase.purchaseToken
        if (purchaseToken in processedPurchaseTokens || !processingPurchaseTokens.add(purchaseToken)) return

        val acknowledge = { onAcknowledged: (Boolean) -> Unit ->
            if (purchase.isAcknowledged) {
                onAcknowledged(true)
            } else {
                acknowledgePurchase(purchaseToken, onAcknowledged)
            }
        }
        val finishProcessing = { success: Boolean ->
            processingPurchaseTokens.remove(purchaseToken)
            if (success) {
                processedPurchaseTokens.add(purchaseToken)
            }
        }

        onPurchaseReady(productId, purchaseToken, acknowledge, finishProcessing)
    }

    private fun acknowledgePurchase(purchaseToken: String, onAcknowledged: (Boolean) -> Unit) {
        if (!billingClient.isReady) {
            onAcknowledged(false)
            return
        }
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            val acknowledged = result.responseCode == BillingClient.BillingResponseCode.OK
            if (!acknowledged) {
                onMessage(result.debugMessage.ifBlank { "Google Play purchase could not be acknowledged." })
            }
            onAcknowledged(acknowledged)
        }
    }
}

private fun String.toDisplayPeriod(): String = when (this) {
    "P1M" -> "month"
    "P1Y" -> "year"
    else -> lowercase()
}
