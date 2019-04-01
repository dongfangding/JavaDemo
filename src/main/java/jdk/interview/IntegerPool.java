package jdk.interview;

/**
 * @author DDf on 2019/4/1
 */
public class IntegerPool {

    /**
     * 两个对象值的比较应该使用equals，而直接使用时比较的两个对象是否相同；
     * 由于Integer/Short等基本类型的值会存在缓存值，所以再缓存值中的值对象是直接调用valueOf方法方法，因此是
     * 同一个对象，反之不是同一个对象;详见IntegerCache，-128~127直接的数是同一个对象
     * @param args
     */
    public static void main(String[] args) {
        // e 会编译错误，空文本不能转换为int类型，即使不是空文本，可以直接用Int接收单字符，但不能通过Integer
        // Integer a = 100, b = 100, c = 200, d = 200, e = '';
        Integer a = 100, b = 100, c = 200, d = 200;
        System.out.println(a == b);
        System.out.println(b == c);
//        System.out.println(d == e);
    }
}
