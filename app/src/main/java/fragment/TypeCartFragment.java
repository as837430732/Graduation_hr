package fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation_hr.Data;
import com.example.graduation_hr.DictationResult;
import com.example.graduation_hr.TestData;
import com.example.graduation_hr.activity.MainActivity;
import com.example.graduation_hr.R;
import com.example.graduation_hr.adapter.ChatAdapter;
import com.example.graduation_hr.model.ChatModel;
import com.example.graduation_hr.model.ItemModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 高浩然 on 2018/3/22.
 */
public class TypeCartFragment extends Fragment {


    /**
     * 语音识别
     */
    private EditText etText;
    private Button btnStartSpeak;

    /**
     * 语音合成
     */
    private EditText etHeCheng;
    private Button btnStartHeCheng;

    //有动画效果
    private RecognizerDialog iatDialog;
    //无动画效果
    private SpeechRecognizer mIat;
    // TextView context_text;
    ListView context_list;

    String result = "";//语音识别的结果
    String[] list;//存放分解的字符串数组

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private TextView tvSend;
    private String content;

    // String url="http://172.20.10.4:8080/ai";//这里填写的是电脑端的IP
    // String url="http://192.168.1.104:8080/ai";//这里填写的是电脑端的IP
    String url = "http://192.168.1.109:8080/ai";//这里填写的是电脑端的IP
    private View view1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("TypeCartFragment", "执行了OnCreate方法");
        //引用创建好的xml布局
        View view = inflater.inflate(R.layout.typecart_fragment, container, false);
//        textView= (TextView) view.findViewById(R.id.textview);
//        textView.setText("haha");
        // 语音配置对象初始化(如果只使用 语音识别 或 语音合成 时都得先初始化这个)
        SpeechUtility.createUtility(view.getContext(), SpeechConstant.APPID + "=5ab24496");
        initView(view);
        view1 = view;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter = new ChatAdapter());

        //adapter.replaceAll(TestData.getTestAdData());

        return view;

    }

    //由于语音识别需要开启线程，UI更新需要在这里进行
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Log.i("--------->", "数据在这里进行显示");
            Bundle b = msg.getData();
            int num = Integer.parseInt(b.getString("num"));
            String s = "";
            if (num != 0) {
                for (int i = 0; i < num; i++) {
                    s += b.getString("s" + i);
                }
            } else {
                s = "暂时没有查询到数据！";
            }

            list = s.split(",");

            //点击完语音按钮后，将数据放入到适配器中，这里是聊天界面中红色方内容
            ArrayList<ItemModel> data = new ArrayList<>();
            //chat_b  红色聊天背景的
            //右边框
            ChatModel modle_r = new ChatModel();
            modle_r.setIcon("https://pic.qqtn.com/up/2018-5/2018050811122993894.jpg");
            modle_r.setContent(content);
            data.add(new ItemModel(ItemModel.CHAT_B, modle_r));
            //左边框
            ChatModel model = new ChatModel();
            model.setIcon("https://pic.qqtn.com/up/2018-5/2018050612061453206.jpg");
            model.setContent(s);
            //Log.i("TypeCartFragment","内容为："+content);
            data.add(new ItemModel(ItemModel.CHAT_A, model));
            adapter.addAll(data);
            // Log.i("TypeCartFragment","data  "+data.toString());
            etText.setText("");
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            //  hideKeyBorad(etText);//隐藏输入面板

            // ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,list);
            // context_list.setAdapter(adapter);
            Log.i("---------->", s);

        }
    };

    private void initView(View view) {
        //  context_text = (TextView) view.findViewById(R.id.context_text);
        etText = (EditText) view.findViewById(R.id.main_et_text);
        btnStartSpeak = (Button) view.findViewById(R.id.main_btn_startSpeak);
        //  context_list = (ListView) view.findViewById(R.id.context_list);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerView);


        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("TypeCartFragment", "内容改变之前");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("TypeCartFragment", "内容改变中");

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("TypeCartFragment", "内容改变后");
                content = s.toString().trim();


            }
        });

        //开始语音识别
        btnStartSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) getActivity();

                // 有交互动画的语音识别器
                iatDialog = new RecognizerDialog(view1.getContext(), mInitListener);
//                //1.创建SpeechRecognizer对象(没有交互动画的语音识别器)，第2个参数：本地听写时传InitListener
//                mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);
//                // 2.设置听写参数
//                mIat.setParameter(SpeechConstant.DOMAIN, "iat"); // domain:域名
//                mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//                mIat.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话
//                //保存音频文件到本地（有需要的话）   仅支持pcm和wav
//                 mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/mIat.wav");
//                mIat.startListening(mRecognizerListener);//

                // 通过实现此接口，获取识别对话框识别过程的结果和错误信息。
                iatDialog.setListener(new RecognizerDialogListener() {
                    String resultJson = "[";//放置在外边做类的变量则报错，会造成json格式不对（？）

                    //通过isLast判断是否是最后一个结果
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                        System.out.println("-----------------   onResult   -----------------");
                        if (!isLast) {
                            resultJson += recognizerResult.getResultString() + ",";
                        } else {
                            resultJson += recognizerResult.getResultString() + "]";
                        }

                        if (isLast) {
                            //解析语音识别后返回的json格式的结果
                            Gson gson = new Gson();
                            List<DictationResult> resultList = gson.fromJson(resultJson,
                                    new TypeToken<List<DictationResult>>() {
                                    }.getType());
                            result = "";
                            for (int i = 0; i < resultList.size() - 1; i++) {
                                result += resultList.get(i).toString();
                            }
                            Log.i("json-------->", resultJson);
                            Log.i("test----------->", result);
                            etText.setText(result);
                            //获取焦点
                            etText.requestFocus();
                            //将光标定位到文字最后，以便修改
                            etText.setSelection(result.length());
                            connectInternet();
                             /*连接网络，result会被赋值*/
                            Log.e("asdaa", result);
                             /*在控制台输出result*/
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {
                        //自动生成的方法存根
                        speechError.getPlainDescription(true);
                    }
                });
                // 开始听写，需将sdk中的assets文件下的文件夹拷入项目的assets文件夹下（没有的话自己新建）
                iatDialog.show();
                //main.showDialog();
            }
        });

//        //语音合成
//        etHeCheng = (EditText) view.findViewById(R.id.main_et_needToHeCheng);
//        btnStartHeCheng = (Button) view.findViewById(R.id.main_btn_startHeCheng);
//        btnStartHeCheng.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startHeCheng(etHeCheng.getText().toString());
//            }
//        });
    }

    public void connectInternet() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                HttpClient hc = new DefaultHttpClient();
                HttpPost hp = new HttpPost(url);
                hp.addHeader("Content-Type", "text/html");    //这行很重要
                hp.addHeader("charset", HTTP.UTF_8);         //这行很重要
                // JSONObject jo = new JSONObject();
                /*JSONObject在连接网络传递数据是最常用的包装数据方式，当然也可以直接发送String类型的*/
                try {
                    String send = URLEncoder.encode(URLEncoder.encode(result, "UTF-8"), "UTF-8");
                    /*给jo赋值，jo中是以键值对的形式存在*/
                    StringEntity se = new StringEntity(send);
                    /*包装可以通过网络发送的数据*/
                    hp.setEntity(se);
                    /*将包装好的数据放在hp中*/
                    HttpResponse hr = hc.execute(hp);
                    /*执行连接语句并获得返回数据*/
                    if (hr.getStatusLine().getStatusCode() == 200)
                    /*200是固定的，如果等于200，就说明服务器连接成功并有返回值*/ {
                        Log.i("--------->", "连接成功");
                        result = EntityUtils.toString(hr.getEntity());
                        Log.i("-------->", result);

                        Message message = new Message();
                        Bundle b = new Bundle();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<Data>() {
                        }.getType();
                        Data data = gson.fromJson(result, Data.class);
                        data.getFlag();
                        String s = "";
                        for (int i = 0; i < data.getData().size(); i++) {
                            Log.i("------->", String.valueOf(data.getData().get(i).getId()));
                            b.putString("s" + i, "" + data.getData().get(i).getId() + " " +
                                    data.getData().get(i).getHum() + "% " +
                                    data.getData().get(i).getTemp() + "°C " +
                                    data.getData().get(i).getLight() + "lx 日期：" +
                                    data.getData().get(i).getTime().substring(0, 10) + " ");
                        }
                        b.putString("num", data.getData().size() + "");
                        message.setData(b);
                        handler.sendMessage(message);
                        /*解析获得的数据并赋值给result*/
                    } else if (hr.getStatusLine().getStatusCode() == 404) {
                        Log.i("TypeCartFragment", "网络连接失败");
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }


    /**
     * 用于SpeechRecognizer（无交互动画）对象的监听回调
     */
//    private RecognizerListener mRecognizerListener = new RecognizerListener() {
//        @Override
//        public void onVolumeChanged(int i, byte[] bytes) {
//
//        }
//
//        @Override
//        public void onBeginOfSpeech() {
//            System.out.println("开始");
//        }
//
//        @Override
//        public void onEndOfSpeech() {
//            System.out.println("结束");
//        }
//
//        @Override
//        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
//           // Log.i(TAG, recognizerResult.toString());
//            String resultJson = "[";//放置在外边做类的变量则报错，会造成json格式不对（？）
//          //  Log.i(TAG, recognizerResult.toString());
//            System.out.println("-----------------   onResult   -----------------");
//            if (!isLast) {
//                resultJson += recognizerResult.getResultString() + ",";
//                System.out.println("sdsdsd"+resultJson);
//            } else {
//                resultJson += recognizerResult.getResultString() + "]";
//                System.out.println("sd"+resultJson);
//            }
//
//            if (isLast) {
//                //解析语音识别后返回的json格式的结果
//                Gson gson = new Gson();
//                List<DictationResult> resultList = gson.fromJson(resultJson,
//                        new TypeToken<List<DictationResult>>() {
//                        }.getType());
//               // String result = "";
//                for (int i = 0; i < resultList.size() - 1; i++) {
//                    result += resultList.get(i).toString();
//                }
//                System.out.println("reslut   "+result);
//                Log.i("json-------->",resultJson);
//                Log.i("test----------->",result);
//                etText.setText(result);
//
//                //获取焦点
//                etText.requestFocus();
//                //将光标定位到文字最后，以便修改
//                etText.setSelection(result.length());
//            }
//        }
//
//        @Override
//        public void onError(SpeechError speechError) {
//            System.out.println("=发生异常错误");
//        }
//
//        @Override
//        public void onEvent(int i, int i1, int i2, Bundle bundle) {
//
//        }
//    };
    //初始化单例对象时，通过此回调接口，获取初始化状态。
    public static final String TAG = "Fragment";
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(getActivity(), "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

//    /**
//     * 开始科大讯飞的合成语音
//     */
//    private void startHeCheng(String recordResult) {
//        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
//        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, null);
//
//        /**
//         2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
//         *
//         */
//
//        // 清空参数
//        mTts.setParameter(SpeechConstant.PARAMS, null);
//
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
//        //设置合成音调
//        mTts.setParameter(SpeechConstant.PITCH, "50");
//        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
//        // 设置播放合成音频打断音乐播放，默认为true
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
////        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
////        boolean isSuccess = mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts2.wav");
////        Toast.makeText(MainActivity.this, "语音合成 保存音频到本地：\n" + isSuccess, Toast.LENGTH_LONG).show();
//        //3.开始合成
//        int code = mTts.startSpeaking(recordResult, mSynListener);
//
//        if (code != ErrorCode.SUCCESS) {
//            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
//                //上面的语音配置对象为初始化时：
//                Toast.makeText(MainActivity.this, "语音组件未安装", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(MainActivity.this, "语音合成失败,错误码: " + code, Toast.LENGTH_LONG).show();
//            }
//        }
//
//    }

    private void hideKeyBorad(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

}