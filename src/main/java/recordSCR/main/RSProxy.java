package recordSCR.main;

import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import recordSCR.module.Module;
import recordSCR.pojo.WatermarkInfo;

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
                        // 设置水印的颜色等
                        System.out.println("前 增强参数方法等 水印! ");
                    }

                    if ("recordScreen".equals(method.getName())) {
                        Class wmClazz = WatermarkInfo.class;
                        WatermarkInfo wi = new WatermarkInfo();
                        Field[] fields = wmClazz.getFields();
                        System.out.println(Arrays.toString(fields));

                        Field point = wmClazz.getDeclaredField("point");
                        Field scalar = wmClazz.getDeclaredField("scalar");

                        point.set(wi, new Point(300,400));
                        scalar.set(wi, new Scalar(0,0,0,0));

                        System.out.println(wi);




                        /*Field point = wmClazz.getDeclaredField("point");
                        point.set(wi, new Point(500, 600));
                        Object o1 = point.get(wi);
                        System.out.println(o1.toString());*/

                        // 水印文字位置
                        Point point2 = new Point(200, 200);
                        // 颜色
                        Scalar scalar2 = new Scalar(255, 0, 0, 0);
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
