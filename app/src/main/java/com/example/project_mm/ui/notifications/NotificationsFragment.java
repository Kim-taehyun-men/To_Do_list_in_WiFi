package com.example.project_mm.ui.notifications;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_mm.R;
import com.example.project_mm.databinding.FragmentNotificationsBinding;
import com.example.project_mm.myDBHelper;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    ///DB
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ///////// DB
        myHelper = new myDBHelper(root.getContext());


        /////////////////////
        EditText edtname = (EditText) root.findViewById(R.id.edtName);

        Button btnselect = (Button) root.findViewById(R.id.btnSelect);
        Button btndelete = (Button) root.findViewById(R.id.btnDelete);

        TextView wfname = (TextView) root.findViewById(R.id.wfName);
        TextView wfmain = (TextView) root.findViewById(R.id.wfMain);

        myHelper = new myDBHelper(getActivity().getApplicationContext());

        btndelete.setOnClickListener(new View.OnClickListener() {  // delete button
            @Override
            public void onClick(View view) {
                String target = edtname.getText().toString();
                if(target.equals(""))
                    Toast.makeText(getActivity().getApplicationContext(), "Please Enter Delete Group ", Toast.LENGTH_SHORT).show();
                else{
                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.execSQL("DELETE FROM groupTBL WHERE gName = '" + target + "'; ");
                    sqlDB.close();
                    Toast.makeText(getActivity().getApplicationContext(), "Delete Completed", Toast.LENGTH_LONG).show();
                    btnselect.callOnClick();
                    edtname.setText("");

                }
            }
        });

        btnselect.setOnClickListener(new View.OnClickListener() { // Search button
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;", null);

                String strNames1 = "WIFI/MAC Name\r\n" + "----------" + "\r\n";
                String strNames2 = "";
                String strMains1 = "list\r\n" + "--------------------" + "\r\n";
                String strMains2 = "";

                String strMains = "";
                String strNames = "";
                while(cursor.moveToNext()){
                    strNames2 = cursor.getString(0) + "\r\n"; // mac
                    strNames1 = cursor.getString(1) + "\r\n"; // wifi
                    strMains1 = cursor.getString(2) + "\r\n"; // list1
                    strMains2 = cursor.getString(3) + "\r\n"; // list2
                    strMains += strMains1 + strMains2;
                    strNames += strNames1 + strNames2;
                }
                wfname.setText(strNames);   //Display wifiName, MAC
                wfmain.setText(strMains);   //Display lists

                cursor.close();
                sqlDB.close();
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
