package com.filepicker;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.filepicker.databinding.LayoutFilePickerBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class FilePicker extends BottomSheetDialogFragment {
    private LayoutFilePickerBinding binding;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    private onFilePickerListener listener;
    private String permission[]={READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,CAMERA};

    public static FilePicker get() {
        return new FilePicker();
    }
    public FilePicker Callback(onFilePickerListener listener){
        this.listener=listener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.layout_file_picker,null,false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        BindView();
        return dialog;
    }

    private void BindView() {
        if (!CheckPermission()){
            requestPermissions(permission,100);
        }
        binding.imgCamera.setOnClickListener(v->{
            if (!CheckPermission()){
                requestPermissions(permission,100);
                return;
            }
            dispatchTakePictureIntent();
        }); binding.imgGallery.setOnClickListener(v->{
            if (!CheckPermission()){
                requestPermissions(permission,100);
                return;
            }
            PickFromGallery();
        });
    }
    private boolean CheckPermission(){
        for (String per:permission){
           boolean isAccepted= ContextCompat.checkSelfPermission(getContext(),per)==0;
           if (!isAccepted){
               return false;
           }
        }
        return true;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (Exception e) {
                e.printStackTrace();
                if (listener!=null){
                    listener.Error(e.getLocalizedMessage());
                }
            }
        }
    }
    private void PickFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,REQUEST_IMAGE_GALLERY);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ModelFiles files=new ModelFiles();
            files.setBitmap(imageBitmap);
            if (listener!=null){
                listener.Success(files);
                dismiss();
            }
        }
        if (requestCode==REQUEST_IMAGE_GALLERY){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            if (bitmap != null) {
                ModelFiles files=new ModelFiles();
                files.setBitmap(bitmap);
                files.setPath(picturePath);
                if (listener!=null){
                    listener.Success(files);
                    dismiss();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
