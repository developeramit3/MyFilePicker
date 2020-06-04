package com.myfilepicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import android.widget.Toast;

import com.filepicker.FilePicker;
import com.filepicker.ModelFiles;
import com.filepicker.onFilePickerListener;
import com.myfilepicker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.btnCamera.setOnClickListener(v->{
            FilePicker.get().Callback(new onFilePickerListener() {
                @Override
                public void Success(ModelFiles files) {
                    binding.image.setImageURI(files.getUri());
                }

                @Override
                public void Error(String error) {
                    Toast.makeText(MainActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                }
            }).show(getSupportFragmentManager(),"");
        });
        binding.btnGallery.setOnClickListener(v->{
            FilePicker.get().Callback(new onFilePickerListener() {
                @Override
                public void Success(ModelFiles files) {
                    binding.image.setImageBitmap(files.getBitmap());
                }

                @Override
                public void Error(String error) {
                    Toast.makeText(MainActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                }
            }).show(getSupportFragmentManager(),"");
        });
    }
}
