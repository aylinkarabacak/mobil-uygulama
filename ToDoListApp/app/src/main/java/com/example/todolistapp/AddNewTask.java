package com.example.todolistapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Button mSaveButton, mDateButton, mImageButton, mTimeButton, mRemoveImageButton;
    private TextView mDateTextView, mTimeTextView;
    private ImageView mImagePreview;
    private Uri selectedImageUri = null;
    private String selectedDate = "";
    private int selectedHour = 0, selectedMinute = 0;
    private int taskId = -1;

    private DataBaseHelper myDb;
    private static final int PICK_IMAGE_REQUEST = 1;

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
        mRemoveImageButton = view.findViewById(R.id.button_remove_image);
        mDateTextView = view.findViewById(R.id.textview_date);
        mTimeTextView = view.findViewById(R.id.textview_time);
        mImagePreview = view.findViewById(R.id.image_preview);

        myDb = new DataBaseHelper(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            taskId = bundle.getInt("id", -1);
            String task = bundle.getString("task");
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            String imageUri = bundle.getString("imageUri");

            mEditText.setText(task);
            mDateTextView.setText("Tarih: " + date);
            mTimeTextView.setText("Saat: " + time);

            selectedDate = date;
            if (time != null && !time.isEmpty()) {
                String[] timeParts = time.split(":");
                if (timeParts.length == 2) {
                    selectedHour = Integer.parseInt(timeParts[0]);
                    selectedMinute = Integer.parseInt(timeParts[1]);
                }
            }

            if (imageUri != null && !imageUri.isEmpty()) {
                selectedImageUri = Uri.parse(imageUri);
                mImagePreview.setImageURI(selectedImageUri);
                mRemoveImageButton.setVisibility(View.VISIBLE);
            }
        }

        mTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                mTimeTextView.setText(String.format("Saat: %02d:%02d", selectedHour, selectedMinute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        mDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                selectedDate = sdf.format(selectedCal.getTime());
                mDateTextView.setText("Tarih: " + selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        mSaveButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString();
            if (!text.isEmpty()) {
                ToDoModel task = new ToDoModel();
                task.setId(taskId);
                task.setTask(text);
                task.setDate(selectedDate);
                String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                task.setTime(selectedTime);

                if (selectedImageUri != null) {
                    task.setImageUri(selectedImageUri.toString());
                }

                if (taskId != -1) {
                    myDb.updateTask(task);
                } else {
                    myDb.insertTask(task);
                }
                dismiss();
            }
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            mImagePreview.setImageURI(selectedImageUri);
            mRemoveImageButton.setVisibility(View.VISIBLE);
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
