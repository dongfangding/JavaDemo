package webservice.jax.source;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * SEI(Service Endpoint Interface)此类的概念
 *
 * @author ddf 2016年9月20日上午10:58:33
 */
@WebService
public interface NumberHelper {
    @WebResult(name = "addResult")
    public int add(@WebParam(name = "a") int a, @WebParam(name = "b") int b);

    @WebResult(name = "jianResult")
    public int jian(@WebParam(name = "a") int a, @WebParam(name = "b") int b);
}
