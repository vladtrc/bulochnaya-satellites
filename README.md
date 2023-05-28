# bulochnaya-satellites
Library API:

Основной класс: LibraryAPI

Основной метод: algoOutput(String path, String pathFacility, String pathRussia)
где параметры: 
* path - путь к папке с выходными результатами.
* pathFacility - путь к папке с исходниками Facility2Constellation.
* pathRussia - путь к папке с исходниками Russia2Constellation.


Как им пользоваться:

1) создаем экземпляр класса LibraryAPI.
   LibraryAPI library=new LibraryAPI();

2) вызываем метод с тремя параметрами:

library.algoOutput("/home/badma/Загрузки/output/Aleksey_algo/",
"/home/badma/Загрузки/DATA_Files/Facility2Constellation/",
"/home/badma/Загрузки/DATA_Files/Russia2Constellation/");



P.S : единственный хардкод, который остался - это период наблюдения и параметры спутников.
В классе Given - если нужно поменять горизонт планирования, нужно внести его руками:

Пример:
   public static Interval limits = new Interval(Instant.parse("2027-06-01T00:00:00Z"),
   Instant.parse("2027-06-14T00:00:00Z"));


также как и показатели ниже:

   public static long tx_speedC = 1000;  // Мегабит/сек отправка на Землю

   public static long tx_speed = 250;  // Мегабит/сек отправка на Землю

   public static long rx_speed = 4000;  // Мегабит/сек фотографирование

   public static long memory_limit = 8000000;  // Мегабит (1 Терабайт)

   public static long memory_limit2 = 4000000;
