package com.cos.phoneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private RecyclerView rvPhoneList;
    private PhoneAdapter phoneAdapter;
    private FloatingActionButton fabSave;
    private Call<CMRespDto<List<Phone>>> call;
    private EditText etName;
    private EditText etTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting();
        findAll();
        save();
    }

    public void setting() {
        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
        call = phoneService.findAll();
        fabSave = findViewById(R.id.fab_save);

    }

    public void findAll() {
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                List<Phone> phones = cmRespDto.getData();
                //어댑터 넘기기
                LinearLayoutManager manger = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL,false);
                rvPhoneList = findViewById(R.id.rv_phone);
                rvPhoneList.setLayoutManager(manger);

                phoneAdapter = new PhoneAdapter(phones);

                rvPhoneList.setAdapter(phoneAdapter);
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패");
            }
        });
    }

    public void save() {
        fabSave.setOnClickListener(v -> {


        View dialog = v.inflate(v.getContext(),R.layout.dialog_item, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());

        etName = dialog.findViewById(R.id.ti_name);
        etTel = dialog.findViewById(R.id.ti_tel);

        dlg.setTitle("연락처 등록");
        dlg.setView(dialog);
        dlg.setNegativeButton("닫기", null);
        dlg.setPositiveButton("등록",(dialogInterface, i) -> {
            Phone phone = new Phone(null, etName.getText()+"", etTel.getText()+"");
            PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
            Call<CMRespDto<Phone>> call = phoneService.save(phone);

            call.enqueue(new Callback<CMRespDto<Phone>>() {
                @Override
                public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                    phoneAdapter.addItem(response.body().getData());
                }

                @Override
                public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                    Log.d(TAG, "onFailure: save 실패" + t.getMessage());
                }
            });

        });
             dlg.show();
        });
    }
}