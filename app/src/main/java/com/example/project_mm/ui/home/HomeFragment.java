package com.example.project_mm.ui.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_mm.R;
import com.example.project_mm.databinding.FragmentHomeBinding;
import com.example.project_mm.myDBHelper;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    ////////////////////DB
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    String WifiName = "";

    //////////Banner Alarm
    NotificationManager manager;
    NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    //////// wifi, mac, list
    String strNames1 = "";
    String strNames2 = "";
    String strMains1 = "";
    String strMains2 = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView wifinamecon = (TextView) root.findViewById(R.id.wifi_name_con);
        TextView wifimaincon1 = (TextView) root.findViewById(R.id.wifi_main_con1);
        TextView wifimaincon2 = (TextView) root.findViewById(R.id.wifi_main_con2);

        myHelper = new myDBHelper(getActivity().getApplicationContext());

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;", null);

        WifiName = getNetworkName(root.getContext());
         // Cursor read tuple
        while(cursor.moveToNext()){

            strNames1 = cursor.getString(0) ; // mac
            strNames2 = cursor.getString(1) ; // wifi
            strMains1 = cursor.getString(2) ; // list1
            strMains2 = cursor.getString(3) ; // list2

            if(strNames1.equals(WifiName))
            // If MacAddress is the same, display the wifi(ssid) and lists
            {
                wifinamecon.setText("Connected Wi-Fi: " + strNames2);
                wifimaincon1.setText(strMains1);
                wifimaincon2.setText(strMains2);
                showNoti(); // Banner Alarm Display start
                break;
            }
            else{
                wifinamecon.setText("There is no Set/Connected WIFI.");
                wifimaincon1.setText("");
                wifimaincon2.setText("");
            }
        }

        cursor.close();
        sqlDB.close();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String getNetworkName(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getBSSID();
    }

    // Banner Alarm
    public void showNoti(){
        builder = null;
        manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        //Version Oreo 이상
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(getActivity().getApplicationContext(),CHANNEL_ID);
            // 이하
        }else{
        }
        //Banner Alarm Title
        builder.setContentTitle(strNames2+" Connected");
        //Banner Alarm Message
        builder.setContentText("Schedule Exists");
        //Banner Alarm Icon
        builder.setSmallIcon(R.drawable.icon);
        Notification notification = builder.build();
        //Banner Alarm Execute
        manager.notify(1,notification);
    }
}

