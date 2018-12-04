package quantasma.app.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import quantasma.app.model.OhlcvTick;
import quantasma.app.model.PersistentOhlcvTick;
import quantasma.app.repository.OhlcvTickRepository;
import quantasma.app.util.Util;
import quantasma.core.BarPeriod;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OhlcvTickServiceImpl implements OhlcvTickService {

    private final OhlcvTickRepository ohlcvTickRepository;

    public OhlcvTickServiceImpl(OhlcvTickRepository ohlcvTickRepository) {
        this.ohlcvTickRepository = ohlcvTickRepository;
    }

    @Override
    public void insert(OhlcvTick ohlcvTick, BarPeriod barPeriod, String symbol) {
        ohlcvTickRepository.insert(new PersistentOhlcvTick(barPeriod,
                                                           ohlcvTick.getDate(),
                                                           symbol,
                                                           ohlcvTick.getBidOpen(),
                                                           ohlcvTick.getBidLow(),
                                                           ohlcvTick.getBidHigh(),
                                                           ohlcvTick.getBidClose(),
                                                           ohlcvTick.getAskOpen(),
                                                           ohlcvTick.getAskLow(),
                                                           ohlcvTick.getAskHigh(),
                                                           ohlcvTick.getAskClose(),
                                                           ohlcvTick.getVolume()));
    }

    @Override
    public void insert(PersistentOhlcvTick persistentOhlcvTick) {
        ohlcvTickRepository.insert(persistentOhlcvTick);
    }

    @Override
    public void insertSkipDuplicates(OhlcvTick ohlcvTick) {
        try {
            ohlcvTickRepository.insert(new PersistentOhlcvTick());
        } catch (DuplicateKeyException e) {
            System.out.println(String.format("Exception [%s] in [%s]", e.getMessage(), ohlcvTick));
        }
    }

    @Override
    public List<OhlcvTick> findBySymbolAndDateBetweenOrderByDate(String symbol, Instant startDate, TemporalAmount window) {
        return ohlcvTickRepository.findBySymbolAndDateBetweenOrderByDate(symbol, startDate, Util.instantPlusTemporalAmount(startDate, window))
                                  .stream()
                                  .map(candlestick -> new OhlcvTick(candlestick.getPeriod(),
                                                                    candlestick.getDate(),
                                                                    candlestick.getSymbol(),
                                                                    candlestick.getBidOpen(),
                                                                    candlestick.getBidLow(),
                                                                    candlestick.getBidHigh(),
                                                                    candlestick.getBidClose(),
                                                                    candlestick.getAskOpen(),
                                                                    candlestick.getAskLow(),
                                                                    candlestick.getAskHigh(),
                                                                    candlestick.getAskClose(),
                                                                    candlestick.getVolume()))
                                  .collect(Collectors.toList());
    }

    @Override
    public long countBySymbol(String symbol) {
        return ohlcvTickRepository.countBySymbol(symbol);
    }

}
