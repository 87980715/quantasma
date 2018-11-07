package quantasma.trade.engine;

import org.ta4j.core.Bar;
import org.ta4j.core.Order;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.util.function.Function;

public interface BidAskBar extends Bar {

    default Num getOpenPrice() {
        return getBidOpenPrice();
    }

    default Num getMinPrice() {
        return getBidMinPrice();
    }

    default Num getMaxPrice() {
        return getBidMaxPrice();
    }

    default Num getClosePrice() {
        return getBidClosePrice();
    }

    Num getBidOpenPrice();

    Num getBidMinPrice();

    Num getBidMaxPrice();

    Num getBidClosePrice();

    Num getAskOpenPrice();

    Num getAskMinPrice();

    Num getAskMaxPrice();

    Num getAskClosePrice();

    void addTrade(Num volume, Num bid, Num ask, Order.OrderType orderType);

    default void addPrice(String bid, String ask, Function<Number, Num> numFunction) {
        addPrice(numFunction.apply(new BigDecimal(bid)), numFunction.apply(new BigDecimal(ask)));
    }

    default void addPrice(Number bid, Number ask, Function<Number, Num> numFunction) {
        addPrice(numFunction.apply(bid), numFunction.apply(ask));
    }

    void addPrice(Num bid, Num ask);
}
