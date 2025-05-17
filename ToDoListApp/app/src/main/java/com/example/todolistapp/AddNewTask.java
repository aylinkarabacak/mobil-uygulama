package com.example.todolistapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolistapp.Model.ToDoModel;
import com.example.todolistapp.Utils.DataBaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private EditText mEditText;
    private Button mSaveButton, mDateButton, mImageButton, mTimeButton, mRemoveImageButton, mSoundButton;
    private TextView mDateTextView, mTimeTextView, mSoundTextView;
    private ImageView mImagePreview;
    private Uri selectedImageUri = null;
    private Uri selectedSoundUri = null;
    private String selectedDate = "";
    private int selectedHour = 0, selectedMinute = 0;
    private int taskId = -1;

    private DataBaseHelper myDb;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_SOUND_REQUEST = 2;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_newtask, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.edittext);
        mSaveButton = view.findViewById(R.id.button_save);
        mDateButton = view.findViewById(R.id.button_date);
        mImageButton = view.findViewById(R.id.button_image);
        mTimeButton = view.findViewById(R.id.button_time);
        mSoundButton = view.findViewById(R.id.button_sound);
        mRemoveImageButton = view.findViewById(R.id.button_remove_image);
        mDateTextView = view.findViewById(R.id.textview_date);
        mTimeTextView = view.findViewById(R.id.textview_time);
        mSoundTextView = view.findViewById(R.id.textview_sound);
        mImagePreview = view.findViewById(R.id.image_preview);

        myDb = new DataBaseHelper(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            taskId = bundle.getInt("id", -1);
            mEditText.setText(bundle.getString("task"));
            selectedDate = bundle.getString("date", "");
            String time = bundle.getString("time", "");

            mDateTextView.setText("Tarih: " + selectedDate);
            mTimeTextView.setText("Saat: " + time);

            if (!time.isEmpty()) {
                String[] parts = time.split(":");
                if (parts.length == 2) {
                    selectedHour = Integer.parseInt(parts[0]);
                    selectedMinute = Integer.parseInt(parts[1]);
                }
            }

            String imageUri = bundle.getString("imageUri");
            if (imageUri != null && !imageUri.isEmpty()) {
                selectedImageUri = Uri.parse(imageUri);
                mImagePreview.setImageURI(selectedImageUri);
                mRemoveImageButton.setVisibility(View.VISIBLE);
            }

            String soundUri = bundle.getString("soundUri");
            if (soundUri != null && !soundUri.isEmpty()) {
                selectedSoundUri = Uri.parse(soundUri);
                mSoundTextView.setText("Ses: " + RingtoneManager.getRingtone(getContext(), selectedSoundUri).getTitle(getContext()));
            }
        }

        mDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view1, year, month, day) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, day);
                selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selected.getTime());
                mDateTextView.setText("Tarih: " + selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        mTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(), (view1, hour, minute) -> {
                selectedHour = hour;
                selectedMinute = minute;
                mTimeTextView.setText(String.format("Saat: %02d:%02d", selectedHour, selectedMinute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        mImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        mRemoveImageButton.setOnClickListener(v -> {
            selectedImageUri = null;
            mImagePreview.setImageDrawable(null);
            mRemoveImageButton.setVisibility(View.GONE);
        });

        mSoundButton.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Bildirim sesi seç");
            startActivityForResult(intent, PICK_SOUND_REQUEST);
        });

        mSaveButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString().trim();
            if (text.isEmpty()) return;

            ToDoModel task = new ToDoModel();
            task.setId(taskId);
            task.setTask(text);
            task.setDate(selectedDate);
            String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            task.setTime(selectedTime);
            if (selectedImageUri != null) task.setImageUri(selectedImageUri.toString());
            if (selectedSoundUri != null) task.setSoundUri(selectedSoundUri.toString());

            if (taskId != -1) myDb.updateTask(task);
            else myDb.insertTask(task);

            setAlarm(task);

            dismiss();
        });
    }

    private void setAlarm(ToDoModel task) {
        if (selectedDate.isEmpty()) return;
        if (task.getStatus() == 1) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        Calendar calendar = Calendar.getInstance();
        String[] dateParts = selectedDate.split("/");
        if (dateParts.length != 3) return;

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);

        calendar.set(year, month, day, selectedHour, selectedMinute, 0);
        long alarmTime = calendar.getTimeInMillis();
        long currentTime = System.currentTimeMillis();

        if (alarmTime <= currentTime) {
            Toast.makeText(getContext(), "Geçmişe alarm kurulmaz.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.NOTIFICATION_TITLE, "Yapılacak İşin Var");
        intent.putExtra(NotificationPublisher.NOTIFICATION_TEXT, task.getTask());

        if (selectedSoundUri != null){
            intent.putExtra(NotificationPublisher.NOTIFICATION_SOUND_URI, selectedSoundUri.toString());
            intent.putExtra("taskId", task.getId());
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                task.getId(), // benzersiz ID kullan
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
            Toast.makeText(getContext(), "Alarm kuruldu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            mImagePreview.setImageURI(selectedImageUri);
            mRemoveImageButton.setVisibility(View.VISIBLE);
        }

        if (requestCode == PICK_SOUND_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedSoundUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (selectedSoundUri != null) {
                mSoundTextView.setText("Ses: " + RingtoneManager.getRingtone(getContext(), selectedSoundUri).getTitle(getContext()));
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListner) {
            ((OnDialogCloseListner) activity).onDialogClose(dialog);
        }
    }
}
