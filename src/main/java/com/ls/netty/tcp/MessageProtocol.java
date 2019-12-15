package com.ls.netty.tcp;

/**
 * @author liushuang
 * @create 2019-12-14 14:39
 */
public class MessageProtocol {
    private int length;
    private byte[] data;

    public MessageProtocol() {
    }

    public MessageProtocol(byte[] data,int length) {
        this.length = length;
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
