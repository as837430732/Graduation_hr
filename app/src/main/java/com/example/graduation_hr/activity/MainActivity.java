package com.example.graduation_hr.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.graduation_hr.R;
import com.iflytek.cloud.ui.RecognizerDialog;

import java.util.ArrayList;

import fragment.HomeFragment;
import fragment.TypeCartFragment;

public class MainActivity extends FragmentActivity {

    FrameLayout frameLayout;
    RadioGroup rgMain;
    //装fragment的实例集合
    private ArrayList<Fragment> fragments;

    private int position = 0;

    //缓存Fragment或上次显示的Fragment
    private Fragment tempFragment;
    //有动画效果对话框
    public RecognizerDialog iatDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ButterKnife和当前Activity绑定
        // ButterKnife.bind(this);
      //  SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=5ab24496");
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        rgMain = (RadioGroup) findViewById(R.id.rg_main);
        //初始化Fragment
        initFragment();
        //设置RadioGroup的监听
        initListener();

      //  iatDialog = new RecognizerDialog(MainActivity.this , mInitListener);
//        iatDialog.setListener(new RecognizerDialogListener() {
//            String resultJson = "[";//放置在外边做类的变量则报错，会造成json格式不对（？）
//
//            //通过isLast判断是否是最后一个结果
//            @Override
//            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
//                System.out.println("-----------------   onResult   -----------------");
//                if (!isLast) {
//                    resultJson += recognizerResult.getResultString() + ",";
//                } else {
//                    resultJson += recognizerResult.getResultString() + "]";
//                }
//
//                if (isLast) {
//                    //解析语音识别后返回的json格式的结果
//                    Gson gson = new Gson();
//                    List<DictationResult> resultList = gson.fromJson(resultJson,
//                            new TypeToken<List<DictationResult>>() {
//                            }.getType());
//                    String result = "";
//                    for (int i = 0; i < resultList.size() - 1; i++) {
//                        result += resultList.get(i).toString();
//                    }
//                    Log.i("json-------->",resultJson);
//                    Log.i("test----------->",result);
////                            etText.setText(result);
////                            //获取焦点
////                            etText.requestFocus();
////                            //将光标定位到文字最后，以便修改
////                            etText.setSelection(result.length());
//                }
//
//            }
//
//            @Override
//            public void onError(SpeechError speechError) {
//                //自动生成的方法存根
//                speechError.getPlainDescription(true);
//            }
//        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showDialog();
//            }
//        },5000);
    }

    private void initListener() {
        rgMain.check(R.id.rb_home);
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_home: //首页
                        position = 0;
                        break;
                    case R.id.rb_type: //分类
                        position = 1;
                        break;
                    case R.id.rb_community: //发现
                        position = 2;
                        break;
                    default:
                        position = 1;
                        break;
                }
                //根据位置得到相应的Fragment
                Fragment baseFragment = getFragment(position);
                /**
                 * 第一个参数: 上次显示的Fragment
                 * 第二个参数: 当前正要显示的Fragment
                 */
                switchFragment(tempFragment,baseFragment);
            }
        });
    }

    /**
     * 添加的时候按照顺序
     */
    private void initFragment(){
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new TypeCartFragment());
      //  fragments.add(new CommunityFragment());

    }

    /**
     * 根据位置得到对应的 Fragment
     * @param position
     * @return
     */
    private Fragment getFragment(int position){
        if(fragments != null && fragments.size()>0){
            Fragment baseFragment = fragments.get(position);
            return baseFragment;
        }
        return null;
    }

    /**
     * 切换Fragment
     * @param fragment
     * @param nextFragment
     */
    private void switchFragment(Fragment fragment, Fragment nextFragment){
        if (tempFragment != nextFragment){
            tempFragment = nextFragment;
            if (nextFragment != null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment是否添加成功
                if (!nextFragment.isAdded()){
                    //隐藏当前的Fragment
                    if (fragment != null){
                        transaction.hide(fragment);
                    }
                    //添加Fragment
                    transaction.add(R.id.frameLayout,nextFragment).commit();
                }else {
                    //隐藏当前Fragment
                    if (fragment != null){
                        transaction.hide(fragment);
                    }
                    transaction.show(nextFragment).commit();
                }
            }
        }
    }
//
//    //初始化单例对象时，通过此回调接口，获取初始化状态。
//    public static final String TAG = "Fragment";
//    private InitListener mInitListener = new InitListener() {
//        @Override
//        public void onInit(int code) {
//            Log.d(TAG, "SpeechRecognizer init() code = " + code);
//            if (code != ErrorCode.SUCCESS) {
//                Toast.makeText(MainActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    public void showDialog(){
        iatDialog.show();
    }
}