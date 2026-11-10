package com.personalfinance.cuentas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.personalfinance.cuentas.presentation.dashboard.CashFlowChartCard
import com.personalfinance.cuentas.presentation.dashboard.CompactFinancialHeroCard
import org.junit.Rule
import org.junit.Test

class DashboardBugExplorationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun p1_heroCardLabels_visibleAcrossWidths() {
        listOf(320, 360, 393, 412, 430).forEach { widthDp ->
            composeTestRule.setContent {
                MaterialTheme {
                    Box(Modifier.requiredWidth(widthDp.dp)) {
                        CompactFinancialHeroCard(
                            consolidatedCents        = 74_651_795L,
                            staticSavingsCents       = 0L,
                            currentMonthPendingCents = 10_000_000L,
                            currentMonthNetCents     = 64_651_795L,
                            onShowTimeline           = {}
                        )
                    }
                }
            }
            composeTestRule.onNodeWithText("Libre este mes", useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithText("Obligaciones este mes", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun p2_cashFlowCard_hasNoOwnToggle_whenOnViewModeChangeIsNull() {
        composeTestRule.setContent {
            MaterialTheme {
                CashFlowChartCard(
                    projections       = emptyList(),
                    selectedHorizon   = 30,
                    onHorizonSelected = {},
                    isProjectedView   = false,
                    onViewModeChange  = null
                )
            }
        }
        composeTestRule.onNodeWithText("Actual", useUnmergedTree = true).assertDoesNotExist()
    }
}