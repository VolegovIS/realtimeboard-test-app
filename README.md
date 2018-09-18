# RealtimeBoard TestApp

Тестовое задание для RealtimeBoard, по совместительству использовалось для изучения со Spring-HATEOAS.

Из требующего настрйоки в проекте используется lombok, [инструкция тут](https://projectlombok.org/setup/intellij). 

Приложение основано на spring-boot, запускается через `spring-boot:run` или через IDE.

Результаты замеров производительности (SIMPLE_STORE - исходная реализация хранилища, MAP_STORE, TREE_SET_STORE, COMBINED_STORE - AdaptedWidgetStore с соответствующим адаптером):

|Benchmark                 |(profileType) |(storeType)     |Mode  |Cnt    |Score    |Error    |Units|
| ------------------------ | ------------:| --------------:| ----:| -----:| -------:| -------:| ---:|
|StoreBenchmark.testStore  |          FULL|    SIMPLE_STORE|  avgt|      4|  336,426|± 11,046| us/op|
|StoreBenchmark.testStore  |          FULL|       MAP_STORE|  avgt|      4|  327,270|±  0,705| us/op|
|StoreBenchmark.testStore  |          FULL|  TREE_SET_STORE|  avgt|      4|  166,047|±  3,158| us/op|
|StoreBenchmark.testStore  |          FULL|  COMBINED_STORE|  avgt|      4|   65,467|±  3,234| us/op|
|StoreBenchmark.testStore  |      ADD_ONLY|    SIMPLE_STORE|  avgt|      4|  368,362|± 11,145| us/op|
|StoreBenchmark.testStore  |      ADD_ONLY|       MAP_STORE|  avgt|      4|  481,584|± 67,892| us/op|
|StoreBenchmark.testStore  |      ADD_ONLY|  TREE_SET_STORE|  avgt|      4|  263,140|± 32,095| us/op|
|StoreBenchmark.testStore  |      ADD_ONLY|  COMBINED_STORE|  avgt|      4|  271,043|± 22,809| us/op|
|StoreBenchmark.testStore  | FIND_ONE_ONLY|    SIMPLE_STORE|  avgt|      4|   76,153|±  9,495| us/op|
|StoreBenchmark.testStore  | FIND_ONE_ONLY|       MAP_STORE|  avgt|      4|    1,261|±  0,042| us/op|
|StoreBenchmark.testStore  | FIND_ONE_ONLY|  TREE_SET_STORE|  avgt|      4|  106,710|± 42,315| us/op|
|StoreBenchmark.testStore  | FIND_ONE_ONLY|  COMBINED_STORE|  avgt|      4|    1,275|±  0,090| us/op|
|StoreBenchmark.testStore  |   UPDATE_ONLY|    SIMPLE_STORE|  avgt|      4|  152,595|±  2,019| us/op|
|StoreBenchmark.testStore  |   UPDATE_ONLY|       MAP_STORE|  avgt|      4|  326,201|± 46,023| us/op|
|StoreBenchmark.testStore  |   UPDATE_ONLY|  TREE_SET_STORE|  avgt|      4|  199,815|±  8,383| us/op|
|StoreBenchmark.testStore  |   UPDATE_ONLY|  COMBINED_STORE|  avgt|      4|  115,160|±  3,863| us/op|
|StoreBenchmark.testStore  | FIND_ALL_ONLY|    SIMPLE_STORE|  avgt|      4|  781,971|± 76,879| us/op|
|StoreBenchmark.testStore  | FIND_ALL_ONLY|       MAP_STORE|  avgt|      4| 1023,492|± 61,812| us/op|
|StoreBenchmark.testStore  | FIND_ALL_ONLY|  TREE_SET_STORE|  avgt|      4|   75,997|± 17,669| us/op|
|StoreBenchmark.testStore  | FIND_ALL_ONLY|  COMBINED_STORE|  avgt|      4|   84,291|± 24,791| us/op|
|StoreBenchmark.testStore  |    DELETE_ADD|    SIMPLE_STORE|  avgt|      4|  120,085|±  3,092| us/op|
|StoreBenchmark.testStore  |    DELETE_ADD|       MAP_STORE|  avgt|      4|   86,168|±  7,670| us/op|
|StoreBenchmark.testStore  |    DELETE_ADD|  TREE_SET_STORE|  avgt|      4|   76,504|±  1,709| us/op|
|StoreBenchmark.testStore  |    DELETE_ADD|  COMBINED_STORE|  avgt|      4|   41,457|±  1,047| us/op|