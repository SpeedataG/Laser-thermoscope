package speedatacom.tn9hongwaitemp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;

import util.DataConversionUtils;
import util.DeviceControl;
import util.PlaySoundPool;
import util.SwitchTemperature;

public class RedActivity extends Activity implements View.OnClickListener {
    private final String TAG = "RedActivity";

    /**
     * 串口对象
     */
    private SerialPort serialPort;
    /**
     * 串口文件句柄  根据句柄进行读写操作
     */
    private int fd;
    /**
     * 给模块上电对象
     */
    private DeviceControl control;

    private TextView tvmubian, tvhuanjing, tvMAX, tvMIN, btnMAX, btnMIN, btnStart;

    private boolean isInit = false;
    private int bSuspicise = 0;
    private int iTestNum = 0;
    private float min = 0;
    private float max = 0;
    private float suspicise = 37;
    private PlaySoundPool playSoundPool;
    private Thread thread;
    /**
     * 串口数据解析
     */
    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handle message"+fd);
            switch (msg.what) {
                case 0:
                    byte[] temp = (byte[]) msg.obj;
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
                            float mubiao = (float) DataConversionUtils.byteArrayToInt(mubiaotemp);
                            float ss = mubiao / 16;
                            if (ss == 0) {
                                return;
                            }
                            float m = (float) (ss - 273);
                            //                                String mresult = saveDecimals(m);
                            String mresult = saveOnePoint(m);
                            if (m > 100) {
                                tvmubian.setTextColor(Color.RED);
                            } else {
                                tvmubian.setTextColor(Color.WHITE);
                            }
                            //                                if(m>37)
                            //                                {
                            //                                    bSuspicise ++;
                            //                                    suspicise = m;
                            //                                    Log.e("suspicise","高温 " + bSuspicise+ ":"+ m);
                            //                                    if(bSuspicise<3)
                            //                                        return;
                            //                                }
                            //                                bSuspicise = 0;
                            iTestNum++;
                            if (iTestNum >= 1) {
                                iTestNum = 0;
//                                control.PowerOffDevice("94");
//                                control.PowerOffDevice("93");
                                thread.interrupt();
                                btnStart.setEnabled(true);
//                                serialPort.CloseSerial(fd);
                                Log.v("LogTemp", "Stop running for 3 test");
                            }
                            Log.v("LogTemp", "Temprature is : " + m);
                            mresult = SwitchTemperature.switchTemp(mresult, playSoundPool);
                            tvmubian.setText(mresult + "℃");
                            if (m > max) {
                                tvMAX.setText(mresult);
                                max = m;
                            } else if (m < min) {
                                tvMIN.setText(mresult);
                                min = m;
                            }
                            if (!isInit) {
                                max = m;
                                min = m;
                                tvMAX.setText(mresult);
                                tvMIN.setText(mresult);
                                isInit = true;
                            }

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
                            float h = (float) (ss - 273.15);
                            String hresult = saveDecimals(h);
                            tvhuanjing.setText(hresult + "℃");
                        } else {
                            Log.e("ErrorPack", "check error huanjing");
                            return;
                        }
                    }
                    // }
                    //break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read);
        playSoundPool = PlaySoundPool.getPlaySoundPool(this);

        initModule();
        initUI();
    }

    /**
     * 初始化红外测温模块
     */
    private void initModule() {
        try {
            //实例化模块上电对象
            control = new DeviceControl("/sys/class/misc/mtgpio/pin");
            control.PowerOnDevice("94");
            control.PowerOnDevice("93");
            //打开串口
            serialPort = new SerialPort();
            serialPort.OpenSerial("/dev/ttyMT2", 9600);
            fd = serialPort.getFd();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        tvmubian = (TextView) findViewById(R.id.tv_mubiao);
        tvhuanjing = (TextView) findViewById(R.id.tv_huanjing);
        tvMAX = (TextView) findViewById(R.id.tv_max);
        tvMIN = (TextView) findViewById(R.id.tv_min);
        btnMAX = (TextView) findViewById(R.id.btn_max);
        btnMIN = (TextView) findViewById(R.id.btn_min);
        btnStart = (TextView) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        btnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    control.PowerOnDevice("94");
//                    control.PowerOnDevice("93");
//                    initSerialport();
                    serialPort.ClearSerialPort();
                    isInit = false;
                    thread = new ReadThread();
                    thread.start();    //手指按下时触发不停的发送消息
                    Log.v("LogTemp", "Start Thread");
                    btnStart.setEnabled(false);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //control.PowerOffDevice("94");
                    //control.PowerOffDevice("93");
                    //thread.interrupt();
                }
                return false;

            }
        });
        btnMIN.setOnClickListener(this);
        btnMAX.setOnClickListener(this);
    }

    private void initSerialport() {
        try {
            serialPort.OpenSerial("/dev/ttyMT2", 9600);
            fd = serialPort.getFd();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    public String saveDecimals(float f) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String dff = df.format(f);
        return dff;
    }

    @Override
    public void onClick(View v) {
        if (v == btnStart) {

        }
    }

    @Override
    protected void onStop() {
        //        停止读串口
        if (thread != null) {
            thread.interrupt();
        }
        // 下电
        control.PowerOffDevice("94");
        control.PowerOffDevice("93");
        Log.v("LogTemp", "onstop");
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 线程，阻塞读串口，有数据通过handler发送到UI线程
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    byte[] temp1 = serialPort.ReadSerial(fd, 5);
                    if (temp1 != null && (temp1[0] == 76 || temp1[0] == 102)) {
                        Message msg = new Message();
                        msg.obj = temp1;
                        handler.sendMessage(msg);
                    } else {

                        Log.e("ErrorPack", "Buffer incorrect" + DataConversionUtils.byteArrayToString(temp1));
                    }
                    //                    byte[] temp1 = new byte[5];
                    //                    byte[] temp;
                    //                    while (true) {
                    //                        temp = serialPort.ReadSerial(fd, 1);
                    //                        if (temp[0] != 76 && temp[0] != 102) {
                    //                            Log.e("ErrorPack", "Bad frame start :" + Integer.parseInt(temp[0] + "", 10));
                    //                            continue;
                    //                        }
                    //                        break;
                    //                    }
                    //                    temp1[0] = temp[0];
                    //                    temp = serialPort.ReadSerial(fd, 4);
                    //                    temp1[1] = temp[0];
                    //                    temp1[2] = temp[1];
                    //                    temp1[3] = temp[2];
                    //                    temp1[4] = temp[3];
                    //                    Message msg = new Message();
                    //                    msg.obj = temp1;
                    //                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
