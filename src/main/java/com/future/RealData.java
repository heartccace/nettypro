package com.future;

/**
 * @author heartccace
 * @create 2020-05-14 11:31
 * @Description TODO
 * @Version 1.0
 */
public class RealData implements IData<String> {
    private final String result;

    public RealData(String result) {
        this.result = result;  // 将会执行很长时间
    }

    @Override
    public String get() {
        return result;
    }
}
