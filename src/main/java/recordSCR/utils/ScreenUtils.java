package recordSCR.utils;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import recordSCR.pojo.ScreenInfo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 屏幕工具类
 */
public class ScreenUtils {

    public static HashMap<String, ScreenInfo> getSCRInfo() {

        HashMap<String, ScreenInfo> scrInfoMap = new HashMap<>();

        GraphicsEnvironment localGrapEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices = localGrapEnv.getScreenDevices();
        for (GraphicsDevice device : devices) {
            String id = Pattern.compile("[^0-9]").matcher(device.getIDstring()).replaceAll("").trim();
            ScreenInfo si = new ScreenInfo(device.getDisplayMode().getHeight(), device.getDisplayMode().getWidth());
            scrInfoMap.put(id, si);

            System.out.println(device.getDisplayMode().toString());

            System.out.println("ID:"+device.getIDstring());
        }
        return scrInfoMap;
    }


    public static CanvasFrame method(FFmpegFrameGrabber grabber) {
        //JFrame frame = new JFrame("JFrame");
        // javaCV提供了优化非常好的硬件加速组件来帮助显示我们抓取的摄像头视频
        CanvasFrame frame = new CanvasFrame("桌面录制", CanvasFrame.getDefaultGamma() / grabber.getGamma());


       /* frame.setAlwaysOnTop(true);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null); //窗口置于屏幕中央
        frame.setVisible(true);*/


        JButton bt1 = new JButton("开始录制");
        JButton bt2 = new JButton("关闭录制");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(200, 200); //窗体屏幕长和宽
        //frame.setBounds(1,2,20,10);
        //frame.setLocation(1,2); //设置一个左上角顶点在（1，2）的窗体。

        frame.setLayout(new FlowLayout()); //创建一个新的流布局管理器, 具有指定的对齐方式以及指定的水平和垂直间隙

        frame.setLocationRelativeTo(null); //窗口置于屏幕中央

        // 向frame中添加一个按钮
        bt1.addActionListener(new MyListener()); //为按钮添加一个实现ActionListener接口的对象
        bt2.addActionListener(e -> System.out.println("b clicked!"));

        frame.add(bt1);
        frame.add(bt2);

        frame.setVisible(true);

        return frame;
    }

    private static class MyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("a clicked!");
        }
    }
}
