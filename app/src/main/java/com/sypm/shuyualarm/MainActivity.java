package com.sypm.shuyualarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sypm.shuyualarm.api.RetrofitClient;
import com.sypm.shuyualarm.data.DataResult;
import com.sypm.shuyualarm.data.OrderBySn;
import com.sypm.shuyualarm.utils.BaseActivity;
import com.sypm.shuyualarm.utils.MD5Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private int recLen = 0;

    private Button change;

    private OrderBySn order;

    private TextView storeName, alarmText, alarmContent;

    private LinearLayout linearLayout;

    private ImageView imageView;

    //门店配送
    private MediaPlayer mp = new MediaPlayer();
    //配送员配送
    private MediaPlayer mp2 = new MediaPlayer();

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen++;
            getOrder();
            handler.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        login();
        initView();
//        getStoreNameByStoreSn();
//        getOrderByStoreSn();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runnable.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void initView() {
        change = (Button) findViewById(R.id.changeName);
        storeName = (TextView) findViewById(R.id.storeName);
        alarmText = (TextView) findViewById(R.id.alarmText);
        alarmContent = (TextView) findViewById(R.id.alarmContent);
        linearLayout = (LinearLayout) findViewById(R.id.stop);
        imageView = (ImageView) findViewById(R.id.alarmImage);

        /**
         * 修改门店名称
         * */
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View renameView = layoutInflater.inflate(R.layout.view_dialog, null);
                Button button = (Button) renameView.findViewById(R.id.button);
                final EditText storeSn = (EditText) renameView.findViewById(R.id.storeSn);
                final TextView storeName2 = (TextView) renameView.findViewById(R.id.storeName);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = storeSn.getText().toString();
                        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
                        storeName2.setText(name);
                    }
                });
                /*final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setView(renameView);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                }).create();
                dialog.show();*/
                Dialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setView(renameView)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Toast.makeText(getActivity(), "取消事件已触发", Toast.LENGTH_SHORT).show();
                                if (storeSn.getText().toString() != null) {
                                    storeName.setText(storeSn.getText().toString());
                                }
                            }
                        })
                        .create();
                alertDialog.show();

            }
        });

        /**
         * 取消铃声
         * */
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()) {
                    mp.reset();
                }
            }
        });
    }

    private void login() {
        Call<DataResult> call = RetrofitClient.getInstance().getSYService().login("113802", MD5Utils.md5Encode("123123"), null);
        call.enqueue(new Callback<DataResult>() {
            @Override
            public void onResponse(Call<DataResult> call, Response<DataResult> response) {
                if (response.body() != null) {
                    if (response.body().status.equals("1")) {
                        Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResult> call, Throwable t) {
//                Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrder() {
        Call<OrderBySn> getOrder = RetrofitClient.getInstance().getSYService().getOrder();
        getOrder.enqueue(new Callback<OrderBySn>() {
            @Override
            public void onResponse(Call<OrderBySn> call, Response<OrderBySn> response) {
                if (response.body() != null) {
                    if (response.body().status == 1) {
                        Toast.makeText(getActivity(), "真的有订单了", Toast.LENGTH_LONG).show();
                    } else if (response.body().status == 0) {
                        if (recLen % 2 == 1) {
                            Resources resources = getActivity().getResources();
                            Drawable btnDrawable = resources.getDrawable(R.drawable.but_bg2);
                            linearLayout.setBackgroundDrawable(btnDrawable);
                            Resources resources2 = getActivity().getResources();
                            Drawable btnDrawable2 = resources2.getDrawable(R.drawable.alarm2);
                            imageView.setBackgroundDrawable(btnDrawable2);
                            alarmText.setText("来订单了");
                            alarmContent.setVisibility(View.VISIBLE);
                        } else {
                            Resources resources = getActivity().getResources();
                            Drawable btnDrawable2 = resources.getDrawable(R.drawable.but_bg);
                            linearLayout.setBackgroundDrawable(btnDrawable2);
                            Resources resources3 = getActivity().getResources();
                            Drawable btnDrawable3 = resources3.getDrawable(R.drawable.connect);
                            imageView.setBackgroundDrawable(btnDrawable3);
                            alarmText.setText("联网成功");
                            alarmContent.setVisibility(View.INVISIBLE);
                        }
                        Toast.makeText(getActivity(), "其实没订单", Toast.LENGTH_LONG).show();
                        try {
                            mp.setDataSource(getActivity(), Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.notify2));
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                if (mp != null) {
                                    Toast.makeText(getActivity(), "播放完毕", Toast.LENGTH_LONG).show();
                                    mp.stop();
                                    mp.reset();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "未获取到数据", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OrderBySn> call, Throwable t) {
                Toast.makeText(getActivity(), "服务器获取失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getOrderByStoreSn() {
        Call<DataResult> call = RetrofitClient.getInstance().getSYService().getOrderByStoreSn("001");
        call.enqueue(new Callback<DataResult>() {
            @Override
            public void onResponse(Call<DataResult> call, Response<DataResult> response) {

            }

            @Override
            public void onFailure(Call<DataResult> call, Throwable t) {

            }
        });
    }

    private void getStoreNameByStoreSn() {
        Call<DataResult> call = RetrofitClient.getInstance().getSYService().getStoreNameByStoreSn("001");
        call.enqueue(new Callback<DataResult>() {
            @Override
            public void onResponse(Call<DataResult> call, Response<DataResult> response) {

            }

            @Override
            public void onFailure(Call<DataResult> call, Throwable t) {

            }
        });
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }*/
}
