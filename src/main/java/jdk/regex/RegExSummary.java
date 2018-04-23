package jdk.regex;

import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式对字符的常见四种操作
 * <p>
 * 1. 匹配match
 * 2. 切割split
 * 3. 替换replaceAll
 * 4. 获取Pattern
 *
 * @author DingDongfang
 */
public class RegExSummary {

    public static void main(String[] args) {
		/*matchDemo();
		splitDemo();
		replaceAllDemo();
		findDemo();
		findDemoByResource();
		findDemoByUrl();*/
        // matchNewLine();
        findDemoByResource2();
    }

    /**
     * 匹配操作，对手机号进行匹配
     */
    public static void matchDemo() {
        System.out.println("---------------------match start---------------------");
        String telNo = "18356784598";
        String regex = "[1][3458][1-9]{9}";
        boolean isMatch = telNo.matches(regex);
        System.out.println(telNo + ":" + isMatch);
        System.out.println("---------------------match end-----------------------");
    }

    /**
     * 切割操作
     */
    public static void splitDemo() {
        System.out.println("---------------------split start----------------------");
        String str = "wo shi    yi  ge  san hao xue         sheng!";
        String regex = " +"; // 对一个或连续的多个空格进行匹配
        String[] arr = str.split(regex);
        for (String s : arr) {
            System.out.println(s);
        }
        System.out.println("---------------------------------");
        str = "woshi########yigesan______________haoxue&&&&&&&sheng";
        regex = "(.)\\1+"; // ()代表组的概念，自定义组从1开始计数，通过数字进行引用，代表第一个字符联系多次出现
        arr = str.split(regex);
        for (String s : arr) {
            System.out.println(s);
        }
        System.out.println("---------------------split end------------------------");
    }

    /**
     * 替换操作
     */
    public static void replaceAllDemo() {
        System.out.println("-------------------replaceAll start-------------------");
        String str = "woshi########yigesan______________haoxue&&&&&&&sheng";
        String regex = "(.)\\1+";
        // $符号用于对组中的数据进行抓取
        System.out.println(str.replaceAll(regex, "$1"));
        System.out.println("-------------------replaceAll end---------------------");

        str = "18356784598";
        regex = "(\\d{3})(\\d{4})(\\d{4})";
        System.out.println(str.replaceAll(regex, "$1****$3"));
    }

    public static void findDemo() {
        System.out.println("-------------------find start-------------------------");
        String str = "wo shi yi ge san hao xue sheng!";
        String regex = "[a-z]{3}";

        // 将正则封装成对象
        Pattern pt = Pattern.compile(regex);
        // 获得匹配器对象
        Matcher mt = pt.matcher(str);

        while (mt.find()) {
            System.out.println(mt.group());
        }

        System.out.println("-------------------find end---------------------------");
    }

    /**
     * 在静态资源界面获得符合规则的数据
     */
    public static void findDemoByResource() {
        System.out.println("-----------findDemoByResource start-------------------");
        String sourcePath = "pom.xml";
        try {
            Set<String> set = new HashSet<String>();
            BufferedReader bw = new BufferedReader(new FileReader(new File(sourcePath)));
            String regex = "org(\\.[a-zA-z]+)+";
            Pattern pt = Pattern.compile(regex);
            String line = null;
            while ((line = bw.readLine()) != null) {
                Matcher m = pt.matcher(line);
                while (m.find()) {
                    set.add(m.group());
                }
            }

            for (String s : set) {
                System.out.println(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-----------findDemoByResource end--------------------");
    }

    /**
     * 在动态网页中获得符合规则的数据
     */
    public static void findDemoByUrl() {
        System.out.println("-------------findDemoByUrl start---------------------");
        try {
            Set<String> set = new HashSet<String>();
            URL url = new URL("http://tomcat.apache.org/whichversion.html");
            BufferedReader bw = new BufferedReader(new InputStreamReader(url.openStream()));
            String regex = "([a-zA-z])+ ([0-9xX]\\.)+[0-9xX]";
            Pattern pt = Pattern.compile(regex);
            String line = null;
            while ((line = bw.readLine()) != null) {
                Matcher m = pt.matcher(line);
                while (m.find()) {
                    set.add(m.group());
                }
            }
            for (String s : set) {
                System.out.println(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-------------findDemoByUrl end----------------------");
    }

    @Test
    public void replaceBr() {
        String str = "1fefwe\n2gregre\n3fewfwefwe\n4fwefwefwef\n5wfwefewf\n";
        System.out.println(str.replaceAll("(.+\n)(.+\n)(.+\n)((.+\n)*)", "$1$2$3****"));
    }


    public static void matchNewLine() {
        String result = "fwefwe\n\n\nfwefw";
        String regex = "[\\t\\n\\r]+";
        Pattern pt = Pattern.compile(regex);
        Matcher matcher = pt.matcher(result);
        result = matcher.replaceAll("@");
        System.out.println(result);
    }

    public static void findDemoByResource2() {
        System.out.println("-----------findDemoByResource start-------------------");
        String sourcePath = "hkq.txt";
        try {
            String result = "";
            String sourceStr = "";
            BufferedReader bw = new BufferedReader(new FileReader(new File(sourcePath)));
            String regex = "[\\t\\n\\r]+";
            Pattern pt = Pattern.compile(regex);
            String line = null;
            while ((line = bw.readLine()) != null) {
                sourceStr += line;
                sourceStr += System.getProperty("line.separator");
            }
            Matcher m = pt.matcher(sourceStr);
            while (m.find()) {
                result += m.replaceAll("@");
            }
            String[] arr = result.split("\\*");
            for (String str : arr) {
                String[] details = str.split("\\@");
                for (String d : details) {
                    System.out.println(d);
                }
                System.out.println("------------------------");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-----------findDemoByResource end--------------------");
    }

    @Test
    public void replaceBr1() {
        String str = "fwefwefwe<br/>fwefwefwe<br/>";
        System.out.println(str.replaceAll("<br/>", "\r\n"));
    }
}
