package recordSCR;


import org.bytedeco.javacv.FFmpegFrameGrabber;
import recordSCR.design.Builder;
import recordSCR.pojo.AudioType;
import recordSCR.pojo.BaseInfo;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ShowDesktop2 {
    public static void main(String[] args) throws FFmpegFrameGrabber.Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Builder b = new Builder();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        BaseInfo info = new BaseInfo("D:\\Temp\\" + date + UUID.randomUUID() +".flv",
                4, 25, AudioType.STEREO, "0");

        b.base(info);
    }
}
