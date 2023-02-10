package recordSCR.main;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import recordSCR.pojo.BaseInfo;
import recordSCR.pojo.ScreenInfo;
import recordSCR.pojo.WatermarkInfo;
import recordSCR.utils.ScreenCanvas;
import recordSCR.utils.ScreenUtils;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Windows下录制屏幕
 */
public class RecordSCR {

    private static String format;
    private static String framerates;
    private static String offset_x;
    private static String offset_y;
    private static String draw_mouse;
    private static String tune_v;
    private static String preset_v;
    private static int bitrate_v;
    private static String crf_a;
    private static int quality_a;
    private static int bitrate_a;
    private static int samplerate_a;

    // 音频捕获
    private static float sampleRate;
    private static int sampleSizeInBits;
    private static boolean signed;
    private static boolean bigEndian;

    private Mat logo = null;
    private Mat mask = null;

    private static Properties p = new Properties();


    static {
        URL resource = RecordSCR.class.getClassLoader().getResource("config/recordScreen.properties");

        try {

            String path = resource.getPath();
            if (path != null) {
                p.load(new FileReader(path));
                format = p.getProperty("rs.format");
                framerates = p.getProperty("rs.framerate");
                offset_x = p.getProperty("rs.offset_x");
                offset_y = p.getProperty("rs.offset_y");
                draw_mouse = p.getProperty("rs.draw_mouse");
                tune_v = p.getProperty("rs.tune_v");
                preset_v = p.getProperty("rs.preset_v");
                bitrate_v = Integer.parseInt(p.getProperty("rs.bitrate_v"));
                crf_a = p.getProperty("rs.constant_rate_factor_a");
                quality_a = Integer.parseInt(p.getProperty("rs.quality_a"));
                bitrate_a = Integer.parseInt(p.getProperty("rs.bitrate_a"));
                samplerate_a = Integer.parseInt(p.getProperty("rs.samplerate_a"));
                sampleRate = Float.parseFloat(p.getProperty("rs.sampleRate"));
                sampleSizeInBits = Integer.parseInt(p.getProperty("rs.sampleSizeInBits"));
                signed = Boolean.parseBoolean(p.getProperty("rs.signed"));
                bigEndian = Boolean.parseBoolean(p.getProperty("rs.bigEndian"));
            }
        } catch (IOException e) {
            System.out.println("找不到配置文件");
            e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * <h3 color='#4B0082'>录制屏幕的方法 (方法一)</h3>
     */
    public void recordingScreen(BaseInfo baseInfo, WatermarkInfo watermarkInfo) throws Exception {

        if (baseInfo != null) {

            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(AVType.DESKTOP); //读取屏幕

            grabber.setFormat(format);

            grabber.setOption(AVType.FRAMERATE, framerates);
            grabber.setOption(AVType.OFFSET_X, offset_x);
            grabber.setOption(AVType.OFFSET_Y, offset_y);


            for (Map.Entry<String, ScreenInfo> ss : ScreenUtils.getSCRInfo().entrySet()) {
                if (ss.getKey().equals(baseInfo.getScreen_device_index()) && ss.getKey().equals("0")) {
                    grabber.setImageWidth(ss.getValue().getScrWidth()); //截取的画面宽度，不设置此参数默认为全屏 4920*2000
                    grabber.setImageHeight(ss.getValue().getScrHeight()); //截取的画面高度，不设置此参数默认为全屏
                }
            }

            grabber.setOption(AVType.DRAW_MOUSE, draw_mouse);
            jmarkInfo(watermarkInfo);  //判断水印详情
            start(grabber);

            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            IplImage grabbedImage = converter.convert(grabber.grab());


            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(baseInfo.getOutputFile(), grabbedImage.width(), grabbedImage.height(), baseInfo.getIsOpenAudio().getV());
            recorder.setInterleaved(true);

            recorder.setVideoOption(AVType.TUNE, tune_v);

            recorder.setVideoOption(AVType.PRESET, preset_v);

            recorder.setVideoOption(AVType.CONSTANT_RATE_FACTOR, String.valueOf(baseInfo.getFramerate()));

            recorder.setVideoBitrate(bitrate_v);

            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // h264编/解码器

            recorder.setFormat(AVType.FLV);

            recorder.setFrameRate(baseInfo.getFramerate());

            recorder.setGopSize(baseInfo.getFramerate() * 2); // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍

            recorder.setAudioOption(AVType.CONSTANT_RATE_FACTOR, crf_a);

            recorder.setAudioQuality(quality_a);

            recorder.setAudioBitrate(bitrate_a);

            recorder.setSampleRate(samplerate_a);

            recorder.setAudioChannels(baseInfo.getIsOpenAudio().getV());

            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC); // 音频编/解码器

            start(recorder);

            // 音频捕获
            new Thread(() -> {

            /*
              设置音频编码器 最好是系统支持的格式，否则getLine() 会发生错误
              采样率:44.1k;采样率位数:16位;立体声(stereo);是否签名;true:
              big-endian字节顺序,false:little-endian字节顺序(详见:ByteOrder类)
             */
                AudioFormat audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, baseInfo.getIsOpenAudio().getV(), signed, bigEndian);

                // 通过AudiSystem获取本地音频混合器信息
                //Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();

                // 通过AudioSystem获取本地音频混合器
                //Mixer mixer = AudioSystem.getMixer(minfoSet[baseInfo.getAudio_device_index()]);

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
                    }, 0, (long) 1000 / baseInfo.getFramerate(), TimeUnit.MILLISECONDS);

                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
            }).start();

            long startTime = 0;
            long videoTS = 0;

            CanvasFrame cFrame = ScreenCanvas.canvas(grabber);

            double alpha = 0.5;// 图像透明权重值,0-1之间

            Frame rotatedFrame = converter.convert(grabbedImage);

            // 执行抓取(capture)过程
            while (cFrame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
                if (cFrame.isVisible()) {
                    // 本机预览要发送的帧
                    rotatedFrame = converter.convert(grabbedImage);

                    Mat mat = converter.convertToMat(rotatedFrame);

                    if (watermarkInfo != null) {
                        // 加文字水印，opencv_imgproc.putText（图片，水印文字（无法识别中文），文字位置，字体，字体大小，字体颜色，字体粗度，文字反锯齿，是否翻转文字）
                        opencv_imgproc.putText(mat,
                                watermarkInfo.getText() == null ? "" : watermarkInfo.getText(),
                                watermarkInfo.getPoint(),
                                opencv_imgproc.CV_FONT_VECTOR0,
                                watermarkInfo.getFontSize(),
                                watermarkInfo.getScalar(),
                                watermarkInfo.getFontThickness(),
                                watermarkInfo.getText_antialiasing(),
                                watermarkInfo.getIsFlip());

                        if (logo != null) {
                            // 定义感兴趣区域(位置，logo图像大小)
                            Mat ROI = mat.apply(new Rect(400, 350, logo.cols(), logo.rows()));
                            opencv_core.addWeighted(ROI, alpha, logo, 1.0 - alpha, 0.0, ROI);
                            // 把logo图像复制到感兴趣区域

                            if (watermarkInfo.getIsCover() == 1) {
                                logo.copyTo(ROI, mask);
                            }
                        }
                    }
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
    }

    private void jmarkInfo(WatermarkInfo watermarkInfo) {
        if (watermarkInfo != null) {
            String imagePath = watermarkInfo.getImagePath();
            if (imagePath != null) {
                File f = new File(imagePath);
                if (f.exists() && f.isFile()) {
                    logo = opencv_imgcodecs.imread(imagePath);
                    mask = opencv_imgcodecs.imread(imagePath, 0);
                    opencv_imgproc.threshold(mask, mask,254,255, opencv_imgcodecs.IMWRITE_PNG_BILEVEL);
                } else {
                    System.out.println("水印图片路径出错,请重试!");
                    System.exit(0);
                }
            }
        }
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
