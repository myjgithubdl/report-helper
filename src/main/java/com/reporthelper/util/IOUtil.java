package com.reporthelper.util;

import org.springframework.util.StreamUtils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil extends StreamUtils {


    /**
     * 自动关闭
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if(closeable != null ){
                closeable.close();
            }
        }catch (IOException e){

        }

    }
}
