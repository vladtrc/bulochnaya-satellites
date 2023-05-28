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




