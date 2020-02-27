package speedatacom.tn9hongwaitemp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.speedata.temperture.ITempertureInterface;
import com.speedata.temperture.Temperture;
import com.speedata.temperture.TempertureCallBack;
import com.speedata.temperture.TempertureManager;

import util.PlaySoundPool;

public class RedActivity extends Activity implements View.OnClickListener {
    private final String TAG = "RedActivity";

    private TextView tvmubian, btnStart;


    private PlaySoundPool playSoundPool;
    private ITempertureInterface tempertureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read);
        playSoundPool = PlaySoundPool.getPlaySoundPool(this);
        initUI();
        tempertureManager = TempertureManager.getIntance();
        tempertureManager.openDev();
        tempertureManager.setCallBack(new TempertureCallBack() {
            @Override
            public void receTempData(Temperture temperture) {
                String msg = "";
                if (temperture.getBodyTemp() < 29) {
                    playSoundPool.playError();
                    msg = "体温低"+temperture.getBodyTemp();
                } else if (temperture.getBodyTemp() > 39) {
                    playSoundPool.playError();
                    msg = "高温警告";
                } else {
                    playSoundPool.playLaser();
                    msg = temperture.getBodyTemp() + "℃";
                }
                final String finalMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvmubian.setText(finalMsg);
                        btnStart.setEnabled(true);
                    }
                });
            }
        });
    }

    private void initUI() {
        tvmubian = (TextView) findViewById(R.id.tv_mubiao);
        btnStart = (TextView) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        btnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v("LogTemp", "Start Thread");
                    btnStart.setEnabled(false);
                    tempertureManager.startMeasurement(true);
                }
                return false;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btnStart) {
            tempertureManager.startMeasurement(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        tempertureManager.stopMeasurement();
        tempertureManager.closeDev();
        super.onDestroy();
    }


}
