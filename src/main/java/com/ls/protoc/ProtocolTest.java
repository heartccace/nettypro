package com.ls.protoc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ls.proto.DataInfo;

/**
 * @author heartccace
 * @create 2020-04-29 14:52
 * @Description TODO
 * @Version 1.0
 */
public class ProtocolTest {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        DataInfo.Student student = DataInfo.Student.newBuilder()
                .setId(21)
                .setName("protocol")
                .setAddress("china")
                .build();
        byte[] bytes = student.toByteArray();

        DataInfo.Student student2 =  DataInfo.Student.parseFrom(bytes);
        System.out.println(student2);

    }
}
