package com.hyunseok.android.servicebasic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActiviry";

    Button btn_start, btn_stop, btn_bindStart, btn_bindStop, btn_callService;

    MyService bService;
    boolean isService = false; // 서비스 중 확인

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { // MyService의 onBind에서 리턴되는 값이 service에 담겨온다.
            Log.d(TAG, "OnServiceConnected===============" + name);

            MyService.MyBinder mb = (MyService.MyBinder) service;
            bService = mb.getService();
            isService = true;
        }

        // 서비스가 중단되거나 연결이 도중에 끊겼을 때 호출됨. onDestory에선 호출되지않음.
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected==================" + name);
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_bindStart = (Button)findViewById(R.id.btn_bindStart);
        btn_bindStop = (Button)findViewById(R.id.btn_bindStop);
        btn_callService = (Button)findViewById(R.id.btn_callService);

        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_bindStart.setOnClickListener(this);
        btn_bindStop.setOnClickListener(this);
        btn_callService.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this, MyService.class);

        switch (v.getId()) {
            case R.id.btn_start :
                // 서비스 시작
                startService(intent); // 서비스 띄울때는 startService()를 사용.
                break;
            case R.id.btn_stop :
                // 서비스 종료
                stopService(intent);
                super.onDestroy();
                 break;
            case R.id.btn_bindStart:
                bindService(intent, conn, Context.BIND_AUTO_CREATE); // 마지막인자는 bind할 때 service가 없으면 생성해주는 옵션
                break;
            case R.id.btn_bindStop:
                if(isService) {
                    unbindService(conn); // 서비스 종료
                    // unbind 시 onServiceDisconnected 호출안됨.. 서비스가 끊겼을 경우만 호출되므로
                    // 마지막 unbind 시 서비스가 실행되지 않고 있음을 알려야 함
                    isService = false;
                    // 아래처럼 강제적으로 onServiceDisconnected를 호출해 줄 수도 있긴 하다
                    //conn.onServiceDisconnected(new ComponentName("com.veryworks.android.servicetest","BindService.class"));
                }else
                    Toast.makeText(getApplicationContext(), "서비스중이 아닙니다, 종료할 수 없음", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_callService:
                if (!isService) {
                    Toast.makeText(getApplicationContext(), "서비스중이 아닙니다, 데이터받을수 없음", Toast.LENGTH_LONG).show();
                    return;
                }
                int num = bService.getRandomNumber();//서비스쪽 메소드에서 값 전달 받아 호출
                Toast.makeText(getApplicationContext(), "받아온 데이터 : " + num, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
