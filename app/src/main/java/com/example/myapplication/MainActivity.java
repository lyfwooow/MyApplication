package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private  Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        init();
    }
    //初始化fragment和fragment数组
    private void initFragment()
    {

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragments = new Fragment[]{fragment1,fragment2,fragment3};
        lastfragment=0;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview,fragment1).show(fragment1).commit();
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bnv);

        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }
    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.id1:
                {
                    if(lastfragment!=0)
                    {
                        switchFragment(lastfragment,0);
                        lastfragment=0;

                    }

                    return true;
                }
                case R.id.id2:
                {
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;

                    }

                    return true;
                }
                case R.id.id3:
                {
                    if(lastfragment!=2)
                    {
                        switchFragment(lastfragment,2);
                        lastfragment=2;

                    }

                    return true;
                }


            }


            return false;
        }
    };
    //切换Fragment
    private void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);


        }
        transaction.show(fragments[index]).commitAllowingStateLoss();


    }


    private void init(){
        final TextView txtResult = (TextView)findViewById(R.id.textView1);

        //btnStart
        Button btnGetIP = (Button)findViewById(R.id.button1);
        View.OnClickListener listener1 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String address = null;
                try{
                    address = getIPAddress();
                }catch(Exception ex){
                    Log.e("MainActivity", ex.getMessage());
                    address = "error: " + ex.getMessage();
                }
                txtResult.setText(address);
            }
        };
        btnGetIP.setOnClickListener(listener1);
    }

    private String getIPAddress() throws SocketException {
        String address = null;
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){//2G,3G,4G
                address = getIPAddressForNetwork();
            }else if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){//WIFI
                address = getIPAddressForWifi();
            }
        }
        return address;
    }

    private String getIPAddressForNetwork() throws SocketException {
        String address = null;
        for(Enumeration<NetworkInterface> enum1 = NetworkInterface.getNetworkInterfaces(); enum1.hasMoreElements();){
            NetworkInterface networkInterface = enum1.nextElement();
            for(Enumeration<InetAddress> enum2 = networkInterface.getInetAddresses(); enum2.hasMoreElements();){
                InetAddress inetAddress = enum2.nextElement();
                if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address){
                    address = "type: 手机网络\n本机IP: " + inetAddress.getHostAddress().toString();
                }
            }
        }
        return address;
    }

    private String getIPAddressForWifi() {
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String address = "type: WIFI\nIP地址: " + intIP2String(wifiInfo.getIpAddress()) + "\nMAC地址：" + wifiInfo.getMacAddress();
        return address;
    }

    private String intIP2String(int ip){
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

}