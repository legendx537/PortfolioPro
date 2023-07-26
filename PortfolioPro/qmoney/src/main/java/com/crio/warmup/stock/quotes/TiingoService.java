
package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  // ./gradlew test --tests TiingoServiceTest


  /*
   * Standard approach to read data from an api 1. accept in string.class 2. initialise and
   * customise the object mapper as per the requirements 3. use the object mapper 4. Then make
   * changes to the list or array
   */

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws  StockQuoteServiceException {

    // create a Url
    String url = buildUri(symbol, from, to);

    String candlePojo = null;
    Candle[] result = null;

    try {

      // call the api using the restTemplate
      // Store the result in in Candle array

      // RestTemplate is already Intialized in the constructor
      // So no need to intialize here again

      // Done using standard approach
      candlePojo = restTemplate.getForObject(url, String.class);

      if (candlePojo == null) {
        throw new StockQuoteServiceException("Response contains no data.");
      }

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());

      result = objectMapper.readValue(candlePojo, TiingoCandle[].class);

      if (result == null) {
        throw new StockQuoteServiceException("Invalid Response from the Api");
      }

    }  catch (JsonProcessingException e) {
       throw new StockQuoteServiceException(e.getMessage());

    } catch (RuntimeException e) {
      // TODO: handle exception

      System.out.println("Message  :-" + e.getMessage());
      e.printStackTrace();

    } catch (Exception e) {

      System.out.println("Unexpected error");
    }
     

    // In tiingo service and advantage service 
    // throw Stockquote Exception inside JsonProcessing Catch .
    //  



    // catch (StockQuoteServiceException e) {
    //   System.out.println("Message :-" + e.getMessage());
    //   e.printStackTrace();
    // } catch (Exception e) {
    //   // TODO: handle exception

    //   try {
    //     throw new RuntimeException("Unexpected error has occured check the program");
    //   } catch (Exception e1) {
    //     // TODO: handle exception
    //     System.out.println("Message  :-" + e1.getMessage());
    //     e1.printStackTrace();
    //   }

    // }

    return Arrays.asList(result);

    // Done using not following the standard approach

    // check if the response we are getting is null
    // if it is null then return an empty list
    // if( candlePojo == null )
    // return new ArrayList<>();

    // Converted to List from array

    // List<Candle> candleList = Arrays.asList(candlePojo);

    // 4. If the provider does not support startDate and endDate, then the implementation
    // should also filter the dates based on startDate and endDate.
    // List<Candle> filteredCandleList = filterCandlesByDate( candleList, from , to );

    // 5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
    // sort in ascending order
    // Collections.sort( filteredCandleList , Comparator.comparing(Candle::getDate));

    // Return the Sorted array as a List of Candles
    // return filteredCandleList;



  }

  // This method is not required here
  // // The Applied Filter To have only Candle which lie under the start date and end date
  // private List<Candle> filterCandlesByDate(List<Candle> candles, LocalDate startDate, LocalDate
  // endDate) {
  // List<Candle> filteredCandles = new ArrayList<>();
  // for (Candle candle : candles) {
  // LocalDate dateTime = candle.getDate();
  // if (dateTime.isAfter(startDate) && dateTime.isBefore(endDate)) {
  // filteredCandles.add(candle);
  // }
  // }
  // return filteredCandles;
  // }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call the Tiingo API.

  private String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

    // The Below lines does not verify the whetehr the start date and end date are null or not
    // String url="https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" +
    // startDate.toString() +"&endDate=" + endDate.toString() +
    // "&token=" + PortfolioManagerApplication.getToken();
    // return url;


    // Here we have implemented with the check on the start date and the end date
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=";

    // check if the start date and the end provided is not null
    if (startDate != null && endDate != null) {
      url += startDate.toString() + "&end_date=" + endDate.toString();
    }

    // add the token
    url += "&token=" + PortfolioManagerApplication.getToken();

    // Return the url that is built
    return url;



    // TODO: CRIO_TASK_MODULE_EXCEPTIONS
    // 1. Update the method signature to match the signature change in the interface.
    // Start throwing new StockQuoteServiceException when you get some invalid response from
    // Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
    // a runtime exception during Json parsing.
    // 2. Make sure that the exception propagates all the way from
    // PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
    // are able to explicitly handle this exception upfront.

    // CHECKSTYLE:OFF
  }

}
