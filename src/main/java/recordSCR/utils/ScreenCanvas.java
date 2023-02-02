package recordSCR.utils;


import java.awt.*;
import javax.swing.*;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;

public class ScreenCanvas {

    private static final int DEFAULT_WIDTH =  100;
    private static final int DEFAULT_HEIGHT = 100;

    public static CanvasFrame canvas(FFmpegFrameGrabber grabber)  {

        // javaCV提供了优化非常好的硬件加速组件来帮助显示我们抓取的摄像头视频
        CanvasFrame cFrame = new CanvasFrame("桌面录制", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        cFrame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        cFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口则退出程序
        //cFrame.setResizable(true);
        cFrame.setVisible(true);

        //cFrame.setLayout(new FlowLayout());  //创建一个新的流布局管理器, 具有指定的对齐方式以及指定的水平和垂直间隙
        cFrame.setLayout(new BorderLayout());

        return cFrame;
    }
}
