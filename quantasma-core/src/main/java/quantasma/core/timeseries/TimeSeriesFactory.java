package quantasma.core.timeseries;

import org.ta4j.core.TimeSeries;

import java.util.function.Function;

@FunctionalInterface
public interface TimeSeriesFactory {

    Function<TimeSeriesDefinition, TimeSeries> function();

    default TimeSeries createInstance(TimeSeriesDefinition timeSeriesDefinition) {
        return function().apply(timeSeriesDefinition);
    }
}