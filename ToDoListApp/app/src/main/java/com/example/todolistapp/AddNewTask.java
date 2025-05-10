package com.example.todolistapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private Button mSaveButton, mDateButton, mImageButton;
    private TextView mDateTextView;
    private ImageView mImagePreview;
    private Uri selectedImageUri = null;
    private String selectedDate = "";

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
        mDateTextView = view.findViewById(R.id.textview_date);
        mImagePreview = view.findViewById(R.id.image_preview);

        myDb = new DataBaseHelper(getActivity());
        boolean isUpdate = false;

        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            mEditText.setText(task);
            String date = bundle.getString("date");
            if (date != null && !date.isEmpty()) {
                selectedDate = date;
                mDateTextView.setText("Tarih: " + selectedDate);
            }

// Görsel varsa göster
            String imageUri = bundle.getString("imageUri");
            if (imageUri != null && !imageUri.isEmpty()) {
                selectedImageUri = Uri.parse(imageUri);
                mImagePreview.setImageURI(selectedImageUri);
            }

            if (task.length() > 0) {
                mSaveButton.setEnabled(true);
            }
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveButton.setEnabled(!s.toString().isEmpty());
                mSaveButton.setBackgroundColor(s.toString().isEmpty() ? Color.GRAY : getResources().getColor(R.color.colorPrimary));
            }
            @Override public void afterTextChanged(Editable s) {}
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

        mImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        final boolean finalIsUpdate = isUpdate;
        mSaveButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString();

            if (!text.isEmpty()) {
                ToDoModel task = new ToDoModel();
                task.setTask(text);
                task.setDate(selectedDate);
                if (selectedImageUri != null) {
                    task.setImageUri(selectedImageUri.toString());
                }

                if (finalIsUpdate) {
                    myDb.updateTask(getArguments().getInt("id"), text, selectedDate, selectedImageUri != null ? selectedImageUri.toString() : null);
                } else {
                    myDb.insertTask(task);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            mImagePreview.setImageURI(selectedImageUri);
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
