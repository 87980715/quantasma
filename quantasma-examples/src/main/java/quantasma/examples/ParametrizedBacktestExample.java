package quantasma.examples;

import org.ta4j.core.Order;
import org.ta4j.core.TradingRecord;
import quantasma.core.BarPeriod;
import quantasma.core.BaseContext;
import quantasma.core.Context;
import quantasma.core.MarketData;
import quantasma.core.TestManager;
import quantasma.core.TradeStrategy;
import quantasma.core.analysis.parametrize.Producer;
import quantasma.core.analysis.parametrize.Variables;
import quantasma.core.MarketDataBuilder;
import quantasma.core.timeseries.ReflectionManualIndexTimeSeries;
import quantasma.core.timeseries.TimeSeriesDefinition;
import quantasma.core.timeseries.bar.BidAskBar;
import quantasma.examples.RSIStrategy.Parameter;

import java.util.function.Function;

import static quantasma.core.analysis.parametrize.generators.Ints.range;

public class ParametrizedBacktestExample {
    public static void main(String[] args) {
        // tag::parametrizedBacktestExample[]
        final MarketData<BidAskBar> marketData =
                MarketDataBuilder.basedOn(TimeSeriesDefinition.unlimited(BarPeriod.M1))
                                 .symbols("EURUSD")
                                 .aggregate(TimeSeriesDefinition.Group.of("EURUSD")
                                                                      .add(TimeSeriesDefinition.unlimited(BarPeriod.M5)))
                                 .wrap(ReflectionManualIndexTimeSeries::wrap)
                                 .build();

        final Context context = new BaseContext.Builder()
                .withMarketData(marketData)
                .build();

        final Function<Variables<Parameter>, TradeStrategy> recipe = var -> {
            var._int(Parameter.RSI_PERIOD).values(10, 14);
            var._int(Parameter.RSI_LOWER_BOUND).with(range(10, 40, 10));
            var._int(Parameter.RSI_UPPER_BOUND).with(range(90, 60, 10));
            var._String(Parameter.TRADE_SYMBOL).with("EURUSD");
            return RSIStrategy.buildBullish(context, var.getParameterValues());
        };

        // Feed historical data by calling marketData.add()

        final TestManager testManager = new TestManager<>(marketData);
        Producer.from(recipe)
                .stream()
                .forEach(tradeStrategy -> {
                    final TradingRecord result = testManager.run(tradeStrategy, Order.OrderType.BUY);
                    // Proper criterion can be used now on the result
                });
        // end::parametrizedBacktestExample[]
    }
}
