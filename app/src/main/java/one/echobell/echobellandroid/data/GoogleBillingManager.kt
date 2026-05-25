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

private val SUBSCRIPTION_PRODUCT_IDS = listOf(
    "echobell.subscription.monthly",
    "echobell.subscription.annual",
)

data class BillingProduct(
    val productId: String,
    val title: String,
    val description: String,
    val price: String,
    val details: ProductDetails,
)

class GoogleBillingManager(
    context: Context,
    private val onPurchaseReady: (productId: String, purchaseToken: String) -> Unit,
    private val onMessage: (String) -> Unit,
) : PurchasesUpdatedListener {
    var products = mutableStateOf<List<BillingProduct>>(emptyList())
        private set
    var loading = mutableStateOf(false)
        private set
    var available = mutableStateOf(false)
        private set

    private val billingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
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
                    restorePurchases()
                } else {
                    loading.value = false
                }
            }
        })
    }

    fun launchPurchase(activity: Activity, product: BillingProduct) {
        val offerToken = product.details.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            onMessage("No Google Play offer is available for this product.")
            return
        }

        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product.details)
            .setOfferToken(offerToken)
            .build()

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()

        billingClient.launchBillingFlow(activity, params)
    }

    fun restorePurchases() {
        if (!billingClient.isReady) return
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach(::handlePurchase)
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

            products.value = productDetailsResult.productDetailsList.map { details ->
                BillingProduct(
                    productId = details.productId,
                    title = details.name,
                    description = details.description,
                    price = details.subscriptionOfferDetails
                        ?.firstOrNull()
                        ?.pricingPhases
                        ?.pricingPhaseList
                        ?.lastOrNull()
                        ?.formattedPrice
                        ?: "",
                    details = details,
                )
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { result ->
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    onMessage(result.debugMessage.ifBlank { "Failed to acknowledge purchase." })
                }
            }
        }

        val productId = purchase.products.firstOrNull() ?: return
        onPurchaseReady(productId, purchase.purchaseToken)
    }
}
