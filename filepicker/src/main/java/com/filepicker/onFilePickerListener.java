package com.filepicker;

public interface onFilePickerListener {
    void Success(ModelFiles files);
    void Error(String error);
}
