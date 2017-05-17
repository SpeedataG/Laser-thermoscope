package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DeviceControl {
    private BufferedWriter CtrlFile;

    public DeviceControl(String path) {
        File DeviceName = new File(path);
        try {
            CtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void PowerOnDevice(String gpio) // poweron barcode device
    {
        try {
            CtrlFile.write("-wmode" + gpio + " 0");   //将GPIO99设置为GPIO模式
            CtrlFile.flush();
            CtrlFile.write("-wdir" + gpio + " 1");        //将GPIO99设置为输出模式
            CtrlFile.flush();
            CtrlFile.write("-wdout" + gpio + " 1");
            CtrlFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void PowerOffDevice(String gpio) // poweroff barcode device
    {
        try {
            CtrlFile.write("-wdout" + gpio + " 0");
            CtrlFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void DeviceClose() throws IOException // close file
    {
        CtrlFile.close();
    }
}