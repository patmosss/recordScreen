package recordSCR.design;


import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.lang.reflect.InvocationTargetException;

public interface Module {

    void scene() throws FFmpegFrameGrabber.Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    String desc();
}
