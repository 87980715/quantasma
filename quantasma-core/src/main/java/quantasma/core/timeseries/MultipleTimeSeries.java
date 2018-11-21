package quantasma.core.timeseries;

import org.ta4j.core.TimeSeries;
import quantasma.core.BarPeriod;

import java.io.Serializable;
import java.time.ZonedDateTime;

public interface MultipleTimeSeries extends Serializable {
    MultipleTimeSeries aggregate(TimeSeriesDefinition timeSeriesDefinition);

    void updateBar(ZonedDateTime priceDate, double bid, double ask);

    void updateBar(ZonedDateTime priceDate, double price);

    void createBar(ZonedDateTime priceDate);

    int lastBarIndex();

    String getInstrument();

    TimeSeries getTimeSeries(BarPeriod period);
}