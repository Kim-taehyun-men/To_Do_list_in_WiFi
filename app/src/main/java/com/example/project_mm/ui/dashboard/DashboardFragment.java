package com.example.project_mm.ui.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mm.R;
import com.example.project_mm.databinding.FragmentDashboardBinding;
import com.example.project_mm.myDBHelper;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {

    public FragmentDashboardBinding binding;
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;

    TextView wifiname;
    TextView MacName;

    public List<ScanResult> results;

    final public BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {   // wifiManager.startScan(); 시  발동되는 메소드 ( 예제에서는 버튼을 누르면 startScan()을 했음. )

            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false); //스캔 성공 여부 값 반환
            if (success) {
                scanSuccess();
            } else {
                scanFailure();
            }

            // onReceive()..
        }
    };
    public void scanSuccess() {    // Wifi ScanSuccess
        results = wifiManager.getScanResults();

        ArrayList<String> saveWiFilist = new ArrayList<String>();
        ArrayList<String> saveMaclist = new ArrayList<String>();
        if (results.size() == 0)
            return;

        // wifi, MAC 값 받기
        for (int i = 0; i < results.size(); i++) {
            ScanResult a = results.get(i);
            saveWiFilist.add(a.SSID); //wifi
            saveMaclist.add(a.BSSID); //MAC
        }

        String[] saveWiFiarray = saveWiFilist.toArray(new String[saveWiFilist.size()]);
        String[] saveMacarray = saveMaclist.toArray(new String[saveWiFilist.size()]);

        AlertDialog.Builder dlg1 = new AlertDialog.Builder(getActivity());
        dlg1.setSingleChoiceItems(saveWiFiarray, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                wifiname.setText(saveWiFiarray[i]);//wifiname(ssid)
                MacName.setText(saveMacarray[i]);  // mac(bsssid)
            }
        });

        dlg1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity().getApplicationContext(),"Setup Complete",Toast.LENGTH_SHORT).show();
            }
        });
            //AlertDialog dlg = dlg1.create();
            dlg1.show();

    }

    public void scanFailure() {    // Wifi ScanFailure
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Wifi Scna 관련
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);


        //////////////////////////// DB

        Button btndialog = (Button) root.findViewById(R.id.btn_dialog); // wifi 서치
        Button btnadd = (Button) root.findViewById(R.id.btn_add);       // 내용 저장

        MacName = (TextView) root.findViewById(R.id.text_gallery);      // MAC
        wifiname = (TextView) root.findViewById(R.id.text_gallery18);   // wifi name(ssid)

        EditText edtext1 = (EditText) root.findViewById(R.id.edtext_1); // list1
        EditText edtext2 = (EditText) root.findViewById(R.id.edtext_2); // list2

        ///////// DB
        myHelper = new myDBHelper(getActivity().getApplicationContext());


        ////////////////////////////////////////////////////////////// WIFI Search Button

        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = wifiManager.startScan();
                if (!success)
                    Toast.makeText(getActivity().getApplicationContext(), "Wifi Scan Failed", Toast.LENGTH_SHORT).show();
            }
        });

        ////////////////////////////////////////////////////////////// Save(ADD) list in DB
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO groupTBL VALUES('"+ MacName.getText().toString()
                        + "', '"+ wifiname.getText().toString() +"' ,'"+ edtext1.getText().toString() +"', '"+ edtext2.getText().toString() +"' );");
                sqlDB.close();
                Toast.makeText(getActivity().getApplicationContext(), "Input Completed", Toast.LENGTH_LONG).show();
                wifiname.setText("Selete Wi-Fi");
                MacName.setText("MACAddress");
                edtext1.setText("");
                edtext2.setText("");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}


