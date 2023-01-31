package recordSCR.main;

import recordSCR.module.Module;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author: tree
 * @description:
 * @date: 2023/1/31
 */
public class RSProxy {

    public static void recording(String outputFile, int audio_device_index, int framerate, String screen_device_index) {

        Module a = new RecordSCR(); //被代理对象

        Module b = (Module) Proxy.newProxyInstance(
                a.getClass().getClassLoader(),
                a.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    if ("watermark".equals(method.getName())) {
                        System.out.println("前 增强参数方法等 水印! ");
                    }

                    if ("recordScreen".equals(method.getName())) {
                        System.out.println("前 增强录制的参数方法等");


                    }

                    Object result = method.invoke(a, args);

                    if ("watermark".equals(method.getName())) {
                        System.out.println("后 增强参数方法等 水印! ");
                    }


                    return result;
                });

        System.out.println("---");
        b.watermark();
        System.out.println("--");

        try {
            b.recordScreen(outputFile, audio_device_index, framerate, screen_device_index);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("录制出错");
            //System.exit(0);
        }
    }
}
