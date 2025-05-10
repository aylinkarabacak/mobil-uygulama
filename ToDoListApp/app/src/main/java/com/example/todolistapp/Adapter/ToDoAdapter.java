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

        holder.mDateView.setText(item.getDate());

        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            holder.mImageView.setImageURI(android.net.Uri.parse(item.getImageUri()));
        } else {
            holder.mImageView.setImageResource(R.drawable.baseline_done_all_24); // default görsel
        }


        holder.mCheckBox.setOnClickListener(v -> {
            taskAction.execute(item);
        });

        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                myDB.updateStatus(item.getId(), 1); // Tamamlandı olarak işaretle
            } else {
                myDB.updateStatus(item.getId(), 0); // Tamamlanmadı olarak işaretle
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
        bundle.putString("imageUri", item.getImageUri());

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
        ImageView mImageView;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            mDateView = itemView.findViewById(R.id.textview_date);
            mImageView = itemView.findViewById(R.id.image_preview);
        }
    }
}
