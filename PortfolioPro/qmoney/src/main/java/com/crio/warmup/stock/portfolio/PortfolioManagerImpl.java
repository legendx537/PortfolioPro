package com.crio.warmup.stock.portfolio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  private RestTemplate restTemplate;

  private StockQuotesService stockQuotesService;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility

  @Deprecated
  public PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }


  // TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // throws JsonProcessingException
  // should not be thrown wwhen we don't parse(read) json file

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws StockQuoteServiceException {

    // create a Url
    // String url = buildUri( symbol , from , to );

    // This is done using the non Standard way of retriving the api
    // calls

    // // call the api using the restTemplate
    // // Store the result in in Camndle array

    // // RestTemplate is already Intialized in the constructor
    // // So no need to intialize here again

    // // Candle[] candlePojo= restTemplate.getForObject (url, Candle[].class);
    // Candle[] candlePojo= restTemplate.getForObject (url, TiingoCandle[].class);

    // // if the response is null then return the empty arrayList
    // if( candlePojo == null )
    // return new ArrayList<>();

    // // Return the array as a List of Candles
    // return Arrays.asList(candlePojo);

    // Here we will implement the standard approach of
    // fetching from an api
    /*
     * String stringResponse = restTemplate.getForObject( url , String.class );
     * 
     * // create a Object mapper ObjectMapper objectMapper=new ObjectMapper();
     * 
     * // Register the JavaTimeModule with Object Mapper -> helps in debugging
     * objectMapper.registerModule(new JavaTimeModule());
     * 
     * // read the value from string to Candle array Candle[] candlePojo = objectMapper.readValue(
     * stringResponse, TiingoCandle[].class);
     * 
     * return Arrays.asList( candlePojo );
     */
    return stockQuotesService.getStockQuote(symbol, from, to);

  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate="
        + startDate.toString() + "&endDate=" + endDate.toString() + "&token="
        + PortfolioManagerApplication.getToken();
    return url;
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws StockQuoteServiceException {


    // Create List of Anuualized Returns to store the values
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

    // for calling calculateAnnualizedReturns
    // we need buying price and selling price
    // which we will get from getOpeningPriceOnStartDate and getClosingPriceOnEndDate method
    // these method require List<Candles> which we will get from fetchCandles

    // so first we have to fetch candles then we have get the
    // buying price and the closing price
    // then we have to call the calculateAnnualizedReturns
    // then we have to add it to the AnuualizedReturn list

    for (PortfolioTrade iterator : portfolioTrades) {

      // ebdDate is already given
      // // convert the String format to LocalDate Format
      // LocalDate endDate = LocalDate.parse(args[1]);

      List<Candle> candleList = stockQuotesService.getStockQuote(iterator.getSymbol(),
          iterator.getPurchaseDate(), endDate);

      double closingPrice = PortfolioManagerApplication.getClosingPriceOnEndDate(candleList);

      double openingPrice = PortfolioManagerApplication.getOpeningPriceOnStartDate(candleList);

      annualizedReturns.add(PortfolioManagerApplication.calculateAnnualizedReturns(endDate,
          iterator, openingPrice, closingPrice));
    }

    // 1st way of sorting
    Comparator<AnnualizedReturn> comparator = getComparator();
    annualizedReturns.sort(comparator);

    // 2nd way of sorting
    // Collections.sort(annualizedReturns, new Comparator<AnnualizedReturn>() {

    // @Override
    // public int compare(AnnualizedReturn arg0, AnnualizedReturn arg1) {

    // return (int) arg0.getAnnualizedReturn().compareTo(arg1.getAnnualizedReturn());
    // }

    // });

    // Collections.reverse(annualizedReturns);


    return annualizedReturns;

  }

  /*
   * 
   * 1. Create a ThreadPool of a configurable size (numThreads)
   * 
   * 2. Submit the execution as a callable task which returns a Future object.
   * 
   * 3. Collect all the Future and return the response.
   * 
   * /// changes 
   * 
   *  create a thread pool 
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
      List<Callable<AnnualizedReturn>> tasks = new ArrayList<>();
   *  
   */
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
      throws InterruptedException, StockQuoteServiceException {
    // TODO Auto-generated method stub

 

    // Create Excutor Service
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    
    // Create List of Annualized return  
    List<AnnualizedReturn> annualizedReturns =new ArrayList<>();

    // Create a List of AnnualizedReturnTasks
    List<AnnualizedReturnTask> annualizedReturnsTask = new ArrayList<>();

    // create a list of calalble  
    List<Callable<AnnualizedReturn>> tasks = new ArrayList<>();

      //create a list to hold the Future object associated with Callable
    List<Future<AnnualizedReturn>> annualizedReturnFutureList=new ArrayList<>();
    

    for(PortfolioTrade portfolioTrade: portfolioTrades) {
      tasks.add( () -> {
          List<Candle> candleList = stockQuotesService.getStockQuote(portfolioTrade.getSymbol(),
        portfolioTrade.getPurchaseDate(), endDate);

       double closingPrice = PortfolioManagerApplication.getClosingPriceOnEndDate(candleList);
 
       double openingPrice = PortfolioManagerApplication.getOpeningPriceOnStartDate(candleList);

       return PortfolioManagerApplication.calculateAnnualizedReturns(endDate, portfolioTrade, openingPrice, closingPrice);
      });
  
  
  
      //   Callable <AnnualizedReturn> callableTask = () ->{return PortfolioManagerApplication.calculateAnnualizedReturns(endDate,
      //     portfolioTrade, openingPrice, closingPrice);};
      //     executorService.submit(callableTask);
      //   annualizedReturnsTask.add(new AnnualizedReturnTask(portfolioTrade, stockQuotesService, endDate));
     }


     List<Future<AnnualizedReturn>> futures = executorService.invokeAll(tasks);
     executorService.shutdown();

    for (Future<AnnualizedReturn> future : futures) {
      try {
        annualizedReturns.add(future.get());
      } catch (ExecutionException e) {
        //TODO: handle exception
        throw new StockQuoteServiceException("Caught ExecutionException and thrown StockQuoteException");
     }
    }


    // try {
    //   annualizedReturnFutureList = executorService.invokeAll(annualizedReturnsTask);      
    // } catch (Exception e) {
    //   //TODO: handle exception
    //   throw new StockQuoteServiceException(e.getMessage());
    // }


    // for(Future<AnnualizedReturn> future : annualizedReturnFutureList) {
    //         try {
    //            annualizedReturns.add(future.get());
    //         } catch (ExecutionException e) {
    //           // TODO Auto-generated catch block
    //           throw new StockQuoteServiceException("Caught into ExecutionException and Thrown the StockQuoteException Instead of Caught Exception");
    //           // e.printStackTrace();
    //         }
    // }

    // executorService.shutdown();


    Comparator<AnnualizedReturn> comparator = getComparator();
    annualizedReturns.sort(comparator);

    return annualizedReturns;
  }


  // Additionally kept it as a note :- for solving

  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Modify the function #getStockQuote and start delegating to calls to
  // stockQuoteService provided via newly added constructor of the class.
  // You also have a liberty to completely get rid of that function itself, however, make sure
  // that you do not delete the #getStockQuote function.

}





//Step 1: Create a threadPool using  ExecutorService
//Step 2 : Create a list of AnnualizedReturns
//Step3 : Create a list of Future of AnnualziedReturns
//Step 4: Loop through the portfoliotrades and add it to list of AnnualizedReturns by using (new AnnualizedReturnTask(portfolioTrade, stockQuotesService, endDate))
//Step 5: Use try catch block and run the invokeAll in try and the result should be stored in list of Future of AnnualizedReturnTask and catch the exception in catch block and in catch block throw StockQuotesException
//Step 6 : The code iterates through each Future object in the annualizedReturnFutureList and retrieves the result of the task using the get() method. The retrieved AnnualizedReturn object is added to the annualizedReturns list.
//Step 7 : The executorService service is shut down.
//Step 8 : The annualizedReturns list is sorted using the comparator returned by the getComparator() method and the sorted list is returned using the Java 8 stream API's sorted() and collect() methods.

