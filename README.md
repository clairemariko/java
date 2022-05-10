# Munro's Tables

A simple web service which supports searching and sorting Munro's tables. Only hills included in the
most recent
classification are available to query. Query results are returned as a list of hills, formatted as a
JSON document.

## Building and running the webservice

You will need to install __JDK 11__ to build and run the webservice.

### Building the webservice
To compile and test the webservice, execute the appropriate command for your operating system:

* Linux or MacOS
  `./gradlew build`
* Microsoft Windows
  `gradle.bat build`

### Running the webservice locally

Execute the appropriate command to run the webservice locally:

* Linux or MacOS
  `./gradlew bootRun`
* Microsoft Windows
  `gradle.bat bootRun`

The webservice binds to port `8080` by default and is available at `http://localhost:8080/hills`

## Querying the data

A number of query parameters are available to customize the search criteria. All query parameters
are optional and
may be applied in any order. The full list of query parameters is:

* `category=Munro|Top` - filter the hills by a category, i.e. Munros or Tops. When this parameter is
  omitted, all hills are returned.
* `minHeight` - specified to the nearest tenth of a metre. Hills will match if they are greater than
  or equal to the minimum height.
* `maxHeight` - specified to the nearest tenth of a metre. Hills will match if they are less than or
  equal to the maximum height.
* `limit` - limit the number of hills returned. Must be a positive integer.
* `sort` - hills may be sorted by `name` or `height` or both. The sort parameter is specified as the
  name of the field, followed by an underscore (`_`) and then the sort order (`asc` or `desc`),
  e.g. `height_desc`. To sort by both name
  and height, specify two sort parameters in the query string, one for each field. The order of the
  fields
  determines the final order of the hills, e.g. if sorting by height descending and name ascending,
  the second sort
  parameter is only used when the heights are equal.

## Examples

Query all Munro summits and tops, ordered by descending order of height:

```
localhost:8080/hills?sort=height_desc
```

Find the first 5 Munro summits in alphabetical order, with identically named summits sorted by
descending order of
height

```
localhost:8080/hills?category=Munro&sort=name_asc&sort=height_desc&limit=5
```

Find the 5 highest Munro summits in descending order of height:

```
localhost:8080/hills?category=Munro&sort=height_desc&limit=5
```

Find all the Munro tops which are 1200 metres high or more, sorted by descending order of height

```
localhost:8080/hills?category=Top&minHeight=1200&sort=height_desc
```

Find all the Munro summits which are 920 metres or less, sorted by ascending order of height

```
localhost:8080/hills?category=Munro&maxHeight=920&sort=height_asc
```

Find all summits which are exactly 1000 metres high

```
localhost:8080/hills?minHeight=1000&maxHeight=1000
```
