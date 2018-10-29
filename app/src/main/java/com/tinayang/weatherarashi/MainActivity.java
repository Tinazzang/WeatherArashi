package com.tinayang.weatherarashi;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.tinayang.bean.TodayWeather;
import com.tinayang.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends Activity implements View.OnClickListener{
    private String updateCityCode;
    TodayWeather todayWeather = null;
    private static final int UPDATE_TODAY_WEATHER=1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, wenduTv, shiduTv, fengliTv, fengxiangTv, weekTv, pmDataTv, qualityTv, suggestTv, highTv, lowTv, typeTv, windTv, sunriseTv, sunsetTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    //主线程接收到消息数据后，调用updateTodayWeather函数，更新UI界面上的数据
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        //在UI线程中，为更新按钮（ImageView）增加单击事件
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        //调用检测网络连接状态方法
        if (NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
            Log.d("WeatherArashi","网络OK");
            Toast.makeText(MainActivity.this,"网络OK!",Toast.LENGTH_LONG).show();
        }
        else {
            Log.d("WeatherArashi","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
        }
        //为选择城市（ImageView）增加OnClick事件
        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();

        //如果citycode改变过，更新天气数据
        updateCityCode = getIntent().getStringExtra("citycode");
        if (updateCityCode!="-1"){
            queryWeatherCode(updateCityCode);
        }
    }

    //初始化界面控件
    void initView(){
        city_name_Tv=(TextView)findViewById(R.id.title_city_name);
        cityTv=(TextView)findViewById(R.id.city);
        timeTv=(TextView)findViewById(R.id.time);
        wenduTv=(TextView)findViewById(R.id.wendu);
        shiduTv=(TextView)findViewById(R.id.shidu);
        fengliTv=(TextView)findViewById(R.id.fengli);
        fengxiangTv=(TextView)findViewById(R.id.fengxiang);
        weekTv=(TextView)findViewById(R.id.week);
        pmDataTv=(TextView)findViewById(R.id.pm25_data);
        qualityTv=(TextView)findViewById(R.id.quality);
        suggestTv=(TextView)findViewById(R.id.suggest);
        pmImg=(ImageView) findViewById(R.id.pm25_img);
        highTv=(TextView)findViewById(R.id.high);
        lowTv=(TextView)findViewById(R.id.low);
        typeTv=(TextView)findViewById(R.id.type);
        windTv=(TextView)findViewById(R.id.wind);
        weatherImg=(ImageView)findViewById(R.id.weather_img);
        sunriseTv=(TextView)findViewById(R.id.sunrise);
        sunsetTv=(TextView)findViewById(R.id.sunset);

        weatherImg=(ImageView)findViewById(R.id.weather_img);
        pmImg=(ImageView)findViewById(R.id.pm25_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        wenduTv.setText("N/A");
        shiduTv.setText("N/A");
        fengliTv.setText("N/A");
        fengxiangTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        qualityTv.setText("N/A");
        suggestTv.setText("N/A");
        highTv.setText("N/A");
        lowTv.setText("N/A");
        typeTv.setText("N/A");
        windTv.setText("N/A");
        sunriseTv.setText("N/A");
        sunsetTv.setText("N/A");
    }

    //在UI线程中，为更新按钮增加单击事件
    @Override
    public void onClick(View view){
        //为选择城市（ImageView）增加OnClick事件
        if (view.getId()==R.id.title_city_manager){
            Intent i=new Intent(this,SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);
        }

        if (view.getId()==R.id.title_update_btn){
            //读取城市ID
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("WeatherArashi",cityCode);
            //获取网络数据
            if (NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("WeatherArashi","网络OK");
                queryWeatherCode(cityCode);
            }
            else {
                Log.d("WeatherArashi","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }
    //接收单机更新按钮后返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode==1&&resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("cityCode","选择的城市代码为"+newCityCode);

            if (NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("WeatherArashi","网络OK");
                queryWeatherCode(newCityCode);
            }
            else {
                Log.d("WeatherArashi","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }

    //获取网络数据
    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;

        Log.d("WeatherArashi",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                //调用parseXML，并返回TodayWeather对象
                TodayWeather todayWeather=null;
                try {
                    URL url=new URL(address);
                    con=(HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in=con.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String str;
                    while ((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("WeatherArashi",str);
                    }
                    String responseStr=response.toString();
                    Log.d("WeatherArashi",responseStr);
                    //获取网络数据后，调用解析函数，并返回TodayWeather对象
                    todayWeather=parseXML(responseStr);
                    if (todayWeather!=null){
                        Log.d("WeatherArashi",todayWeather.toString());

                        //将解析的天气对象，通过消息发送给主线程，
                        Message msg =new Message();
                        msg.what=UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if (con!=null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    //编写解析函数，解析所需信息，将解析的数据存入TodayWeather对象中
    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather=null;
        int fengxiangCount=0;
        int fengliCount=0;
        int dateCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;
        try {
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType=xmlPullParser.getEventType();
            Log.d("WeatherArashi","parseXML");
            while (eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")){
                            todayWeather=new TodayWeather();
                        }
                        if (todayWeather!=null){
                            if (xmlPullParser.getName().equals("city")){
                                eventType=xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("updatetime")){
                                eventType=xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("shidu")){
                                eventType=xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("wendu")){
                                eventType=xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("pm25")){
                                eventType=xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("quality")){
                                eventType=xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("suggest")){
                                eventType=xmlPullParser.next();
                                todayWeather.setSuggest(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("fengxiang")&&fengxiangCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli")&&fengliCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if (xmlPullParser.getName().equals("date")&&dateCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }
                            else if (xmlPullParser.getName().equals("high")&&highCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            }
                            else if (xmlPullParser.getName().equals("low")&&lowCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            }
                            else if (xmlPullParser.getName().equals("type")&&typeCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                            else if (xmlPullParser.getName().equals("sunrise_1")){
                                eventType=xmlPullParser.next();
                                todayWeather.setSunrise_1(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("sunset_1")){
                                eventType=xmlPullParser.next();
                                todayWeather.setSunset_1(xmlPullParser.getText());
                            }
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType=xmlPullParser.next();
            }

        }
        catch(XmlPullParserException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }

    //利用TodayWeather对象更新UI中的控件
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        wenduTv.setText(todayWeather.getWendu()+"℃");
        shiduTv.setText("湿度："+todayWeather.getShidu());
        fengliTv.setText("风力："+todayWeather.getFengli());
        fengxiangTv.setText(todayWeather.getFengxiang());
        weekTv.setText("今天  "+todayWeather.getDate());
        pmDataTv.setText(todayWeather.getPm25());
        qualityTv.setText(todayWeather.getQuality());
        suggestTv.setText(todayWeather.getSuggest());
        highTv.setText(todayWeather.getHigh());
        lowTv.setText(todayWeather.getLow());
        typeTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengxiang()+todayWeather.getFengli());
        sunriseTv.setText("日出："+todayWeather.getSunrise_1());
        sunsetTv.setText("日落："+todayWeather.getSunset_1());

        if (todayWeather.getPm25()!=null){
            int pm25=Integer.parseInt(todayWeather.getPm25());
            if (pm25<=50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }
            else if (pm25>=51&&pm25<=100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }
            else if (pm25>=101&&pm25<=150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }
            else if (pm25>=151&&pm25<=200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }
            else if (pm25>=201&&pm25<=300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
            else if (pm25>=300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }

        if (todayWeather.getType()!=null){
            Log.d("type",todayWeather.getType());
            switch (todayWeather.getType()){
                case "晴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "多云":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "阴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "小雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "中雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                case "大雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "特大暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "阵雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "雷阵雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "小雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "中雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "大雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "暴雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "阵雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "雨夹雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "雾":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "沙尘暴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                default:
                    break;
            }
        }
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }
}
