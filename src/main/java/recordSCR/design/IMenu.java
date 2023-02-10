package recordSCR.design;

import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.lang.reflect.InvocationTargetException;

/**
 * @author: tree
 * @description:
 * @date: 2023/2/10
 */
public interface IMenu {
    IMenu appendRecording(Module module) throws FFmpegFrameGrabber.Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    //IMenu appendWatermark(Module module) throws FFmpegFrameGrabber.Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
