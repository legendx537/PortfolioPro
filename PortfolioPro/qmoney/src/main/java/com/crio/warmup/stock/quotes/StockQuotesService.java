package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.List;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;

public interface StockQuotesService {



  List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws  StockQuoteServiceException;

}
