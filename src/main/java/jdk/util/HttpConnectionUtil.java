package jdk.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * http连接工具类
 *
 * @author dongfang.ding
 * @date 2019/6/26 11:13
 */
public class HttpConnectionUtil {

    private HttpConnectionUtil() {
    }

    /**
     * 根据网络资源创建Http连接
     *
     * @param serverPath
     * @param method
     * @param properties
     * @param connectionTimeOut
     * @return
     * @throws IOException
     */
    public static HttpURLConnection getConnection(String serverPath, String method, Map<String, String> properties,
            int connectionTimeOut) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(serverPath).openConnection();
        urlConnection.setConnectTimeout(connectionTimeOut);
        urlConnection.setRequestMethod(method);
        if (properties != null && !properties.isEmpty()) {
            properties.forEach(urlConnection::setRequestProperty);
        }
        if (urlConnection.getResponseCode() != 200) {
            throw new UnknownHostException(serverPath + ": " + urlConnection.getResponseCode());
        }
        return urlConnection;
    }
}
