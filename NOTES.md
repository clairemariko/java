# Investigation and notes when doing test
- installed Java via homebrew
- setup Oracle account
- read about Gradle to get basic understanding
- Found tutorials on Amigoscode 

## First task FilteringTest

I had tried to use the Map<String, String> queryParam to store all the parameters.
This means that within this we can itterate over it and pass the parameters to the filter logic

### Filtering logic
Use Streams - looked up the documentation for Streams API and how to use it
This area doesn't seem very DRY, if i can get the Map to work so not to repeat filter logic for each condition

### Learnings
Enums - how to itterate over enums. The implementation is not scalable, for example if we add another category type and write a test then it will fails as the logic is explicietly filtering on "Top" or "Monroe"
I looked into creating a new stream by using Stream.of(Category) as a way around this but still needed to harcode the category type. 

- Found a design pattern Builder : Started learning how to implement this as I think this might fit well into the need of filtering


### Tests
The test pass however I dont think my implementation is very flexible and worth refactoring. 
This is because my logic is depending on pre-definied conditions for example : 
minHeight != null && maxHeight != null then call this method

## Hill Search Service Review
- Could we have multiple services as there seems to be at least two main actions (filtering and loading data). Reason I'm thinking of this is loading data is not really behaviour logic and could be seperated out. 
- Exception handling for methods 
- autowiring annotation - I read in tutorial that we can use the @Autowire annotation to use the service in the controller rather than having "new my service"

## Second task AllParams

## Thid task AllParams
TODO Do a check on the contentType of the file passed in and depending on that type add the logic for json, xml or csv

## Hill Search Service Review
I ended up spending too much time trying to implement a better design pattern(started looking into the Builder patter) to make my code more DRY as there a lot of repeating functionality however being completely new to Java I feel I need some more time and practice to be able to implement. 