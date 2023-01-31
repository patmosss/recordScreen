package recordSCR.utils;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ScreenCanvas  {


    public static CanvasFrame canvas(FFmpegFrameGrabber grabber)  {
        // javaCV提供了优化非常好的硬件加速组件来帮助显示我们抓取的摄像头视频
        CanvasFrame cFrame = new CanvasFrame("桌面录制", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        cFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口则退出程序
        cFrame.setBounds(600, 300, 500, 400);
        //cFrame.setAlwaysOnTop(true);
        //cFrame.setResizable(true);

        JButton jbt = new JButton("1");

        JButton startBtn = new JButton("开始录制");
        JButton EndBtn = new JButton("结束录制");
        //EndBtn.setBounds(27,5, 10, 5);


        cFrame.setSize(600, 600);

        //cFrame.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 20));  //创建一个新的流布局管理器, 具有指定的对齐方式以及指定的水平和垂直间隙
        cFrame.setLayout(new BorderLayout());
        cFrame.setLocationRelativeTo(null); //窗口置于屏幕中央

        cFrame.add(jbt);
        cFrame.setVisible(true);

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("开始录制");
            }
        });

        EndBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("结束录制");
            }
        });

        cFrame.add(startBtn);
        cFrame.add(EndBtn);
        cFrame.setVisible(true);

        Canvas canvas = cFrame.getCanvas();


        // 对canvas设置鼠标监听事件
        canvas.addMouseListener(new MouseListener() {

            //处理鼠标单击事件方法
            @Override
            public void mouseClicked(MouseEvent e) {
                // 控制台输出点击的坐标
                System.out.println("x: " + e.getX());
                System.out.println("y: " + e.getY());
            }

            // 按下鼠标按键时执行方法
            @Override
            public void mousePressed(MouseEvent e) {
                int button = e.getButton();
                System.out.println(button);

            }

            // 释放鼠标按键时执行方法
            @Override
            public void mouseReleased(MouseEvent e) {

            }

            // 鼠标进入组件区域时执行方法
            @Override
            public void mouseEntered(MouseEvent e) {

            }
            // 鼠标离开组件区域执行方法
            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        return cFrame;
    }
}
