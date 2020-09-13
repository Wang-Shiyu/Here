package com.example.great.lab9.view;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.baidu.mapapi.SDKInitializer;
import com.example.great.lab9.R;

import static android.content.Context.LOCATION_SERVICE;

public class LocationFragment extends BaseFragment {

    private MapView mapView;
    public BaiduMap baiduMap = null;


    private Intent intent;
    private Button button;
    private TextView txtLat;
    private TextView txtLon;
    private double dLat;
    private double dLon;
    public LocationClient locationClient = null;
    List<Address> locationList = null;
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            LatLng latLng = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 16);   //设置地图中心点以及缩放级别
            dLat=locData.latitude;
            dLon=locData.longitude;
            resultList.add(dLat);
            resultList.add(dLon);
            txtLat.setText("纬度"+String.valueOf(locData.latitude));
            txtLon.setText("经度"+String.valueOf(locData.longitude));

            String lat = locData.latitude+ "";
            lat = lat.substring(0,7);
            String lon = locData.longitude + "";
            lon = lon.substring(0,8);

            LOCATION = lat + lon;
            //Toast.makeText(getActivity().getApplicationContext(),LOCATION,Toast.LENGTH_LONG).show();

            baiduMap = mapView.getMap();
            baiduMap.setMyLocationEnabled(true);
            baiduMap.setMyLocationData(locData);
            baiduMap.animateMapStatus(u);
            setOnePointToMap(dLat,dLon);
        }
    };

    private void setOnePointToMap(Double Lat,Double Lon) {

        // 2 描述
        BitmapDescriptor descriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_pic);

        // 3 位置 纬经度
        LatLng latLng = new LatLng(Lat, Lon);

        // 1 覆盖一层 透视的 图层！
        OverlayOptions overlayOptions = new MarkerOptions().title("广州站")
                .icon(descriptor).position(latLng);

        // 向地图添加一个 Overlay
        baiduMap.addOverlay(overlayOptions);

    }


    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02 ;bd09ll
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
        locationClient.setLocOption(option);
    }



    long MIN_TIME = 1000l;
    float MIN_DISTANCE = 10f;

    LocationManager locationManager;
    String locationProvider;
    Button go;

    public static  String   LOCATION="";
    public static List<Double> resultList =  new ArrayList<Double>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SDKInitializer.initialize(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_location, container, false);


        isPrepared = true;

        mapView = (MapView)view.findViewById(R.id.BDMap);



        locationClient = new LocationClient(this.getContext()); // 实例化LocationClient类
        locationClient.registerLocationListener(myListener); // 注册监听函数
        this.setLocationOption();   //设置定位参数
        locationClient.start(); // 开始定位

        txtLat = (TextView) view.findViewById(R.id.txtLat);
        txtLon = (TextView) view.findViewById(R.id.txtLon);

        loadData();

        return view;
    }





    @Override
    protected void loadData() {


        if (!isPrepared || !isVisible) {
            return;
        }

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        //Network定位
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } //GPS定位
        else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(getActivity(), "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            //无法定位：1、提示用户打开定位服务；2、跳转到设置界面
            Toast.makeText(getActivity(), "无法定位，请打开定位服务", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
            return;
        }

        if (!locationProvider.isEmpty()) {
            //获取Location
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                //不为空,显示地理位置经纬度
                showLocation(location);
            }
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, MIN_TIME, MIN_DISTANCE, locationListener);
        }
    }

    /**
     * 显示地理位置经度和纬度信息
     *
     * @param location
     */
    private void showLocation(Location location) {
        String locationStr = "纬度：" + location.getLatitude() + "\n\n" + "经度：" + location.getLongitude()+ "\n\n";

        //List<Double> doubleList= new ArrayList<Double>();



        String lat = location.getLatitude()+ "";
        lat = lat.substring(0,7);
        String lon = location.getLongitude() + "";
        lon = lon.substring(0,8);
        //LOCATION = lat + lon;

        Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());

        try {
            locationList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = locationList.get(0);//得到Address实例

        for (int i = 0; address.getAddressLine(i) != null; i++) {
            String addressLine = address.getAddressLine(i);//得到周边信息。包含街道等。i=0，得到街道名称
            locationStr += "地区名：" + addressLine;
        }
        //postionView.setText(locationStr);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //位置发生变化,重新显示
            showLocation(location);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除监听器
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }



}