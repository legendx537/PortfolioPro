package com.crio.warmup.stock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;



public class PortfolioManagerApplication {
   
  // the below are the three token files  
  private static String token = "780114da8421e7fd8d71cf5965eba467a8b72c02";
  //  private static String token = "f8f43e95887cfbd3079e95cc1df68412e608e7ae";
  // private static String token = "9c2ea22d81510f51d3506fc42731edd0bac5c442";


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Task:
  // - Read the json file provided in the argument[0], The file is available in the classpath.
  // - Go through all of the trades in the given file,
  // - Prepare the list of all symbols a portfolio has.
  // - if "trades.json" has trades like
  // [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  // Then you should return ["MSFT", "AAPL", "GOOGL"]
  // Hints:
  // 1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  // Check if they are of any help to you.
  // 2. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  /*  // Created the Simplified method for mainReadFile
   * a) Create an object of RestTemplate class
    b) Create a list of TotalReturnsDto
    c) Create a list of PortfolioTrade 
    d) Call the readTradesFromJson() method by passing arg[0] and assign it to PortfolioTrade list.
    e) Iterate through the portfolioTrade list and within each iteration, create and store a URL in a string type of a variable by calling prepareUrl() method (pass the portfolioTrade object, endDate, and getToken() as arguments).
    f) Create a tingoCandle array and assign the getForObject() result to it. getForObject() method can be called using the RestTemplate object which is used to deserialize the result to Tiingo POJO.
    g) Create TotalReturnsDto object by passing the Portfoliosymbol and closing price(Can be obtained by accessing TiingoCandleArray[TiingoCandleArray.length-1].getClose()).
    h) Now add the object to the TotalReturnsDto list created in step b.
    i) Sort the totalReturnsDto in increasing order based on closingPrice of TotalReturnDto.
    j) Iterate totalReturnsDto list and add the symbols to the List<String>.
    k) Return the List<String>.
   */

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

    // For Preparing a List of all the Symbols in a file
    // We need a List First
    List<String> portfolioSymbol = new ArrayList<>();

    // Get the input file source location
    File inputFile = resolveFileFromResources(args[0]);

    // Get An instance of ObjectMapper
    ObjectMapper objectMapper = getObjectMapper();

    PortfolioTrade[] portfolio = objectMapper.readValue(inputFile, PortfolioTrade[].class);

    for (PortfolioTrade iterator : portfolio) {
      // add the symbols of the each trade to the List
      portfolioSymbol.add(iterator.getSymbol());
    }

    return portfolioSymbol;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI())
        .toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 =
        "/home/crio-user/workspace/om989-hitcse2020-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@5542c4ed";
    String functionNameFromTestFileInStackTrace = "mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "29";


    return Arrays.asList(
        new String[] {valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
            functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace});
  }

    // Note:
  // 1. You may need to copy reelvant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  // and deserialize the results in List<Candle>

  

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    
      //  Create an object of RestTemplate class
       RestTemplate restTemplate = new RestTemplate(); 

      //Create a list of TotalReturnDto
      List<TotalReturnsDto> totalReturnsDtosPojo=new ArrayList<>();

      //create a list of portfolio trade 
      List<PortfolioTrade> portfolioTrade=new ArrayList<>();
      
      // readed traeds in portfolio using the readFromJSON file
      portfolioTrade = readTradesFromJson(args[0]);


      for (PortfolioTrade iterator : portfolioTrade) {
      
      // convert the string to localDate 
      LocalDate localDate = LocalDate.parse( args[1] );

      String url =  prepareUrl( iterator , localDate , PortfolioManagerApplication.getToken()  );

      // get the list of the each trade 
      TiingoCandle[] tiingoPojo = restTemplate.getForObject(  url , TiingoCandle[].class );
      
      // get the closing price  
      Double closingPrice = tiingoPojo[tiingoPojo.length -1].getClose();
       
      // Create a TotalReturnDto Object  
      TotalReturnsDto totalReturnsDto=new TotalReturnsDto( iterator.getSymbol() , closingPrice); 
      
      // add the Object to the totalReturnsDto Object  
      totalReturnsDtosPojo.add( totalReturnsDto);
    }



    // Collections.sort( totalReturnsDtosPojo , new Comparator<TotalReturnsDto>() {
  
    //     @Override
    //     public int compare(TotalReturnsDto a, TotalReturnsDto b) {
    //       // TODO Auto-generated method stub
    //       if( a.getClosingPrice() < b.getClosingPrice() )
    //        return -1;
    //       else if( a.getClosingPrice() == b.getClosingPrice() )  
    //         return 0;
    //       else  
    //        return 1;

    //     }
    //  });

    // After refactoring the code 
    Collections.sort( totalReturnsDtosPojo ,new UserComparator());
      
    // After refactoring 
    return getSymbolsList(totalReturnsDtosPojo);
    
  }

  public static String getToken() {
    return token;
  }

 
  private static List<String> getSymbolsList(List<TotalReturnsDto> totalReturnsDtosPojo) {
   
     List<String> sortedSymbol=new ArrayList<>();

     for(TotalReturnsDto iterator:totalReturnsDtosPojo) {
          sortedSymbol.add( iterator.getSymbol() );
     }
    
     return sortedSymbol;
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    
    // Resolve given filename to actual filename.
    File inputFile = resolveFileFromResources(filename);

    // From the resolved file path, read the contents of the file into a collection..
     
    // Get An instance of ObjectMapper
    // ObjectMapper is required to extract the output from the file 
    ObjectMapper objectMapper = getObjectMapper();
    
    PortfolioTrade[] portfolio = objectMapper.readValue(inputFile, PortfolioTrade[].class);
    
    // The below is not required 
    //  We can use Arrays.asList() to canvert the Array To List  

    // // Process the collection and extract the symbols.
    //  List<PortfolioTrade> portfoliosList = new ArrayList<>(); 
     
    //  for (PortfolioTrade iterator : portfolio) {
    //   // add each trade to the List
    //   portfoliosList.add(iterator);
    // }

     return Arrays.asList(portfolio);
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String url="https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate=" + trade.getPurchaseDate().toString() +"&endDate=" + endDate.toString() +
        "&token=" + token; 
    return url;
  }



  public static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    // Get the opening price of the last Candle
    int length = candles.size(); 

    if( length <= 0) 
     return (double) 0; 

     
    return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    // Get the closing price of the candles 
    int length = candles.size();
    
    if( length <= 0) 
     return (double) 0;
   
    return candles.get(length-1).getClose();     
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    
    // Construct a url first 
    String url =  prepareUrl( trade , endDate , token  );

    // call the api to fetch the data
     RestTemplate restTemplate=new RestTemplate();

    Candle[] tiingoPojo = restTemplate.getForObject(  url , TiingoCandle[].class );

    return Arrays.asList(tiingoPojo);
  }
  

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.


  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
      
       // Read Trades From Json to decode the Portfolio list from given JSON in args[0] 
       List<PortfolioTrade> portfolioTradesList= readTradesFromJson(args[0]);
       
       // Create List of Anuualized Returns  to store the values 
       List<AnnualizedReturn> annualizedReturns=new ArrayList<>();
       
       // for calling calculateAnnualizedReturns  
       // we need buying price and selling price  
       // which we will get from getOpeningPriceOnStartDate and  getClosingPriceOnEndDate method 
       // these method require List<Candles> which we will get from fetchCandles 
       
       // so first we have to fetch candles then we have get the 
       // buying price and the closing price  
       // then we have to call the calculateAnnualizedReturns 
       // then we have to add it to the AnuualizedReturn list 

       for(PortfolioTrade iterator: portfolioTradesList) {
         
         // convert the String format to LocalDate Format  
         LocalDate endDate = LocalDate.parse(args[1]); 

          List<Candle> candleList = fetchCandles( iterator , endDate, PortfolioManagerApplication.getToken() );

          double closingPrice = getClosingPriceOnEndDate(candleList);

          double openingPrice = getOpeningPriceOnStartDate(candleList);

          annualizedReturns.add(  calculateAnnualizedReturns( endDate , iterator , openingPrice , closingPrice ));
       }

       // sort the obtained list according to annualizedReturns in descending order
      //  annualizedReturns.stream().sorted( (s1, s2) -> {
      //           double d1 = s1.getAnnualizedReturn();
      //           double d2 = s2.getAnnualizedReturn();
         
      //            if(  d2 > d1 )
      //                return 1;
      //             else if( d2 == d1 )
      //              return 0; 
      //             else 
      //              return -1;      
      //           }).collect( Collectors.toList() );


      Collections.sort(annualizedReturns, new Comparator<AnnualizedReturn>() {

        @Override
        public int compare(AnnualizedReturn arg0, AnnualizedReturn arg1) {
          
          return (int) arg0.getAnnualizedReturn().compareTo(arg1.getAnnualizedReturn());
        }
        
      });

      Collections.reverse(annualizedReturns);


      return annualizedReturns;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      
      // First Way 

      // // for calclulating total return 
      // // we need opening price and closing price 
      // // which is given as buyPrice and sellPrice 
      // // in the function  
      // Double totalReturn = ( sellPrice - buyPrice) / buyPrice;

      // // calculate the total_num_years here
      // // for this we need purchase date and endDate
      // // and total_num_year should be a double value 
      // LocalDate purachaDate = trade.getPurchaseDate();

      // // calculate the days between purchase date and end Date 
      // double daysBetween = ChronoUnit.DAYS.between( purachaDate, endDate);   
      
      // //calculate the total_num_year 
      // double total_num_year = daysBetween / 365;

      // // calculate the power function here 
      // double power = ( 1 / total_num_year );

      // //calulate the base here 
      // double base = 1 + totalReturn;

      // // calculate the annualized returns  
      // double annualized_returns = Math.pow( base , power ) -1;
       
      // // calculate the AnnualizedReturn Object  
      // return new AnnualizedReturn( trade.getSymbol() , annualized_returns , totalReturn);

      
      // Second Way 

        double totalReturn= (sellPrice-buyPrice)/buyPrice;
        double numOfYears= ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / 365.2422;
        double oneByNumOfYrs= 1.0/ numOfYears;
        double annualized_return = Math.pow((1.0+ totalReturn), oneByNumOfYrs) -1 ;
      
      
        return new AnnualizedReturn(trade.getSymbol(),annualized_return, totalReturn);

      }  


      // TODO: CRIO_TASK_MODULE_REFACTOR
      //  Once you are done with the implementation inside PortfolioManagerImpl and
      //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
      //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
      //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

      // Note:
      // Remember to confirm that you are getting same results for annualized returns as in Module 3.

      public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args) throws Exception, IOException{
          String file = args[0];
          LocalDate endDate = LocalDate.parse(args[1]);
          
        
          String contents = readFileAsString(file);
          
          ObjectMapper objectMapper = getObjectMapper();

          // We need to have a JavaType To pass on to the readvalues() 
          // in order to convert the It to the specific type  
          // For JavaType we need a TypeFactory Instance  
          
          // Get an instance of typeFactory  
          TypeFactory typeFactory = objectMapper.getTypeFactory();
          
          // Construct JavaType For the Same  
          JavaType portfolioTradeListType = typeFactory.constructCollectionType( List.class, PortfolioTrade.class); 
           
          // read the values using readValues of ObjectMapper 
          List<PortfolioTrade> portfolioTrades = objectMapper.readValue( contents , portfolioTradeListType );

          // get an instance of portfolio manager  
          // Here we need to create a restTemplate  
          RestTemplate restTemplate=new RestTemplate();
          
          // call the factory static method to getPortfolioManager to get an instance  
          PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);

          return portfolioManager.calculateAnnualizedReturn( portfolioTrades , endDate);
    }

    private static String readFileAsString(String fileName) throws IOException, URISyntaxException {
      return new String(Files.readAllBytes(resolveFileFromResources(fileName).toPath()), "UTF-8");
    }
  

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    // printJsonObject(mainReadFile(args));

    // printJsonObject(mainReadQuotes(args));

    // printJsonObject(mainCalculateSingleReturn(args));

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}


class UserComparator implements Comparator<TotalReturnsDto> 
{
      @Override
      public int compare(TotalReturnsDto a, TotalReturnsDto b) {
        // TODO Auto-generated method stub
        if( a.getClosingPrice() < b.getClosingPrice() )
            return -1;
        else if( a.getClosingPrice() == b.getClosingPrice() )  
          return 0;
        else  
        return 1;
      }
  
}







