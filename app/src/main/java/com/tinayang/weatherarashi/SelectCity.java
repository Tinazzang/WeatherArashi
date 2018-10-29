package com.tinayang.weatherarashi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.tinayang.app.MyApplication;
import com.tinayang.bean.City;

import java.util.ArrayList;
import java.util.List;

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView cityListLv;

    private List<City> mCityList;
    private MyApplication mApplication;
    private ArrayList<String> mArrayList;

    private String updateCityCode="-1";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        //为选择城市界面的返回（ImageView）设置OnClick事件
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        //加载数据库文件中的城市列表
        mApplication=(MyApplication)getApplication();
        mCityList=mApplication.getCityList();
        mArrayList=new ArrayList<String>();
        for (int i=0;i<mCityList.size();i++){
            String cityName = mCityList.get(i).getCity();
            mArrayList.add(cityName);
        }
        cityListLv=(ListView)findViewById(R.id.selectcity_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,mArrayList);
        cityListLv.setAdapter(adapter);

        //添加ListView项的点击事件的动作，将主界面数据更新为点击的城市的城市代码
        AdapterView.OnItemClickListener itemClickListener= new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateCityCode = mCityList.get(position).getNumber();
                Log.d("updateCityCode",updateCityCode);
            }
        };
        //为组件绑定监听
        cityListLv.setOnItemClickListener(itemClickListener);
    }

    //为选择城市界面的返回（ImageView）设置OnClick事件
    @Override
    public void onClick(View v){
        switch (v.getId()){
            //点击返回时，将citycode传递给MainActivity类
            case R.id.title_back:
                //finish();
                //传递数据
                Intent intent=new Intent(this,MainActivity.class);
                intent.putExtra("cityCode",updateCityCode);
                Log.d("cityCode",updateCityCode);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }
}
