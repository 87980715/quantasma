package quantasma.app.feature.data.historical.tester;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.analysis.criteria.AverageProfitCriterion;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.BuyAndHoldCriterion;
import org.ta4j.core.analysis.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.analysis.criteria.NumberOfBarsCriterion;
import org.ta4j.core.analysis.criteria.NumberOfTradesCriterion;
import org.ta4j.core.analysis.criteria.ProfitLossCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import org.ta4j.core.num.Num;
import quantasma.core.TradeStrategy;
import quantasma.core.analysis.StrategyBacktest;
import quantasma.core.analysis.TradeScenario;
import quantasma.core.analysis.criterion.FinishDepositCriterion;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

@Service
@Slf4j
public class BacktestServiceImpl implements BacktestService {

    @Autowired
    private List<StrategyBacktest> backtests;

    @Override
    public void testOverPeriod(TradeStrategy strategy, ZonedDateTime from, TemporalAmount timeDuration) {
        final StrategyBacktest backtest = backtests.stream()
                                                   .filter(strategyBacktest -> strategyBacktest.strategy() == strategy.getClass())
                                                   .findFirst()
                                                   .orElseThrow(RuntimeException::new);

        final List<TradeScenario> tradeScenarios = backtest.run(from.toLocalDateTime(), timeDuration);

        log.info("== Backtesting result for [{}] ==", backtest.strategy().getName());
        tradeScenarios.forEach(tradeScenario -> {
            final TimeSeries timeSeries = tradeScenario.getTimeSeries();
            final TradingRecord tradingRecord = tradeScenario.getTradingRecord();

            final Num finishDeposit = new FinishDepositCriterion(100, 0.0001).calculate(timeSeries, tradingRecord);
            final int trades = tradingRecord.getTrades().size();
            final Num profit = finishDeposit.minus(timeSeries.numOf(100));
            final Num avgProfit = profit.dividedBy(timeSeries.numOf(trades));
            final Num winningTradesNumber = new AverageProfitableTradesCriterion().calculate(timeSeries, tradingRecord);

            log.info("Parameters [{}]", tradeScenario.getParameters().getParameters());
            log.info(String.format("%9s | %9s | %9s | %11s | %9s | %9s",
                                   "p/l",
                                   "avg-p/l",
                                   "trades",
                                   "%win-trades",
                                   "rwrd/risk",
                                   "p/l"));
            log.info(String.format("%9.5f | %9.5f | %9d | %11.2f | %9.4f | %9.5f",
                                   profit.doubleValue(),
                                   avgProfit.doubleValue(),
                                   trades,
                                   winningTradesNumber.doubleValue(),
                                   new RewardRiskRatioCriterion().calculate(timeSeries, tradingRecord).doubleValue(),
                                   new ProfitLossCriterion().calculate(timeSeries, tradingRecord).doubleValue()));

            if (trades > 0) {
                log.info("trades:");
                tradingRecord.getTrades()
                             .stream()
                             .map(trade ->
                                          String.format("open price: %7s, close price: %7s, p/l in pips: %7.2f, open date: %7s, close date: %7s",
                                                        trade.getEntry().getPrice(),
                                                        trade.getExit().getPrice(),
                                                        new FinishDepositCriterion.ProfitLossPipsCalculator(0.0001).calculate(timeSeries, trade).doubleValue(),
                                                        timeSeries.getBar(trade.getEntry().getIndex()).getBeginTime(),
                                                        timeSeries.getBar(trade.getExit().getIndex()).getBeginTime()))
                             .forEach(System.out::println);
            }
        });
    }
}