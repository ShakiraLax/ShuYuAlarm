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
import com.sypm.shuyualarm.data.Order;
import com.sypm.shuyualarm.data.StoreName;
import com.sypm.shuyualarm.utils.BaseActivity;
import com.sypm.shuyualarm.utils.RememberHelper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private int recLen = 0;

    private Button change;

    private Order orderBean;

    private StoreName storeNameBean;

    private TextView storeName, alarmText, alarmContent;

    private LinearLayout linearLayout;

    private ImageView imageView;

    private MediaPlayer mp = new MediaPlayer();

    private String inputStoreSn;

    private String getStoreName;


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen++;
            if (!RememberHelper.getStoreSn().equals("")) {
                getOrderByStoreSn();
            }
            handler.postDelayed(this, 60000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initView();
        runnable.run();
    }

    private void initView() {
        change = (Button) findViewById(R.id.changeName);
        storeName = (TextView) findViewById(R.id.storeName);
        alarmText = (TextView) findViewById(R.id.alarmText);
        alarmContent = (TextView) findViewById(R.id.alarmContent);
        linearLayout = (LinearLayout) findViewById(R.id.stop);
        imageView = (ImageView) findViewById(R.id.alarmImage);

        if (RememberHelper.getStoreName() != null) {
            storeName.setText(RememberHelper.getStoreName());
        }

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
                        if (!storeSn.getText().toString().equals("")) {
                            inputStoreSn = storeSn.getText().toString();
                            Call<StoreName> call = RetrofitClient.getInstance().getSYService().getStoreNameByStoreSn(inputStoreSn);
                            call.enqueue(new Callback<StoreName>() {
                                @Override
                                public void onResponse(Call<StoreName> call, Response<StoreName> response) {
                                    if (response.body() != null) {
                                        if (response.body().status.equals("1")) {
                                            storeNameBean = response.body();
                                            if (response.body().storeName != null) {
                                                getStoreName = storeNameBean.storeName;
                                                RememberHelper.saveStoreName(getStoreName);
                                                RememberHelper.saveStoreSn(inputStoreSn);
                                                storeName2.setText(getStoreName);
                                            } else {
                                                Toast.makeText(getActivity(), "输入门店编号有误", Toast.LENGTH_LONG).show();
                                            }
                                        } else {

                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "根据门店编号获取门店名称失败", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<StoreName> call, Throwable t) {

                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "门店编码不能为空", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                /**
                 * 弹窗消失事件
                 * */
                Dialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setView(renameView)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                storeName.setText(RememberHelper.getStoreName());
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
//                if (mp.isPlaying()) {
//                    mp.reset();
//                }
            }
        });
    }

    /**
     * 获取订单
     */
    private void getOrderByStoreSn() {
        Call<Order> call = RetrofitClient.getInstance().getSYService().getOrderByStoreSn(RememberHelper.getStoreSn());
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.body() != null) {
                    if (response.body().status.equals("1")) {
                        orderBean = response.body();
                        if (orderBean.number.equals("0")) {
                            Resources resources = getActivity().getResources();
                            Drawable btnDrawable2 = resources.getDrawable(R.drawable.but_bg);
                            linearLayout.setBackgroundDrawable(btnDrawable2);
                            Resources resources3 = getActivity().getResources();
                            Drawable btnDrawable3 = resources3.getDrawable(R.drawable.connect);
                            imageView.setBackgroundDrawable(btnDrawable3);
                            alarmText.setText("联网成功");
                            alarmContent.setVisibility(View.INVISIBLE);

                        } else {
                            Resources resources = getActivity().getResources();
                            Drawable btnDrawable = resources.getDrawable(R.drawable.but_bg2);
                            linearLayout.setBackgroundDrawable(btnDrawable);
                            Resources resources2 = getActivity().getResources();
                            Drawable btnDrawable2 = resources2.getDrawable(R.drawable.alarm2);
                            imageView.setBackgroundDrawable(btnDrawable2);
                            alarmText.setText("来订单了");
                            alarmContent.setVisibility(View.VISIBLE);

                            try {
                                mp.setDataSource(getActivity(), Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.store));
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

                    }
                } else {
                    Toast.makeText(getActivity(), "根据门店编号获取订单失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

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
