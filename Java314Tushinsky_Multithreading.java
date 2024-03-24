/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java314tushinsky_multithreading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class Java314Tushinsky_Multithreading {

    public static interface Function< F, T1, T2> {
        F operation(T1 t1, T2 t2);
    }
    
    private static final Function <Integer, Integer, Integer> SUM_VALUE = (i1, i2) -> getSumValue(i1, i2);
    private static final Function <Integer, Integer, Integer> MIN_VALUE = (i1, i2) -> getMinValue(i1, i2);
    private static final Function <Integer, Integer, Integer> MAX_VALUE = (i1, i2) -> getMaxValue(i1, i2);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int bound = 500;// размер массива
        int[] number = new int[bound];
        for(int i = 0; i < bound; i++) {
            // заполняем массив случайными числами
            int num = new Random().nextInt(bound);
            number[i] = num;
//            System.out.println("num=" + num);
            
        }
        int size = (int) bound / 2;
        int[] num1 = new int[size];
        int[] num2 = new int[size];
        for(int i = 0; i < size; i++) {
            num1[i] = number[i];
            num2[i] = number[size + i];
        }
        
//         Задача 1. Параллельный поиск в массиве
        parallelSearching(num1, num2);
        
//         Задача 2. Сумма элементов в массиве
        ArrayList<int[]> list = new ArrayList<>();
        list.add(num1);
        list.add(num2);
        sumArrayItems(list);
        
//         Задача 4. Вычисление матричного произведения
        int[][] array1 = new int[4][3];
        int[][] array2 = new int[3][4];
        // заполним массивы
        for(int i =0; i < array1.length; i++) {
            for(int j = 0; j < array2.length; j++) {
                array1[i][j] = new Random().nextInt(bound);
                array2[j][i] = new Random().nextInt(bound);
            }
        }
        for(int[] a : array1) {
            System.out.println("Array1=" + Arrays.toString(a));
        }
        for(int[] a : array2) {
            System.out.println("Array2=" + Arrays.toString(a));
        }
        calculateMatrixMultiplay(array1, array2);
        
        // Задача 5. Чётные и нечётные
        writeNumbers();
    }
    
    private static void sumArrayItems(ArrayList<int[]> list) {
        // список потоков
        ArrayList<ValueThread> threadList = new ArrayList<>(list.size());
        for(int i = 0; i < list.size(); i++) {
            // создаём потоки
            ValueThread sumThread = new ValueThread();
            sumThread.setNumber(list.get(i));// массив данных
            sumThread.setFunction(SUM_VALUE);// функция для вычисления

            threadList.add(sumThread);// добавили в список
        }
        threadList.forEach(thread -> {
            thread.start();// запускаем каждый поток
            try {
                thread.join();// присоединяем его к основному потоку
            } catch (InterruptedException ex) {
                Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        // выводим сумму для каждого массива
        threadList.forEach(thread -> System.out.println(thread.getName() + " sum = " + thread.getValue()));
        // общая сумма
        int sumTotal = threadList.stream().mapToInt(thread -> thread.getValue()).sum();
        System.out.println("sumtotal=" + sumTotal);// вывод
    }
    
    private static void parallelSearching(int[] num1, int[] num2) {
        ValueThread minThread = new ValueThread();
        ValueThread maxThread = new ValueThread();
        minThread.setNumber(num1);
        minThread.setFunction(MIN_VALUE);
        maxThread.setNumber(num2);
        maxThread.setFunction(MAX_VALUE);
        minThread.start();
        maxThread.start();
        try {
            minThread.join();
            maxThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // останавливаем потоки
        System.out.println("minvalue = " + minThread.getValue());
        System.out.println("maxvalue = " + maxThread.getValue());
        
        
        
    }
    
    private static void calculateMatrixMultiplay(int[][] array1, int[][] array2) {
        /*
        Алгоритм нахождения произведения матриц
        1. определить размеры матриц
        2. если число столбцов первой матрицы совпадает с числом строк второй
        матрицы, то выполнять умножение
        3. получаем матрицу размером X*Y, где X - количество строк первой матрицы,
        Y - количество столбцов второй матрицы.
        Для того чтобы найти элемент c 12 � 12 c 12 нужно перемножать соответствующие
        элементы 1 строки матрицы A и 2 столбца матрицы B : c 12 = a 11 ⋅ b 12 + a 12 ⋅ b 22
        "материал взят с сайта Студворк https://studwork.ru/spravochnik/matematika/matricy/umnojenie-matric"
        */
        // проверяем условие умноженияы
        if(array1.length == array2[0].length) {
            // если условие выполняется, выполняем перемножение
            // список потоков = количество строк первого массива
            int[][] resultArray = new int[array1.length][];// результат вычисления
            ArrayList<MultiplyValueThread> threadList = new ArrayList<>(array1.length);
            for (int[] rowArray : array1) {
                // создаём потоки
                MultiplyValueThread thread = new MultiplyValueThread(rowArray, array2);
                threadList.add(thread);// добавляем в массив
            }
            threadList.forEach(thread -> {
                thread.start();// запускаем каждый поток
                try {
                    thread.join();// присоединяем его к основному потоку
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            // заполняем результатами вычисления массив
            for(int i = 0; i < resultArray.length; i++) {
                resultArray[i] = threadList.get(i).getValueArray();
                System.out.println("resultArray=" + Arrays.toString(resultArray[i]));
            }
            
        }
    }
    
    private static void writeNumbers() {
        /*
        Вводим путь к файлу. Проверяем его существование. Создаём два потока.
        Задаём для них условия отбора чётных или нечётных чисел. В потоках
        создаются два файла, имена которых совпадают с исодным с добавлением
        суффикса ODD для нечётных чисел и EVEN для чётных чисел.
        Файл передаём в качестве параметра в созданные потоки, в которых и
        происходит разбор содержимого файла
        */
        try {
            BufferedReader inp = new BufferedReader(new InputStreamReader(System.in, "Windows-1251"));
            System.out.println("Введите путь к файлу:");
            String filename = inp.readLine();
//            System.out.println("filename=" + filename);
            File file = new File(filename);
            if (file.exists()) {
                System.out.println("Введите разделитель полей, если файл с разделителями:");
                String delimeter = inp.readLine();
                String extension = filename.substring(filename.lastIndexOf("."));// расширение файла
                // имя файла без расширения
                String subFileName = filename.substring(0, filename.lastIndexOf("."));
//                System.out.println("extension=" + extension);
//                System.out.println("subname=" + subFileName);
                // создаём объекты для чтения данного файла
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                // создаём два потока для записи чётных и нечётных чисел
                OddEvenThred evenThread = new OddEvenThred(reader, subFileName.concat("Even").concat(extension), true, delimeter);// чёт
                OddEvenThred oddThread = new OddEvenThred(reader, subFileName.concat("Odd").concat(extension), false, delimeter);// нечет
                // запускаем потоки
                System.out.println("Ожидайте...");
                evenThread.start();
                oddThread.start();
                try {
                    evenThread.join();
                    oddThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Количество чётных элементов=" + evenThread.getNumCount());
                System.out.println("Количество нечётных элементов=" + oddThread.getNumCount());
                
            } else {
                // если файл не существует, информируем пользователя
                System.out.println("Файл по указанному пути не существует! Проверьте правильность ввода.");
            }
        } catch (IOException ex) {
            Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Возвращает минимальное значение из двух переданных элементов
     * @param i1 первый элемент
     * @param i2 второй элемент
     * @return минимальное из двух элементов
     */
    private static int getMinValue(int i1, int i2) {
        return i1 < i2 ? i1 : i2;
    }
    
    /**
     * Возвращает максимальное значение из двух переданных элементов
     * @param i1 первый элемент
     * @param i2 второй элемент
     * @return максимальное из двух элементов
     */
    private static int getMaxValue(int i1, int i2) {
        return i1 < i2 ? i2 : i1;
    }
    
    /**
     * Возвращает сумму двух элементов
     * @param i1 первый элемент
     * @param i2 второй элемент
     * @return сумма двух элементов
     */
    private static synchronized int getSumValue(int i1, int i2) {
        return i1 + i2;
    }
    
    /**
     * Возвращает произведение двух элементов
     * @param i1 первый элемент
     * @param i2 второй элемент
     * @return произведение двух элементов
     */
    private static synchronized int getMultiplyValue(int i1, int i2) {
        return i1 * i2;
    }
    
    /**
     * Определяет принадлежность числа к чётным или нечётным
     * @param number целое число для проверки
     * @return true - если чётное, иначе возвращает false
     */
    private static synchronized boolean isOddedNumber(int number) {
        return (number % 2) == 0;
    }
    
    /**
     * Класс, реализующий поток
     */
    private static class ValueThread extends Thread {
        private int[] number;// целочисленный массив для обработки
        private int value;// значение, которое возвращается после обработки
        private Function function;// функция, которая используется для обработки
        
        public ValueThread() {
            
        }

        /**
         * Возвращает вычисленное переданной функцией значение
         * @return целочисленное значение вычисления функции
         */
        public int getValue() {
            return value;
        }

        /**
         * Задаёт массив целых чисел для обработки
         * @param number целочисленный массив
         */
        public void setNumber(int[] number) {
            this.number = number;
//            System.out.println(Arrays.toString(number));
        }

        /**
         * Задаёт функцию для обработки элементов массива
         * @param function функция для обработки элементов массива
         */
        public void setFunction(Function function) {
            this.function = function;
        }
        
        @Override
        public void run() {
            int index = 0;// счётчик цикла
            value = number[index];// первый элемент массива
            while(index < (number.length - 1)) {
                // цикл пока не достигнут конец массива
                value = (int) function.operation(value, number[index + 1]);// вычисляем значение
                if(index % 100 == 0) {
                    // инфрмация о прогрессе
                    System.out.println(Thread.currentThread().getName() + " обработано " + index + " элементов");
                }
                index++;// увеличиваем счётчик
                
                try {
                    sleep(5);// поток спит, передаёт управление другим потокам
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
    }
    
    private static class MultiplyValueThread extends Thread {
        private final int[] rowArray;// первый элемент - строка массива, содержащего данные 
        private final int[][] array;// второй элемент - массив, содержащий данные
        private final int[] valueArray;// результирующий массив - строка результирующего массива

        public MultiplyValueThread(int[] rowArray, int[][] array) {
            this.rowArray = rowArray;
            this.array = array;
            valueArray = new int[array[0].length];// инициализируем возвращаемый массив
        }

        public int[] getValueArray() {
            return valueArray;
        }

        @Override
        public void run() {
            int index = 0;// счётчик цикла
            int col = array[0].length;
            while(index < valueArray.length) {
                int value = 0;
                for(int i = 0; i < rowArray.length; i++) {
                    value += getMultiplyValue(rowArray[i], array[i][index]);
                }
                valueArray[index] = value;
                index++;// увеличиваем счётчик
                try {
                    sleep(5);// поток спит, передаёт управление другим потокам
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
    }
    
    private static class OddEvenThred extends Thread {

        private final BufferedReader reader;// объект для чтения файла
        private final String fileName;// имя файла, в который будем записывать числа
        private final boolean odded;// признак чёта или нечета (по умолчанию нечет)
        private final String delimiter;// символ-разделитель данных в файле
        private FileOutputStream fos;
        private OutputStreamWriter osw;
        private BufferedWriter writer;
        private int numCount = 0;
        /**
         * Создаёт поток для чтения и записи чисел в указанный файл
         * @param reader объект для чтения из файла, содержащего данные
         * @param fileName имя файла, в который будет запись
         * @param odded признак чёта или нечета чисел
         * @param delimiter разделитель данных в файле
         */
        public OddEvenThred(BufferedReader reader, String fileName, 
                boolean odded, String delimiter) {
            this.reader = reader;
            this.fileName = fileName;
            this.odded = odded;
            this.delimiter = delimiter;
        }

        public int getNumCount() {
            return numCount;
        }
        
        
        @Override
        public void run() {
            try {
                // создаём объект для записи данных в заданный файл
                fos = new FileOutputStream(new File(fileName));
                osw = new OutputStreamWriter(fos, "Windows-1251");
                writer = new BufferedWriter(osw);
                // проверяем задан ли разделитель данных
                if(delimiter.equals("")) {
                    // если разделитель не задан, тогда в строке будет содержаться только одно число
                    ReadWriteUnDelimitedFile();
                } else {
                    // если разделитель задан, тогда строку будем разбивать на части
                    ReadWriteDelimitedFile();
                }
            } catch (IOException ex) {
                Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * Чтение и запись файла с разделителями
         */
        private void ReadWriteDelimitedFile() throws IOException {
            String readLine;// сюда читаем строку данных из файла
            int index = 0;// счётчик
            while((readLine = reader.readLine()) != null) {
                // преобразуем строку в массив, используя указанный разделитель
                String[] strArray = readLine.split(delimiter);
                String writeline = "";
                for(String str : strArray) {
                    int num = Integer.parseInt(str);
                    if(odded == isOddedNumber(num)) {
                        // проверяем полученное число на заданное условие
                        writeline = writeline.concat(String.valueOf(num)).concat(delimiter);
                        numCount++;// увеличиваем счётчик элементов, удовлетворяющих условию
                    }
                }
                if(writeline.isEmpty()) {
                    continue;
                }
                String subline = writeline.substring(0, writeline.length() - 1).concat("\r\n");
                writer.write(subline);
                // информируем о количестве обработанных строк (частоту можно выбрать произвольно)
                if((index % 100) == 0) {
                    // частота 100 строк, т к файл, выбранный для примера, большой
                    System.out.println(Thread.currentThread().getName() + ": Обработано..." + index);
                }
                index++;
                try {
                    Thread.currentThread().sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            writer.close();
        }
        
        /**
         * Чтение и запись простого файла
         */
        private void ReadWriteUnDelimitedFile() throws IOException {
            String readLine;// сюда читаем строку данных из файла
            int index = 0;// счётчик
            while((readLine = reader.readLine()) != null) {
                // преобразуем строку в массив, используя указанный разделитель
                int num = Integer.parseInt(readLine);
                if(odded == isOddedNumber(num)) {
                    // проверяем полученное число на заданное условие
                    writer.write(readLine.concat("\r\n"));
                    numCount++;// увеличиваем счётчик элементов, удовлетворяющих условию
                }
                // информируем о количестве обработанных строк (частоту можно выбрать произвольно)
                if((index % 100) == 0) {
                    // частота 100 строк, т к файл, выбранный для примера, большой
                    System.out.println(Thread.currentThread().getName() + ": Обработано..." + index);
                }
                index++;
                
                try {
                    Thread.currentThread().sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Java314Tushinsky_Multithreading.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            writer.close();
        }
        
        
        
    }
}
