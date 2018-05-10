package fragment;

/**
 * Created by 高浩然 on 2018/3/22.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.graduation_hr.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * 首页Fragment
 */
public class HomeFragment extends Fragment {
    //用于显示截取数据
    public static final int UPDATE_TEXE =1;
    private TextView textView,ip_text;
    private Button jiequ_button;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_TEXE:
                    Log.i("--------->","数据在这里进行显示");
                    Bundle b = msg.getData();
                    String s = b.getString("id")+"   "+b.getString("temp")+"   "+b.getString("hum")+"    "+b.getString("light");
                    textView.setText(s);
                    ip_text.setText("截取IP地址为"+b.getString("ip")+"的广播数据");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hone_fragment,container,false);
        Log.i("---------->","lalalla");
        try {
            initView(view);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View view) throws SocketException {
        textView = (TextView) view.findViewById(R.id.jiequ_text);
        ip_text = (TextView) view.findViewById(R.id.ip_text);
        jiequ_button = (Button) view.findViewById(R.id.jiequ_button);
        final DatagramSocket datagramSocket = new DatagramSocket(12345);//设置端口号
        jiequ_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Log.i("------------->","线程被开启");
                               // Thread.sleep(3000);// 线程暂停3秒，单位毫秒
                                Message message = new Message();
                                message.what=UPDATE_TEXE;
                                //以下是截取广播数据代码
                                DatagramPacket packet = new DatagramPacket(new byte[512], 512);
                                datagramSocket.receive(packet);
                                String msg = new String(packet.getData(), 0, packet.getLength());
                                //            msg = msg.substring(0,15);
                //            System.out.println("=====>" + msg + "<=======");
                                System.out.println("-------------");
                                String[] msg1=msg.split(" ");
                                int id = Integer.parseInt(msg1[0]);
                                String temp = msg1[1];
                                String hum = msg1[2];
                                String light = msg1[3];
                                String ip = msg1[4];
                                //String light = msg1[2].substring(1);
                                Log.i("收到的数据为",msg);
                                System.out.println("id   "+id+"   temp  "+temp+"   hum   "+hum+"   light  "+light+"  ip  "+ip);
                                Bundle b = new Bundle();
                                b.putString("id", String.valueOf(id));
                                b.putString("temp",temp);
                                b.putString("hum",hum);
                                b.putString("light",light);
                                b.putString("ip",ip);
                                message.setData(b);
                                handler.sendMessage(message);
                                Log.i("----------->","数据已经发送到界面UI中");
//                                packet.setData("I am server!!!".getBytes());
//                                datagramSocket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }//catch (InterruptedException e) {
                               // e.printStackTrace();
                          //  }

                        }
                        //message.arg1="";
                    }
                }).start();
            }
        });
    }
}


