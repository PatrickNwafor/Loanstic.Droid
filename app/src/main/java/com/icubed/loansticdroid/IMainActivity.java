package com.icubed.loansticdroid;


public interface IMainActivity {

    void setCameraFrontFacing();

    void setCameraBackFacing();

    boolean isCameraFrontFacing();

    boolean isCameraBackFacing();

    void setFrontCameraId(String cameraId);

    void setBackCameraId(String cameraId);

    String getFrontCameraId();

    String getBackCameraId();

    void hideStatusBar();





}
