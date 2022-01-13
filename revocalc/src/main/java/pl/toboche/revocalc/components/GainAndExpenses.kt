package pl.toboche.revocalc.components

import CapitalGainCalculator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.toboche.revocalc.R
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun GainAndExpenses(gainAndExpenses: CapitalGainCalculator.GainAndExpenses) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        DefaultSpacer()

        GainAndExpensesHeader("PIT-38 - rozliczenia zysków z akcji")
        GainAndExpensesHeader("Sekcja „Dochody / straty”")
        DefaultSpacer()

        ResultItem(
            stringResource(R.string.capital_income),
            gainAndExpenses.tradingIncome
        )
        ResultItem(
            stringResource(R.string.costs),
            gainAndExpenses.tradingCost
        )

        DefaultSpacer()
        Divider()
        DefaultSpacer()
        GainAndExpensesHeader("PIT-36/PIT-36L/PIT-38 - rozliczenia dywidend")
        GainAndExpensesHeader("Sekcja „Kwota do zapłaty / nadpłata”")
        DefaultSpacer()

        ResultItem(
            stringResource(R.string.dividend_tax_already_paid_header),
            gainAndExpenses.dividendTaxAlreadyPaid
        )
        ResultItem(
            stringResource(R.string.dividend_total_dividend_tax_to_pay_header),
            gainAndExpenses.totalDividendTaxToPay
        )
        ResultItem(
            stringResource(R.string.dividend_tax_header),
            gainAndExpenses.dividendTaxLeftToPay
        )

        DefaultSpacer()
        Divider()
        DefaultSpacer()
        GainAndExpensesHeader(text = "Dodatkowe informacje")
        DefaultSpacer()

        ResultItem(
            stringResource(R.string.custody_fees_header),
            gainAndExpenses.custodyFees
        )
        ResultItem(
            stringResource(R.string.tax_to_pay),
            gainAndExpenses.finallyToPay
        )
        ResultItem(
            stringResource(R.string.total_gain),
            gainAndExpenses.totalGrossIncome
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GainAndExpensesViewPreview() {
    GainAndExpenses(
        CapitalGainCalculator.GainAndExpenses(
            dividendTaxLeftToPay = BigDecimal("123.33"),
            custodyFees = BigDecimal("0.33"),
            tickerGainCalculationResult = BigDecimal("44234.33"),
            finallyToPay = BigDecimal("11111.22"),
            finalLoss = BigDecimal.ZERO,
            dividendTaxAlreadyPaid = "0.1".toBigDecimal(),
            totalDividendTaxToPay = "111.4434".toBigDecimal(),
            tradingIncome = "2312".toBigDecimal(),
            tradingCost = "999.9999".toBigDecimal(),
            totalGrossIncome = "999.9999".toBigDecimal()
        )
    )
}