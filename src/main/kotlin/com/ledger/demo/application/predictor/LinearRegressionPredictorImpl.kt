package com.ledger.demo.application.predictor

import com.ledger.demo.application.dto.Direction
import com.ledger.demo.domain.TransactionEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

/**
 * One implementation of predictor based on linear regression algo
 */
@Service
class LinearRegressionPredictorImpl(
    private val clock: Clock
): IPredictBalanceService {

    /**
     * This method calculates and estimates amount based on linear regression algorithm,
     * the goal of linear regression is to find the "best-fit" line (regression line)
     * that predicts the value of the dependent variable (often denoted as y) x based
     * on the independent variable(s)
     *
     * How it works:
     * Data Collection: Collect or define the dataset that includes values for the
     * dependent variable and the independent variable(s).
     *
     * Model Training: The algorithm calculates the optimal values ofm (slope) and b (intercept)
     * that minimize the error between predicted values and actual values in the dataset.
     *
     * Prediction: Once trained, the model can predict the dependent variable for new independent variable
     * values by plugging them into the linear equation.
     *
     * @see https://en.wikipedia.org/wiki/Linear_regression
     *
     * @param futureDateTime Offsetdatetime object that represents the future timestamp
     * @param transactions List of historical transactions
     *
     * @return BigDecimal calculated estimation amount based on historical data
     */
    override fun predictAmount(futureDateTime: OffsetDateTime, transactions: List<TransactionEntity>): BigDecimal {

        transactions.ifEmpty { throw IllegalArgumentException("Transactions list must not be empty") }

        // Prepare data for linear regression:
        // x-axis: Days since the first transaction in our historical period
        // y-axis: Cumulative balance at each transaction date
        val daysSinceStart = transactions.map { transaction ->
            ChronoUnit.DAYS.between(transactions.first().createdAt, transaction.createdAt).toDouble()
        }

        // Calculate cumulative balances over time as y-axis values for regression
        var cumulativeBalance = BigDecimal.ZERO
        val cumulativeBalances = transactions.map { transaction ->
            cumulativeBalance = when(transaction.direction) {
                Direction.OUT -> cumulativeBalance.subtract(transaction.amount)
                Direction.IN -> cumulativeBalance.add(transaction.amount)
            }
            cumulativeBalance.toDouble()
        }

        // Apply linear regression to find the slope (m) and intercept (b)
        val (slope, intercept) = linearRegression(daysSinceStart, cumulativeBalances)

        // Calculate days to predict into the future (from today to the requested future date)
        val daysToPredict = ChronoUnit.DAYS.between(OffsetDateTime.now(clock), futureDateTime).toDouble()

        // Predict future balance using the formula y = mx + b (where m is slope and b is intercept)
        return BigDecimal(intercept + slope * (daysSinceStart.last() + daysToPredict))
            .setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * Calculates the slope (m) and intercept (b) of the best-fit line using the least squares methodology
     *
     * @param x List of x-values (days since start)
     * @param y List of y-values (cumulative balances)
     *
     * @return Pair of slope (m) and intercept (b)
     */
    private fun linearRegression(x: List<Double>, y: List<Double>): Pair<Double, Double> {
        // Number of points
        val n = x.size

        // Summations needed for calculations
        val sumX = x.sum()                            // Sum of x values
        val sumY = y.sum()                            // Sum of y values
        val sumXY = x.zip(y).sumOf { it.first * it.second }  // Sum of x*y products
        val sumXSquare = x.sumOf { it * it }          // Sum of x squared

        // Slope (m) formula: m = (n*sumXY - sumX*sumY) / (n*sumXSquare - sumX^2)
        val slope = (n * sumXY - sumX * sumY) / (n * sumXSquare - sumX * sumX)

        // Intercept (b) formula: b = (sumY - m*sumX) / n
        val intercept = (sumY - slope * sumX) / n

        return slope to intercept
    }
}