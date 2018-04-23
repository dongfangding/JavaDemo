package jdk.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class getLocaHost {
    /**
     * 导出可执行jar包，
     * 1. 使用压缩软件打开jar包，修改MAINFEST.MF文件
     * Main-Class: package.Class
     * 添加 Main-Class: main.java.jdk.socket.getLocaHost
     * 2. 本机DOS 运行java -jar getLocalhost.jar，程序可以正常运行
     * 3. 使用软件将jar包包装成exe文件
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println(InetAddress.getLocalHost().getHostName() + ":" +
                    InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
