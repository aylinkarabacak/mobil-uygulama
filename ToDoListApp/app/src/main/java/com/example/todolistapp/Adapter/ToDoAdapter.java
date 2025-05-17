package com.example.todolistapp.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.AddNewTask;
import com.example.todolistapp.MainActivity;
import com.example.todolistapp.Model.ToDoModel;
import com.example.todolistapp.R;
import com.example.todolistapp.TaskAction;
import com.example.todolistapp.Utils.DataBaseHelper;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> mList;
    private MainActivity activity;
    private DataBaseHelper myDB;
    private TaskAction taskAction;

    public ToDoAdapter(DataBaseHelper myDB, MainActivity activity, TaskAction taskAction){
        this.activity = activity;
        this.myDB = myDB;
        this.taskAction = taskAction;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout , parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoModel item = mList.get(position);

        holder.mCheckBox.setText(item.getTask());
        holder.mCheckBox.setChecked(item.getStatus() == 1);

        holder.mDateView.setText("Tarih: " + item.getDate());
        holder.mTimeView.setText("Saat: " + item.getTime());

        // Bildirim sesi gösterimi
        if (item.getSoundUri() != null && !item.getSoundUri().isEmpty()) {
            android.net.Uri soundUri = android.net.Uri.parse(item.getSoundUri());
            android.media.Ringtone ringtone = android.media.RingtoneManager.getRingtone(holder.itemView.getContext(), soundUri);
            String title = (ringtone != null) ? ringtone.getTitle(holder.itemView.getContext()) : "Bilinmeyen Ses";
            holder.mSoundView.setText("Ses: " + title);
        } else {
            holder.mSoundView.setText("Ses: Yok");
        }

        // Foto varsa göster
        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            holder.mImageView.setImageURI(android.net.Uri.parse(item.getImageUri()));
        } else {
            holder.mImageView.setImageResource(R.drawable.baseline_done_all_24); // Default ikon
        }

        // Tıklama ve kontrol işlemleri
        holder.mCheckBox.setOnClickListener(v -> taskAction.execute(item));

        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                myDB.updateStatus(item.getId(), 1);

                // Alarm iptali
                android.app.AlarmManager alarmManager = (android.app.AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                android.content.Intent intent = new android.content.Intent(activity, com.example.todolistapp.NotificationPublisher.class);
                android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                        activity,
                        item.getId(),
                        intent,
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
                );

                if (alarmManager != null) alarmManager.cancel(pendingIntent);
            } else {
                myDB.updateStatus(item.getId(), 0);
            }
        });
    }

    public Context getContext(){
        return activity;
    }

    public void setTasks(List<ToDoModel> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position){
        ToDoModel item = mList.get(position);
        myDB.deleteTask(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        ToDoModel item = mList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("date", item.getDate());
        bundle.putString("time", item.getTime());
        bundle.putString("imageUri", item.getImageUri());
        bundle.putString("soundUri", item.getSoundUri());
        bundle.putInt("status", item.getStatus());

        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager(), task.getTag());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mDateView;
        TextView mTimeView;
        TextView mSoundView;
        ImageView mImageView;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            mDateView = itemView.findViewById(R.id.textview_date);
            mTimeView = itemView.findViewById(R.id.textview_time);
            mSoundView = itemView.findViewById(R.id.textview_sound);
            mImageView = itemView.findViewById(R.id.image_preview);
        }
    }
}
