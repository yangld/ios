package com.example.i18n;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18nTest {
    public static void main(String[] args) {
        // # Locale NumberFormat DateFormat MessageFormat ResourceBundle
        System.out.println("test");
        Locale locale = new Locale("zh", "CN");
        System.out.println(locale);
        System.out.println(Locale.getDefault());
        // java -Duser.language=en -Duser.region=US MyTest。
        NumberFormat currFmt = NumberFormat.getCurrencyInstance(locale);
        double amt = 123456.78;
        System.out.println(currFmt.format(amt));
//        locale = new Locale("en", "US");
        Date date = new Date();
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        System.out.println(df.format(date));


        //①信息格式化串

        //②用于动态替换占位符的参数
        Object[] params = {"John", new GregorianCalendar().getTime(), 1.0E3};

        String pattern1 = "{0}，你好！你于 {1} 在工商银行存入 {2} 元。";
        //③使用默认本地化对象格式化信息
        String msg1 = MessageFormat.format(pattern1, params);
        System.out.println(msg1);

        String pattern2 = "At {1,time,short} On {1,date,long}，{0} paid {2,number, currency}.";
        //④使用指定的本地化对象格式化信息
        MessageFormat mf = new MessageFormat(pattern2, Locale.US);
        String msg2 = mf.format(params);
        System.out.println(msg2);


        ResourceBundle rb = ResourceBundle.getBundle("resource", Locale.US);
        System.out.println(rb.getString("greeting.common"));
    }
}
