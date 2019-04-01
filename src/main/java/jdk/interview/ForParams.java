package jdk.interview;

/**
 * @author DDf on 2019/4/1
 */
public class ForParams {

    public static int printInt(int a) {
        System.out.print(a);
        return a;
    }

    public static void main(String[] args) {
        int i = 0;
        /**
         * printInt(i++), printInt(++i)只会在第一次循环的时候执行一次；
         * i < 6 && printInt(i++) < 5 第一次循环结束之后直接走这一局判断语句
         * printInt(++i) 每次for循环中的语句块执行完之后才会执行
         */
        for (printInt(i++), printInt(++i); i < 6 && printInt(i++) < 5; printInt(++i)) {
            printInt(0);
        }

        System.out.println("-----------------------------");
        /**
         * 等同于以下语句
         */
        i = 0;
        printInt(i++);
        printInt(++i);
        while (i < 6 && printInt(i++) < 5) {
            printInt(0);
            printInt(++i);
        }
    }
}
