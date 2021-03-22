package com.example.ios;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> strs = new ArrayList<>();
        strs.add("111");
        strs.add("222");
        strs.add("333");
        strs.forEach(appItem -> {
            if (appItem.equals("222")) {
                return;
            }
            System.out.println(appItem);
        });
    }
}
