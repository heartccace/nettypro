package com.future;

/**
 * @author heartccace
 * @create 2020-05-14 11:41
 * @Description TODO
 * @Version 1.0
 */
public class Client {
    public IData request() {
        final FutureData future = new FutureData();
        new Thread(() -> {
            RealData realData = new RealData("");
            future.setRealData(realData);
        }).start();
        return future;
    }
}
