package quantasma.core.timeseries.bar.factory;

import org.ta4j.core.num.Num;
import quantasma.core.BarPeriod;
import quantasma.core.timeseries.bar.OneSideBar;

import java.util.function.Function;

public interface BarFactory<B extends OneSideBar> {

    B create(BarPeriod barPeriod, Function<Number, Num> numFunction);

}