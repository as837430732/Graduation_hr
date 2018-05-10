package com.example.graduation_hr;

import java.util.ArrayList;

/**
 * Created by 高浩然 on 2018/3/28.
 */
public class Data {
    private String flag;
    private ArrayList<Data2> data;

    public static class Data2{
        int id;
        String hum;
        String temp;
        String light;
        String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getHum() {
            return hum;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getLight() {
            return light;
        }

        public void setLight(String light) {
            this.light = light;
        }
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ArrayList<Data2> getData() {
        return data;
    }

    public void setData(ArrayList<Data2> data) {
        this.data = data;
    }
}
