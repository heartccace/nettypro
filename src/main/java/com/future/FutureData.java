package com.future;

/**
 * @author heartccace
 * @create 2020-05-14 11:32
 * @Description TODO
 * @Version 1.0
 */
public class FutureData implements IData<String> {
    private RealData realData = null;
    private volatile boolean isReady = false;

    public synchronized void setRealData(RealData realData) {
        if(isReady) return;
        this.realData = realData;
        this.isReady = true;
        notifyAll();
    }
    @Override
    public synchronized String get() {
        while(!isReady) {
            try{
                wait();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return realData.get();
    }
}
