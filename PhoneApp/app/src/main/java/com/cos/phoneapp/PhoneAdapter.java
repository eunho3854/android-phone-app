package com.cos.phoneapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 어댑터와 RecyclerView와 연결 (Databinding 사용금지) (MVVM 사용금지)
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder> {

    private final List<Phone> phones;
    private static final String TAG = "PhoneAdapter";

    public PhoneAdapter(List<Phone> phones) {
        this.phones = phones;
    }

    public void addItem(Phone phone) {
        phones.add(phone);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        phones.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phone_item, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setItem(phones.get(position));
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvTel;
        private EditText etName, etTel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvTel = itemView.findViewById(R.id.tel);
            PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);

            // AlertDialog (Update, Delete)
            itemView.setOnClickListener(v -> {
                View dialog = v.inflate(v.getContext(),R.layout.dialog_item, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());

                etName = dialog.findViewById(R.id.ti_name);
                etTel = dialog.findViewById(R.id.ti_tel);
                int pos = getAdapterPosition();
                Phone phone = phones.get(pos);

                etName.setText(tvName.getText());
                etTel.setText(tvTel.getText());

                dlg.setTitle("연락처 수정");
                dlg.setView(dialog);
                dlg.setNegativeButton("닫기", null);

                // Update
                dlg.setPositiveButton("수정",(dialogInterface, i) -> {
                    phone.setName(etName.getText()+"");
                    phone.setTel(etTel.getText()+"");
                    Call<CMRespDto<Phone>> call = phoneService.update(phone.getId(),phone);

                    call.enqueue(new Callback<CMRespDto<Phone>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                            Log.d(TAG, "onResponse: update 성공 : " + response.body());
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                            Log.d(TAG, "onFailure: update 실패" + t.getMessage());
                        }
                    });
                }); // Update

                // Delete()
                dlg.setNegativeButton("삭제", ((dialogInterface, i) -> {
                    Call<CMRespDto<String>> call = phoneService.delete(phone.getId());
                    call.enqueue(new Callback<CMRespDto<String>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<String>> call, Response<CMRespDto<String>> response) {
                            Log.d(TAG, "onResponse: delete 성공 : " + response.body());
                            removeItem(pos);
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<String>> call, Throwable t) {
                            Log.d(TAG, "onResponse: delete 실패 : " + t.getMessage());
                        }
                    });
                })); // Delete()
                dlg.show();
            });
        }

        public void setItem(Phone phone) {
            tvName.setText(phone.getName());
            tvTel.setText(phone.getTel());
        }
    }
}
