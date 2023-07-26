
package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  private String alphaToken = "IAR6PHXS5V8UZN55";

  private RestTemplate restTemplate;

  public AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  // to fetch daily adjusted data for last 20 years.
  // Refer to documentation here: https://www.alphavantage.co/documentation/
  // --
  // The implementation of this functions will be doing following tasks:
  // 1. Build the appropriate url to communicate with third-party.
  // The url should consider startDate and endDate if it is supported by the provider.
  // 2. Perform third-party communication with the url prepared in step#1
  // 3. Map the response and convert the same to List<Candle>
  // 4. If the provider does not support startDate and endDate, then the implementation
  // should also filter the dates based on startDate and endDate. Make sure that
  // result contains the records for for startDate and endDate after filtering.
  // 5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  // IMP: Do remember to write readable and maintainable code, There will be few functions like
  // Checking if given date falls within provided date range, etc.
  // Make sure that you write Unit tests for all such functions.
  // Note:
  // 1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  // 2. Run the tests using command below and make sure it passes:
  // ./gradlew test --tests AlphavantageServiceTest
  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  // 1. Update the method signature to match the signature change in the interface.
  // 2. Start throwing new StockQuoteServiceException when you get some invalid response from
  // Alphavantage, or you encounter a runtime exception during Json parsing.
  // 3. Make sure that the exception propagates all the way from PortfolioManager, so that the
  // external user's of our API are able to explicitly handle this exception upfront.
  // CHECKSTYLE:OFF

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws  StockQuoteServiceException {
    // TODO Auto-generated method stub


    Map<LocalDate, AlphavantageCandle> candleMap = null;

    // alphavantage class
    AlphavantageDailyResponse alphavantageDailyResponse = null;

    String candleString = null;
    try {
      // Create a Url
      String url = buildUri(symbol);

      // Fetch response as a String response
      candleString = restTemplate.getForObject(url, String.class);

      System.out.println(candleString);
      System.out.println("================================");

      // Throw a StockQuoteServiceException if the response is null
      // If we are not able to process response from the third party
      if (candleString == null) {
        throw new StockQuoteServiceException("Response contains no data.");
      }


      // Intialize a Object Mapper
      ObjectMapper objectMapper = new ObjectMapper();

      // resgister the module -> helps in debugging
      objectMapper.registerModule(new JavaTimeModule());

      // Convert the String to alphaVantage class
      alphavantageDailyResponse =
          objectMapper.readValue(candleString, AlphavantageDailyResponse.class);

      System.out.println(alphavantageDailyResponse);
      System.out.println("================================");

      // In case the response is null
      if (alphavantageDailyResponse == null) {
        throw new StockQuoteServiceException("Invalid Response from the Api");
        // return new ArrayList<>();
      }

      // // Resolve the candleString response to Candle array
      // candleMap =
      // objectMapper.readValue(candleString, AlphavantageDailyResponse.class).getCandles();

      // System.out.println( candleMap );
      // System.out.println("================================");

      // // // In case the response is null
      // if (candleMap == null) {
      // throw new StockQuoteServiceException("Invalid Response from the Api");
      // // return new ArrayList<>();
      // }

    } catch (JsonProcessingException e) {
      throw new StockQuoteServiceException(e.getMessage());

    } catch (RuntimeException e) {
      // TODO: handle exception

      System.out.println("Message  :-" + e.getMessage());
      e.printStackTrace();

    } catch (Exception e) {

      System.out.println("Unexpected error");
    }

    // Convert the alphaClass to map
    candleMap = alphavantageDailyResponse.getCandles();

    System.out.println(candleMap);
    System.out.println("================================");

    // check if the response Map ( candleMap ) is null
    // then return an empty arraylist
    if (candleMap == null)
      return new ArrayList<>();

    // Extract out the AlphavantageCandle from Map
    List<AlphavantageCandle> alphaCandleList = new ArrayList<>();

    for (Map.Entry<LocalDate, AlphavantageCandle> map : candleMap.entrySet()) {

      // create a AlphavantageCadle object with value of the map
      AlphavantageCandle alphavantageCandle = map.getValue();
      // set the date explicitly
      alphavantageCandle.setDate(map.getKey());

      // after setting the date add it to the List
      alphaCandleList.add(alphavantageCandle);

    }

    // Filter the Candles according to the dates
    List<Candle> filteredCandleList = filterCandlesByDate(alphaCandleList, from, to);

    // sort accordingly in ascending order
    Collections.sort(filteredCandleList, Comparator.comparing(Candle::getDate));

    // return the sorted List
    return filteredCandleList; 


     
    // Other tried ways 
      
    // catch (NullPointerException e) {

    // try {
    // throw new StockQuoteServiceException("The response from the api is null");
    // } catch (Exception e1) {
    // //TODO: handle exception
    // System.out.println("Message :-" + e1.getMessage());
    // e1.printStackTrace();
    // }

    // } catch (JsonProcessingException e) {

    // try {
    // throw new StockQuoteServiceException("JsonProcessing Exeption has occured");
    // } catch (StockQuoteServiceException e1) {
    // // TODO Auto-generated catch block
    // System.out.println("Message :-" + e1.getMessage());
    // e1.printStackTrace();
    // }

    // } catch (Exception e) {

    // try {
    // throw new RuntimeException("Unexpected error has occured check the program");
    // } catch (Exception e1) {
    // //TODO: handle exception
    // System.out.println("Message :-" + e1.getMessage());
    // e1.printStackTrace();
    // }

    // }
  }


  private List<Candle> filterCandlesByDate(List<AlphavantageCandle> candles, LocalDate startDate,
      LocalDate endDate) {
    List<Candle> filteredCandles = new ArrayList<>();
    for (Candle candle : candles) {
      LocalDate dateTime = candle.getDate();
      if (dateTime.isAfter(startDate.minusDays(1)) && dateTime.isBefore(endDate.plusDays(1))) {
        filteredCandles.add(candle);
      }
    }
    return filteredCandles;
  }

  // CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // 1. Write a method to create appropriate url to call Alphavantage service. The method should
  // be using configurations provided in the {@link @application.properties}.
  // 2. Use this method in #getStockQuote.


  private String buildUri(String symbol) {
    // There is no strat date and end date support
    // So we have to build the uri accordingly
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol
        + "&outputsize=full&apikey=" + alphaToken;

    // Return the url that is built
    return url;
  }



  // String createUrl(String symbol) {

  // return "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol
  // + "&outputsize=full&apikey=" + getAPI();
  // }

  // String getAPI() {
  // return "0K4LCXEQZW4RSEE6";
  // }


  // @Override
  // public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) {


  // String responseString = restTemplate.getForObject(createUrl(symbol), String.class);

  // AlphavantageDailyResponse alphavantageDailyResponse = null;
  // try {
  // ObjectMapper objectMapper = new ObjectMapper();
  // objectMapper.registerModule(new JavaTimeModule());

  // alphavantageDailyResponse =
  // objectMapper.readValue(responseString, AlphavantageDailyResponse.class);
  // if (alphavantageDailyResponse.getCandles() == null || responseString == null)
  // try {
  // throw new StockQuoteServiceException("Invalid Response Found");
  // } catch (StockQuoteServiceException e) {
  // // TODO Auto-generated catch block
  // try {
  // throw new StockQuoteServiceException(e.getMessage());
  // } catch (StockQuoteServiceException e1) {
  // // TODO Auto-generated catch block
  // e1.printStackTrace();
  // }
  // }
  // } catch (JsonProcessingException e) {
  // try {
  // throw new StockQuoteServiceException(e.getMessage());
  // } catch (StockQuoteServiceException e1) {
  // // TODO Auto-generated catch block
  // e1.printStackTrace();
  // }
  // }
  // List<Candle> alphavantageCandles = new ArrayList<>();
  // Map<LocalDate, AlphavantageCandle> mapOFDateAndAlphavantageCandle =
  // alphavantageDailyResponse.getCandles();
  // for (LocalDate localDate : mapOFDateAndAlphavantageCandle.keySet()) {
  // if (localDate.isAfter(from.minusDays(1)) && localDate.isBefore(to.plusDays(1))) {
  // AlphavantageCandle alphavantageCandle =
  // alphavantageDailyResponse.getCandles().get(localDate);
  // alphavantageCandle.setDate(localDate);
  // alphavantageCandles.add(alphavantageCandle);
  // }
  // }
  // return alphavantageCandles.stream().sorted(Comparator.comparing(Candle::getDate))
  // .collect(Collectors.toList());

  // }



}

