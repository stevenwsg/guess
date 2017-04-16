package com.example.asus.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.asus.bmobbean.User;
import com.example.asus.common.BaseApplication;
import com.example.asus.common.MySwipeBackActivity;
import com.example.asus.common.MyToast;
import com.example.asus.view.PickerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class EditPersonalDataActivity extends MySwipeBackActivity {
    private TextView mName;
    private TextView mEmail;
    private TextView mSex;
    private TextView mAge;
    private TextView mCity;
    private TextView mUsername;


    private BaseApplication mApplication;
    private User mCurrentUser;

    private List<String> ageItemList;
    private List<String> sexItemList;
    private String editAge = "20";
    private String editSex = "男";  //此处数据无用，只是用于更新前省略判断是否为""情况

    public LocationClient mLocationClient = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_data);
        mApplication = (BaseApplication) getApplication();
        mCurrentUser = mApplication.getUser();
        initData();
        initView();
        initLocation();
    }

    private void initData() {
        ageItemList = new ArrayList<String>();
        sexItemList = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            ageItemList.add("" + i);
        }
        sexItemList.add(0, "男");
        sexItemList.add(1, "女");
    }

    private void initView() {
        mUsername = (TextView) findViewById(R.id.username);
        mName = (TextView) findViewById(R.id.name);
        mEmail = (TextView) findViewById(R.id.email);
        mSex = (TextView) findViewById(R.id.sex);
        mAge = (TextView) findViewById(R.id.age);
        mCity = (TextView) findViewById(R.id.city);
        mName.setText(mCurrentUser.getName());
        mCity.setText(mCurrentUser.getCity());
        mAge.setText(mCurrentUser.getAge() == null ? "" : mCurrentUser.getAge() + "");
        mEmail.setText(mCurrentUser.getEmail());
        mSex.setText(mCurrentUser.getSex());
        mUsername.setText(mCurrentUser.getUsername());
    }

    public void editName(View view) {

    }

    public void editEmail(View view) {

    }


    public void editSex(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_sex, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        PickerView wheelView = (PickerView) dialogView.findViewById(R.id.pickerView);
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        wheelView.setData(sexItemList);
        if (TextUtils.isEmpty(mSex.getText())) {
            mSex.setText("男");
            wheelView.setSelected("男");
        } else {
            wheelView.setSelected(mSex.getText().toString());
        }
        wheelView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                editSex = text;
            }
        });
        chooseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!TextUtils.equals(editSex, mSex.getText())) {
                    showProgressbarWithText("正在更新");
                    mCurrentUser.setSex(editSex);
                    mCurrentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            hideProgressbar();
                            if (checkCommonException(e, EditPersonalDataActivity.this)) {
                                return;
                            }
                            mSex.setText(editSex);
                        }
                    });
                }
            }
        });
    }

    public void editAge(View view) {
        View dialogView = View.inflate(this, R.layout.dialog_choose_age, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Translucent_NoTitle);
        dialog.setView(dialogView, 10, 0, 10, 0);
        PickerView wheelView = (PickerView) dialogView.findViewById(R.id.pickerView);
        final Dialog chooseDialog = dialog.show();
        WindowManager.LayoutParams lp = chooseDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//宽高可设置具体大小
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        chooseDialog.getWindow().setAttributes(lp);
        wheelView.setData(ageItemList);
        if (TextUtils.isEmpty(mAge.getText())) {
            mAge.setText("20");
            wheelView.setSelected(20 + "");
        } else {
            wheelView.setSelected(mAge.getText().toString());
        }
        wheelView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                editAge = text;
            }
        });
        chooseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!TextUtils.equals(editAge, mAge.getText())) {
                    showProgressbarWithText("正在更新");
                    mCurrentUser.setAge(Integer.valueOf(editAge));
                    mCurrentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            hideProgressbar();
                            if (checkCommonException(e, EditPersonalDataActivity.this)) {
                                return;
                            }
                            mAge.setText(editAge);
                        }
                    });
                }
            }
        });
    }

    public void editCity(View view) {
        showProgressbarWithText("正在定位");
        mLocationClient.start();
    }

    public void editPassword(View view) {
        Intent intent = new Intent(this, FindPassActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                hideProgressbar();
                if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                    logd("网络定位");
                    mCity.setText(bdLocation.getCity());
                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
                    logd("离线定位");
                    mCity.setText(bdLocation.getCity());
                } else if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                    logd("GPS定位");
                    mCity.setText(bdLocation.getCity());
                } else {
                    MyToast.getInstance().showShortWarn(EditPersonalDataActivity.this, "定位失败,点击重新定位");
                    loge("定位失败:  ERROR CODE: " + bdLocation.getLocType());
                }
                mLocationClient.stop();
            }
        });
    }


}
