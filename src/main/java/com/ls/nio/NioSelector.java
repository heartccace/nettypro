package com.ls.nio;

import java.io.IOException;
import java.nio.channels.Selector;

/**
 * @author heartccace
 * @create 2020-04-30 16:16
 * @Description TODO
 * @Version 1.0
 */
public class NioSelector {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
    }
}
