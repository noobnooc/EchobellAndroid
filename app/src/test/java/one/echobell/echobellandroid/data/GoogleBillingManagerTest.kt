package one.echobell.echobellandroid.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoogleBillingManagerTest {
    @Test
    fun selectsOfferWithExpectedTrialPeriod() {
        val paidOffer = offer("monthly", "paid", paidPhase("P1M"))
        val trialOffer = offer("monthly", "trial", trialPhase("P3D"), paidPhase("P1M"))

        val selected = listOf(paidOffer, trialOffer)
            .selectBillingOffer(expectedBasePlanId = "monthly", expectedTrialPeriod = "P3D")

        assertEquals("trial", selected?.offerToken)
    }

    @Test
    fun fallsBackToPaidBasePlanOfferWhenTrialIsUnavailable() {
        val paidOffer = offer("monthly", "paid", paidPhase("P1M"))
        val wrongTrialOffer = offer("monthly", "wrong-trial", trialPhase("P7D"), paidPhase("P1M"))

        val selected = listOf(wrongTrialOffer, paidOffer)
            .selectBillingOffer(expectedBasePlanId = "monthly", expectedTrialPeriod = "P3D")

        assertEquals("paid", selected?.offerToken)
    }

    @Test
    fun rejectsOffersOutsideExpectedBasePlan() {
        val selected = listOf(offer("annual", "annual-trial", trialPhase("P7D"), paidPhase("P1Y")))
            .selectBillingOffer(expectedBasePlanId = "monthly", expectedTrialPeriod = "P3D")

        assertNull(selected)
    }

    private fun offer(
        basePlanId: String,
        offerToken: String,
        vararg phases: BillingPricingPhase,
    ) = BillingOfferCandidate(
        basePlanId = basePlanId,
        offerToken = offerToken,
        pricingPhases = phases.toList(),
    )

    private fun trialPhase(period: String) = BillingPricingPhase(
        formattedPrice = "$0.00",
        billingPeriod = period,
        priceAmountMicros = 0L,
    )

    private fun paidPhase(period: String) = BillingPricingPhase(
        formattedPrice = "$9.99",
        billingPeriod = period,
        priceAmountMicros = 9_990_000L,
    )
}
