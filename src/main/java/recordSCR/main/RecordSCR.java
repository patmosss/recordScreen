package recordSCR.main;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;
import recordSCR.module.Module;
import recordSCR.pojo.ScreenInfo;
import recordSCR.utils.ScreenCanvas;
import recordSCR.utils.ScreenUtils;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Windows下录制屏幕
 */
public class RecordSCR implements Module {

    /**
     * <h3 color='#4B0082'>录制屏幕的方法 (方法一)</h3>
     * @param outputFile 输出文件/地址(可以是本地文件，也可以是流媒体服务器地址)
     * @param audio_device_index 音频设备，本机默认是4
     * @param framerate 视频帧率:最低 25(即每秒25张图片,低于25就会出现闪屏)
     */
    public void recordingScreen(String outputFile, int audio_device_index, int framerate, String screen_device_index) throws Exception {

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop"); //读取屏幕

        grabber.setFormat("gdigrab"); // 基于gdigrab的输入格式

        grabber.setOption("framerate", "60"); //设置60帧每秒的高帧率
        grabber.setOption("offset_x", "0"); //截屏起始点X，全屏录制不设置此参数
        grabber.setOption("offset_y", "0"); //截屏起始点Y，全屏录制不设置此参数


        // TODO: 录取不同的屏幕设备[待完成] 目前是2块屏幕 0:3000*2000 1:1920*1080
        for (Map.Entry<String, ScreenInfo> ss : ScreenUtils.getSCRInfo().entrySet()) {
            if (ss.getKey().equals(screen_device_index) && ss.getKey().equals("0")) {
                grabber.setImageWidth(ss.getValue().getScrWidth()); //截取的画面宽度，不设置此参数默认为全屏 4920*2000
                grabber.setImageHeight(ss.getValue().getScrHeight()); //截取的画面高度，不设置此参数默认为全屏
            }
        }

        grabber.setOption("draw_mouse", "1"); //绘制鼠标：1 隐藏鼠标：0
        start(grabber);

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage(); //转换器
        IplImage grabbedImage = converter.convert(grabber.grab());


        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabbedImage.width(), grabbedImage.height(), 2);
        recorder.setInterleaved(true);

        recorder.setVideoOption(AVType.Tune.key, "zerolatency");

        recorder.setVideoOption(AVType.Preset.key, "ultrafast");

        recorder.setVideoOption(AVType.Constant_Rate_Factor.key, String.valueOf(framerate));

        recorder.setVideoBitrate(2000000); // 2000 kb/s, 720P视频的合理比特率范围

        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // h264编/解码器

        recorder.setFormat(AVType.Flv.key); // 封装格式flv

        recorder.setFrameRate(framerate); // 视频帧率(保证视频质量的情况下最低25，低于25会出现闪屏)

        recorder.setGopSize(framerate * 2); // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍

        recorder.setAudioOption(AVType.Constant_Rate_Factor.key, "0"); // 不可变(固定)音频比特率

        recorder.setAudioQuality(0); // 最高质量

        recorder.setAudioBitrate(192000); // 音频比特率

        recorder.setSampleRate(44100); // 音频采样率

        recorder.setAudioChannels(2); // 双通道(立体声)

        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC); // 音频编/解码器

        start(recorder);

        // 音频捕获
        new Thread(() -> {

            /*
              设置音频编码器 最好是系统支持的格式，否则getLine() 会发生错误
              采样率:44.1k;采样率位数:16位;立体声(stereo);是否签名;true:
              big-endian字节顺序,false:little-endian字节顺序(详见:ByteOrder类)
             */
            AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

            // 通过AudiSystem获取本地音频混合器信息
            Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();

            // 通过AudioSystem获取本地音频混合器
            Mixer mixer = AudioSystem.getMixer(minfoSet[audio_device_index]);

            // 通过设置好的音频编解码器获取数据线信息
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            try {
            /*
                打开并开始捕获音频，通过line可以获得更多控制权
                获取设备：TargetDataLine line = (TargetDataLine)mixer.getLine(dataLineInfo);
             */
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                line.open(audioFormat);
                line.start();

                int sampleRate = (int) audioFormat.getSampleRate(); // 获取当前音频采样率

                int numChannels = audioFormat.getChannels(); // 获取当前音频通过数量

                int audioBufferSize = sampleRate * numChannels; // 初始化音频缓冲区(size是音频采样率*通道数)

                byte[] audioBytes = new byte[audioBufferSize];

                ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
                exec.scheduleAtFixedRate(() -> {
                    try {
                        // 非阻塞方式读取
                        int nBytesRead = line.read(audioBytes, 0, line.available());
                        // 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
                        int nSamplesRead = nBytesRead / 2;
                        short[] samples = new short[nSamplesRead];

                        /*
                          ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
                          ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序，解码器定义的
                          ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
                          ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
                         */
                        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                        // 将short[]包装到ShortBuffer
                        ShortBuffer sBuffer = ShortBuffer.wrap(samples, 0, nSamplesRead);
                        // 按通过录制shortBuffer
                        recorder.recordSamples(sampleRate, numChannels, sBuffer);
                    } catch (FFmpegFrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }, 0, (long) 1000 / framerate , TimeUnit.MILLISECONDS);

            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();

        long startTime = 0;
        long videoTS = 0;

        CanvasFrame cFrame = ScreenCanvas.canvas(grabber);

        Frame rotatedFrame = converter.convert(grabbedImage);
        //Frame capturedFrame = null;
        // 执行抓取(capture)过程
        while (cFrame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            if (cFrame.isVisible()) {
                // 本机预览要发送的帧
                rotatedFrame = converter.convert(grabbedImage);
                cFrame.showImage(rotatedFrame);
            }
            // 定义我们的开始时间，当开始时需要先初始化时间戳
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            // 创建一个 timestamp用来写入帧中
            videoTS = 1000 * (System.currentTimeMillis() - startTime);

            // 检查偏移量
            if (videoTS > recorder.getTimestamp()) {
                System.out.println("Lip-flap correction: " + videoTS + " : " + recorder.getTimestamp() + " -> " + (videoTS - recorder.getTimestamp()));
                //告诉录制器写入这个timestamp
                recorder.setTimestamp(videoTS);
            }

            // 发送帧
            record(recorder, rotatedFrame);
        }

        stop(grabber, recorder, cFrame);
    }

    /**
     * <h3 color='#4B0082'>录制屏幕的方法 (方法二 代理)</h3>
     * @param outputFile 输出文件/地址(可以是本地文件，也可以是流媒体服务器地址)
     * @param audio_device_index 音频设备，本机默认是4
     * @param framerate 视频帧率:最低 25(即每秒25张图片,低于25就会出现闪屏)
     */
    @Override
    public void recordScreen(String outputFile, int audio_device_index, int framerate, String screen_device_index) throws Exception {

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop"); //读取屏幕

        grabber.setFormat("gdigrab"); // 基于gdigrab的输入格式

        grabber.setOption("framerate", "60"); //设置60帧每秒的高帧率
        grabber.setOption("offset_x", "0"); //截屏起始点X，全屏录制不设置此参数
        grabber.setOption("offset_y", "0"); //截屏起始点Y，全屏录制不设置此参数


        // TODO: 录取不同的屏幕设备[待完成] 目前是2块屏幕 0:3000*2000 1:1920*1080
        for (Map.Entry<String, ScreenInfo> ss : ScreenUtils.getSCRInfo().entrySet()) {
            if (ss.getKey().equals(screen_device_index) && ss.getKey().equals("0")) {
                grabber.setImageWidth(ss.getValue().getScrWidth()); //截取的画面宽度，不设置此参数默认为全屏 4920*2000
                grabber.setImageHeight(ss.getValue().getScrHeight()); //截取的画面高度，不设置此参数默认为全屏
            }
        }

        grabber.setOption("draw_mouse", "1"); //绘制鼠标：1 隐藏鼠标：0
        start(grabber);

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage(); //转换器
        IplImage grabbedImage = converter.convert(grabber.grab());



        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabbedImage.width(), grabbedImage.height(), 2);
        recorder.setInterleaved(true);

        recorder.setVideoOption(AVType.Tune.key, "zerolatency");

        recorder.setVideoOption(AVType.Preset.key, "ultrafast");

        recorder.setVideoOption(AVType.Constant_Rate_Factor.key, String.valueOf(framerate));

        recorder.setVideoBitrate(2000000); // 2000 kb/s, 720P视频的合理比特率范围

        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // h264编/解码器

        recorder.setFormat(AVType.Flv.key); // 封装格式flv

        recorder.setFrameRate(framerate); // 视频帧率(保证视频质量的情况下最低25，低于25会出现闪屏)

        recorder.setGopSize(framerate * 2); // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍

        recorder.setAudioOption(AVType.Constant_Rate_Factor.key, "0"); // 不可变(固定)音频比特率

        recorder.setAudioQuality(0); // 最高质量

        recorder.setAudioBitrate(192000); // 音频比特率

        recorder.setSampleRate(44100); // 音频采样率

        recorder.setAudioChannels(2); // 双通道(立体声)

        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC); // 音频编/解码器

        start(recorder);

        // 音频捕获
        new Thread(() -> {

            /*
              设置音频编码器 最好是系统支持的格式，否则getLine() 会发生错误
              采样率:44.1k;采样率位数:16位;立体声(stereo);是否签名;true:
              big-endian字节顺序,false:little-endian字节顺序(详见:ByteOrder类)
             */
            AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

            // 通过AudiSystem获取本地音频混合器信息
            Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();

            // 通过AudioSystem获取本地音频混合器
            Mixer mixer = AudioSystem.getMixer(minfoSet[audio_device_index]);

            // 通过设置好的音频编解码器获取数据线信息
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            try {
            /*
                打开并开始捕获音频，通过line可以获得更多控制权
                获取设备：TargetDataLine line = (TargetDataLine)mixer.getLine(dataLineInfo);
             */
                TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                line.open(audioFormat);
                line.start();

                int sampleRate = (int) audioFormat.getSampleRate(); // 获取当前音频采样率

                int numChannels = audioFormat.getChannels(); // 获取当前音频通过数量

                int audioBufferSize = sampleRate * numChannels; // 初始化音频缓冲区(size是音频采样率*通道数)

                byte[] audioBytes = new byte[audioBufferSize];

                ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
                exec.scheduleAtFixedRate(() -> {
                    try {
                        // 非阻塞方式读取
                        int nBytesRead = line.read(audioBytes, 0, line.available());
                        // 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
                        int nSamplesRead = nBytesRead / 2;
                        short[] samples = new short[nSamplesRead];

                        /*
                          ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
                          ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序，解码器定义的
                          ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
                          ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
                         */
                        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                        // 将short[]包装到ShortBuffer
                        ShortBuffer sBuffer = ShortBuffer.wrap(samples, 0, nSamplesRead);
                        // 按通过录制shortBuffer
                        recorder.recordSamples(sampleRate, numChannels, sBuffer);
                    } catch (FFmpegFrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }, 0, (long) 1000 / framerate , TimeUnit.MILLISECONDS);

            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();

        long startTime = 0;
        long videoTS = 0;

        CanvasFrame cFrame = ScreenCanvas.canvas(grabber);

        Frame rotatedFrame = converter.convert(grabbedImage);
        //Frame capturedFrame = null;
        // 执行抓取(capture)过程
        while (cFrame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            if (cFrame.isVisible()) {
                // 本机预览要发送的帧
                rotatedFrame = converter.convert(grabbedImage);
                cFrame.showImage(rotatedFrame);
            }
            // 定义我们的开始时间，当开始时需要先初始化时间戳
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            // 创建一个 timestamp用来写入帧中
            videoTS = 1000 * (System.currentTimeMillis() - startTime);

            // 检查偏移量
            if (videoTS > recorder.getTimestamp()) {
                System.out.println("Lip-flap correction: " + videoTS + " : " + recorder.getTimestamp() + " -> " + (videoTS - recorder.getTimestamp()));
                //告诉录制器写入这个timestamp
                recorder.setTimestamp(videoTS);
            }

            // 发送帧
            record(recorder, rotatedFrame);
        }
        stop(grabber, recorder, cFrame);
    }

    /**
     * TODO: 水印功能
     */
    @Override
    public void watermark() {
        int i = 1;
        System.out.println("实现水印功能");
    }


    private void record(FFmpegFrameRecorder recorder, Frame frame) {
        try {
            Thread.sleep(40);
            recorder.record(frame);
        } catch (Exception e) {
            System.out.println("录制帧发送异常...");
        }
    }

    private void start(FFmpegFrameGrabber grabber) {
        try {
            grabber.start();
        } catch (FFmpegFrameGrabber.Exception e) {
            try {
                System.out.println("首次打开抓取器失败，准备重启抓取器");
                grabber.restart();
            } catch (FrameGrabber.Exception e1) {
                try {
                    System.out.println("重启抓取器失败，正在关闭抓取器");
                    grabber.stop();
                    System.exit(0);
                } catch (FFmpegFrameGrabber.Exception e2) {
                    System.exit(0);
                }
            }
        }
    }

    private void start(FFmpegFrameRecorder recorder) {
        try {
            System.out.println("开始录制...");
            recorder.start();
        } catch (FFmpegFrameRecorder.Exception e) {
            try {
                System.out.println("首次打开录制器失败! 准备重启录制器...");
                recorder.stop();
                recorder.start();
            } catch (FFmpegFrameRecorder.Exception exception) {
                try {
                    System.out.println("重启录制器失败，正在停止录制器...");
                    recorder.stop();
                    System.exit(0);
                } catch (FFmpegFrameRecorder.Exception ex) {
                    System.exit(0);
                }

            }
        }
    }

    private void stop(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, CanvasFrame canvasFrame) {
        canvasFrame.dispose();
        try {
            if (recorder != null) {
                recorder.stop();
            }
        } catch (FFmpegFrameRecorder.Exception e) {
            System.out.println("关闭录制器失败");
            try {
                if (recorder != null) {
                    grabber.stop();
                }
            } catch (FFmpegFrameGrabber.Exception exception) {
                System.out.println("关闭摄像头失败");
                System.exit(0);
            }
        }
        try {
            if (recorder != null) {
                grabber.stop();
            }
        } catch (FFmpegFrameGrabber.Exception e) {
            System.out.println("关闭摄像头失败");
        }
    }

}
