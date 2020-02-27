package com.speedata.temperture;

import android.serialport.DeviceControlSpd;
import android.serialport.SerialPortSpd;
import android.util.Log;

import com.speedata.libutils.DataConversionUtils;
import com.speedata.temperture.utils.SwitchTemperature;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :EchoXBR in  2020-02-27 15:56.
 * 功能描述:TN901 和TN905 测温模块
 */
public class TN90Model implements ITempertureInterface {

    ReadThread thread;
    boolean isOne = false;
    Temperture temperture;
    TempertureCallBack callBack;

    /**
     * 串口对象
     */
    private SerialPortSpd serialPort;
    /**
     * 串口文件句柄  根据句柄进行读写操作
     */
    private int fd;
    /**
     * 给模块上电对象
     */
    private DeviceControlSpd control;
    private String TAG = "TN90Model";

    private void parseData(byte[] temp) {

        //目标温度
        byte[] mubiaotemp = new byte[2];
        // 环境温度
        byte[] huanjingtemp = new byte[2];
        //for (int i = 0; i < temp.length; i += 5) {
        // 头为0x4c 对应十进制为76  则此组数据为目标温度
        if (temp[0] == 76) {
            byte sum = (byte) (temp[0] + temp[1] + temp[2]);
            byte ww = temp[3];
            if (sum == ww) {
                //copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                System.arraycopy(temp, 1, mubiaotemp, 0, 2);
                float targetTmp1 = ((float) DataConversionUtils.byteArrayToInt(mubiaotemp)) / 16;
                if (targetTmp1 == 0) {
                    return;
                }
                float targetTemp = (targetTmp1 - 273);
                temperture.setTargetTemp(targetTemp);
                temperture.setBodyTemp(Float.parseFloat(SwitchTemperature.switchTemp(saveOnePoint(targetTemp))));
                Log.v("LogTemp", "Temprature is : " + targetTemp);
            } else {
                Log.e("ErrorPack", "check error mubiao  " + DataConversionUtils.byteArrayToStringLog(temp, temp.length));
                return;
            }
        }
        // 头为0x66 对应十进制为102  则此组数据为环境温度
        else if (temp[0] == 102) {
            byte sum = (byte) (temp[0] + temp[1] + temp[2]);
            if (sum == temp[3]) {
                //copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                System.arraycopy(temp, 1, huanjingtemp, 0, 2);
                float huanjing = (float) DataConversionUtils.byteArrayToInt(huanjingtemp);
                float ss = huanjing / 16;
                float environmentTemp = (float) (ss - 273.15);
                temperture.setEnvironmentTemp(environmentTemp);
            } else {
                Log.e("ErrorPack", "check error huanjing");
                return;
            }
        }
    }


    @Override
    public int openDev() {
        try {
            //主板上电
            control = new DeviceControlSpd(DeviceControlSpd.PowerType.MAIN, new int[]{93, 94});
            control.PowerOnDevice();
            //打开串口
            serialPort = new SerialPortSpd();
            serialPort.OpenSerial("/dev/ttyMT2", 9600);
            fd = serialPort.getFd();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    @Override
    public void closeDev() {
        serialPort.CloseSerial(fd);
        try {
            control.PowerOffDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startMeasurement(boolean isOne) {
        this.isOne = isOne;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        thread = new ReadThread();
        if (serialPort != null) {
            serialPort.clearPortBuf(fd);
        }
        thread.start();
    }

    @Override
    public void setCallBack(TempertureCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void stopMeasurement() {
        thread.interrupt();
    }

    /**
     * 保留一位小数
     *
     * @param data
     *
     * @return
     */
    public String saveOnePoint(float data) {
        DecimalFormat decimalFormat = new DecimalFormat(".0");//保留一位
        return decimalFormat.format(data);
    }

    /**
     * 线程，阻塞读串口
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    if (temperture != null && temperture.getEnvironmentTemp() != 0 && temperture.getBodyTemp() != 0) {
                        callBack.receTempData(temperture);
                        temperture = new Temperture();
                        if (isOne) {
                            this.interrupt();
                        }
                    }
                    if (temperture == null) {
                        temperture = new Temperture();
                    }
                    byte[] temp1 = serialPort.ReadSerial(fd, 5);
                    if (temp1 != null && (temp1[0] == 76 || temp1[0] == 102)) {
                        parseData(temp1);
                    } else {
                        Log.e("ReadThread", "Buffer incorrect" + DataConversionUtils.byteArrayToString(temp1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
