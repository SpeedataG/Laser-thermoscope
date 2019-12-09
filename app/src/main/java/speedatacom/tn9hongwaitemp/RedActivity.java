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

    private float min = 0;

    /**
     * 串口数据解析
     */
    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handle message");
            switch (msg.what) {
                case 0:
                    byte[] temp = (byte[]) msg.obj;
                    //目标温度
                    byte[] mubiaotemp = new byte[2];
                    // 环境温度
                    byte[] huanjingtemp = new byte[2];
                    for (int i = 0; i < temp.length; i += 5) {
                        // 头为0x4c 对应十进制为76  则此组数据为目标温度
                        if (temp[i] == 76) {

                            byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                            byte ww = temp[i + 3];
                            if (sum == ww) {
                                //copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                System.arraycopy(temp, i + 1, mubiaotemp, 0, 2);
                                float mubiao = (float) DataConversionUtils.byteArrayToInt(mubiaotemp);
                                float ss = mubiao / 16;
                                float m = (float) (ss - 273.15);
                                String mresult = saveDecimals(m);
                                if (m > 100) {
                                    tvmubian.setTextColor(Color.RED);
                                } else {
                                    tvmubian.setTextColor(Color.WHITE);
                                }
                                tvmubian.setText(mresult + "℃");
                                if (m > min) {
                                    tvMAX.setText(mresult);
                                } else {
                                    tvMIN.setText(mresult);
                                }
                                min = m;
                            } else {
                                Log.e(TAG, "check error mubiao  " + DataConversionUtils.byteArrayToStringLog(temp, temp.length));
                                return;
                            }
                        }
                        // 头为0x66 对应十进制为102  则此组数据为环境温度
                        else if (temp[i] == 102) {
                            byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                            if (sum == temp[i + 3]) {
                                //copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                System.arraycopy(temp, i + 1, huanjingtemp, 0, 2);
                                float huanjing = (float) DataConversionUtils.byteArrayToInt(huanjingtemp);
                                float ss = huanjing / 16;
                                float h = (float) (ss - 273.15);
                                String hresult = saveDecimals(h);
                                tvhuanjing.setText(hresult + "℃");
                            } else {
                                Log.e(TAG, "check error huanjing");
                                return;
                            }
                        }
                    }
                    break;
            }
        }
    };
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read);


        initModule();
        initUI();
    }

    /**
     * 初始化红外测温模块
     */
    private void initModule() {
        try {
            //打开串口
            serialPort = new SerialPort();
            serialPort.OpenSerial("/dev/ttyMT2", 9600);
            fd = serialPort.getFd();
            //实例化模块上电对象
            control = new DeviceControl("/sys/class/misc/mtgpio/pin");
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
        //        btnStart.setOnClickListener(this);
        btnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    control.PowerOnDevice("94");
                    control.PowerOnDevice("93");
                    thread = new ReadThread();
                    thread.start();    //手指按下时触发不停的发送消息
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    control.PowerOffDevice("94");
                    control.PowerOffDevice("93");
                    thread.interrupt();
                }
                return false;

            }
        });
        btnMIN.setOnClickListener(this);
        btnMAX.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public String saveDecimals(float f) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String dff = df.format(f);
        return dff;
    }

    @Override
    public void onClick(View v) {

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
            while (!interrupted())
                try {
                    byte[] temp1 = serialPort.ReadSerial(fd, 2048);
                    if (temp1 != null) {
                        Message msg = new Message();
                        msg.obj = temp1;
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
