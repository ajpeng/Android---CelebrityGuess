package com.example.connectlib;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MyClass {

    public class DownloadClass extends InputStream{

        @Override
        public int read() throws IOException {
            return 0;
        }
    }

    public static void main(String[] args){

        //DownloadTask task = new DownloadTask();
        URL url;

    }
}
