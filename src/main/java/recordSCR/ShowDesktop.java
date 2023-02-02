package recordSCR;

import recordSCR.main.RecordSCR;
import recordSCR.pojo.AudioType;
import recordSCR.pojo.BaseInfo;
import recordSCR.pojo.WatermarkInfo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ShowDesktop {
    public static void main(String[] args) throws Exception{
        RecordSCR recordSCR = new RecordSCR();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        BaseInfo bi1 = new BaseInfo("D:\\Temp\\" + date + UUID.randomUUID() +".flv",
                4, 25, AudioType.STEREO, "0");

        /*
          录制屏幕
          @param outputFile 输出文件/地址(可以是本地文件，也可以是流媒体服务器地址)
          @param audio_device_index 音频设备，本机默认是4
          @param framerate 视频帧率:最低 25(即每秒25张图片,低于25就会出现闪屏)
          @param screen_device_index 0: 本机设备 1：全屏
         */
        WatermarkInfo wi1 = new WatermarkInfo("wi1");
        recordSCR.recordingScreen(bi1, wi1);

        /*
            outputFile:输出文件/地址(可以是本地文件，也可以是流媒体服务器地址)
            audio_device_index 音频设备，本机默认是4
            framerate 视频帧率:最低 25(即每秒25张图片,低于25就会出现闪屏)
            screen_device_index 0: 本机设备 1：全屏
            watermarkText: 水印文字（暂时无法识别中文）
         */



        // 水印文字 水印图片路径 是否水印图片桌面置上
        WatermarkInfo wi = new WatermarkInfo("test1", "D:\\Temp\\1.jpg", 0);
        //WatermarkInfo wi = new WatermarkInfo("wi1");
        //WatermarkInfo wi = new WatermarkInfo("D:\\Temp\\1.jpg", 0);

        //recordSCR.recordScreen(bi1, wi);

    }
}
