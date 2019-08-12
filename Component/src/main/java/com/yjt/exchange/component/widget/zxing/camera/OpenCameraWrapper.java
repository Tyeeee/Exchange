package com.hynet.heebit.components.widget.zxing.camera;

import android.hardware.Camera;

import com.hynet.heebit.components.utils.LogUtil;

public final class OpenCameraWrapper {

    public static final int NO_REQUESTED_CAMERA = -1;

    private OpenCameraWrapper() {
    }

    public static int getCameraId(int requestedId) {
        int number = Camera.getNumberOfCameras();
        if (number == 0) {
             LogUtil.Companion.getInstance().print("No cameras!");
            return -1;
        }
        int cameraId = requestedId;
        boolean explicitRequest = cameraId >= 0;
        if (!explicitRequest) {
            int index = 0;
            while (index < number) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break;
                }
                index++;
            }
            cameraId = index;
        }
        if (cameraId < number) {
            return cameraId;
        } else {
            if (explicitRequest) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static OpenCamera open(int cameraId) {
        int number = Camera.getNumberOfCameras();
        if (number == 0) {
             LogUtil.Companion.getInstance().print("No cameras!");
            return null;
        }
        if (cameraId >= number) {
             LogUtil.Companion.getInstance().print("Requested camera does not exist: " + cameraId);
            return null;
        }
        if (cameraId <= NO_REQUESTED_CAMERA) {
            cameraId = 0;
            while (cameraId < number) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, cameraInfo);
                if (CameraFacing.values()[cameraInfo.facing] == CameraFacing.BACK) {
                    break;
                }
                cameraId++;
            }
            if (cameraId == number) {
                 LogUtil.Companion.getInstance().print("No camera facing " + CameraFacing.BACK + "; returning camera #0");
                cameraId = 0;
            }
        }
         LogUtil.Companion.getInstance().print("Opening camera #" + cameraId);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        Camera camera = Camera.open(cameraId);
        if (camera == null) {
            return null;
        }
        return new OpenCamera(cameraId, camera, CameraFacing.values()[cameraInfo.facing], cameraInfo.orientation);
    }

}
