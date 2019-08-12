package com.hynet.heebit.components.widget.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.text.TextUtils;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.hynet.heebit.components.utils.LogUtil;

final class CameraConfigurationManager {

    private final Context context;
    private int cwNeededRotation;
    private int cwRotationFromDisplayToCamera;
    private Point screenResolution;
    private Point cameraResolution;
    private Point bestPreviewSize;
    private Point previewSizeOnScreen;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    void initializeFromCameraParameters(OpenCamera openCamera) {
        Camera.Parameters parameters = openCamera.getCamera().getParameters();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayRotation = display.getRotation();
        int cwRotationFromNaturalToDisplay;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                cwRotationFromNaturalToDisplay = 0;
                break;
            case Surface.ROTATION_90:
                cwRotationFromNaturalToDisplay = 90;
                break;
            case Surface.ROTATION_180:
                cwRotationFromNaturalToDisplay = 180;
                break;
            case Surface.ROTATION_270:
                cwRotationFromNaturalToDisplay = 270;
                break;
            default:
                // Have seen this return incorrect values like -90
                if (displayRotation % 90 == 0) {
                    cwRotationFromNaturalToDisplay = (360 + displayRotation) % 360;
                } else {
                    throw new IllegalArgumentException("Bad rotation: " + displayRotation);
                }
        }
         LogUtil.Companion.getInstance().print("Display at: " + cwRotationFromNaturalToDisplay);
        int cwRotationFromNaturalToCamera = openCamera.getOrientation();
         LogUtil.Companion.getInstance().print("Camera at: " + cwRotationFromNaturalToCamera);
        if (openCamera.getFacing() == CameraFacing.FRONT) {
            cwRotationFromNaturalToCamera = (360 - cwRotationFromNaturalToCamera) % 360;
             LogUtil.Companion.getInstance().print("Front camera overriden to: " + cwRotationFromNaturalToCamera);
        }
        cwRotationFromDisplayToCamera = (360 + cwRotationFromNaturalToCamera - cwRotationFromNaturalToDisplay) % 360;
         LogUtil.Companion.getInstance().print("Final display orientation: " + cwRotationFromDisplayToCamera);
        if (openCamera.getFacing() == CameraFacing.FRONT) {
             LogUtil.Companion.getInstance().print("Compensating rotation for front camera");
            cwNeededRotation = (360 - cwRotationFromDisplayToCamera) % 360;
        } else {
            cwNeededRotation = cwRotationFromDisplayToCamera;
        }
         LogUtil.Companion.getInstance().print("Clockwise rotation from display to camera: " + cwNeededRotation);
        Point screenResolution = new Point();
        display.getSize(screenResolution);
        this.screenResolution = screenResolution;
        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = screenResolution.x;
        screenResolutionForCamera.y = screenResolution.y;
        if (screenResolution.x < screenResolution.y) {
            screenResolutionForCamera.x = screenResolution.y;
            screenResolutionForCamera.y = screenResolution.x;
        }
         LogUtil.Companion.getInstance().print("Screen resolution in current orientation: " + this.screenResolution);
        cameraResolution = CameraConfigurationUtil.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
//        cameraResolution = CameraConfigurationUtil.findBestPreviewSizeValue(parameters, this.screenResolution);
         LogUtil.Companion.getInstance().print("Camera resolution: " + cameraResolution);
        bestPreviewSize = CameraConfigurationUtil.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
//        bestPreviewSize = CameraConfigurationUtil.findBestPreviewSizeValue(parameters, this.screenResolution);
         LogUtil.Companion.getInstance().print("Best available preview size: " + bestPreviewSize);
        boolean isScreenPortrait = this.screenResolution.x < this.screenResolution.y;
        boolean isPreviewSizePortrait = bestPreviewSize.x < bestPreviewSize.y;
        if (isScreenPortrait == isPreviewSizePortrait) {
            previewSizeOnScreen = bestPreviewSize;
        } else {
            previewSizeOnScreen = new Point(bestPreviewSize.y, bestPreviewSize.x);
        }
         LogUtil.Companion.getInstance().print("Preview size on screen: " + previewSizeOnScreen);
    }


    void setDesiredCameraParameters(OpenCamera openCamera, boolean safeMode) {
        Camera camera = openCamera.getCamera();
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) {
             LogUtil.Companion.getInstance().print("Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }
         LogUtil.Companion.getInstance().print("Initial camera parameters: " + parameters.flatten());
        if (safeMode) {
             LogUtil.Companion.getInstance().print("In camera config safe mode -- most settings will not be honored");
        }
        parameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(cwRotationFromDisplayToCamera);
        Camera.Size size = camera.getParameters().getPreviewSize();
        if (size != null && (bestPreviewSize.x != size.width || bestPreviewSize.y != size.height)) {
             LogUtil.Companion.getInstance().print("Camera said it supported preview size " + bestPreviewSize.x + 'x' + bestPreviewSize.y + ", but after setting it, preview size is " + size.width + 'x' + size.height);
            bestPreviewSize.x = size.width;
            bestPreviewSize.y = size.height;
        }
    }

    Point getBestPreviewSize() {
        return bestPreviewSize;
    }

    Point getPreviewSizeOnScreen() {
        return previewSizeOnScreen;
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    int getCWNeededRotation() {
        return cwNeededRotation;
    }

    boolean getTorchState(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters != null) {
                String flashMode = parameters.getFlashMode();
                return !TextUtils.isEmpty(flashMode) && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode));
            }
        }
        return false;
    }

//    private static final int TEN_DESIRED_ZOOM = 27;
//    private final Context context;
//    private Point screenResolution;
//    private Point cameraResolution;
//    private int previewFormat;
//    private String previewFormatString;
//
//    CameraConfigurationManager(Context context) {
//        this.context = context;
//    }
//
//    void initializeFromCameraParameters(Camera camera) {
//        Camera.Parameters parameters = camera.getParameters();
//        previewFormat = parameters.getPreviewFormat();
//        previewFormatString = parameters.get("preview-format");
//         LogUtil.Companion.getInstance().getInstance().print("Default preview format: " + previewFormat + '/' + previewFormatString);
//        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = manager.getDefaultDisplay();
//        screenResolution = new Point(display.getWidth(), display.getHeight());
//         LogUtil.Companion.getInstance().print("Screen resolution: " + screenResolution);
//        Point screenResolutionForCamera = new Point();
//        screenResolutionForCamera.x = screenResolution.x;
//        screenResolutionForCamera.y = screenResolution.y;
//        if (screenResolution.x < screenResolution.y) {
//            screenResolutionForCamera.x = screenResolution.y;
//            screenResolutionForCamera.y = screenResolution.x;
//        }
//        cameraResolution = getCameraResolution(parameters, screenResolutionForCamera);
//         LogUtil.Companion.getInstance().print("Camera resolution: " + screenResolution);
//    }
//
//    void setDesiredCameraParameters(Camera camera) {
//        Camera.Parameters parameters = camera.getParameters();
//        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
//        int position;
//        if (supportedPreviewSizes.size() > 2) {
//            position = supportedPreviewSizes.size() / 2 + 1;//supportedPreviewSizes.get();
//        } else {
//            position = supportedPreviewSizes.size() / 2;
//        }
//        int width = supportedPreviewSizes.get(position).width;
//        int height = supportedPreviewSizes.get(position).height;
//         LogUtil.Companion.getInstance().print("Setting preview size: " + cameraResolution);
//        camera.setDisplayOrientation(90);
//        cameraResolution.x = width;
//        cameraResolution.y = height;
//        parameters.setPreviewSize(width, height);
//        setFlash(parameters);
//        setZoom(parameters);
//        camera.setParameters(parameters);
//    }
//
//    Point getCameraResolution() {
//        return cameraResolution;
//    }
//
//    Point getScreenResolution() {
//        return screenResolution;
//    }
//
//    int getPreviewFormat() {
//        return previewFormat;
//    }
//
//    String getPreviewFormatString() {
//        return previewFormatString;
//    }
//
//    private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {
//        String previewSizeValueString = parameters.get("preview-size-values");
//        // saw this on Xperia
//        if (previewSizeValueString == null) {
//            previewSizeValueString = parameters.get("preview-size-value");
//        }
//        Point cameraResolution = null;
//        if (previewSizeValueString != null) {
//             LogUtil.Companion.getInstance().print("preview-size-values parameter: " + previewSizeValueString);
//            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
//        }
//        if (cameraResolution == null) {
//            // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
//            cameraResolution = new Point(
//                    (screenResolution.x >> 3) << 3,
//                    (screenResolution.y >> 3) << 3);
//        }
//        return cameraResolution;
//    }
//
//    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
//        int bestX = 0;
//        int bestY = 0;
//        float diff = Float.MAX_VALUE;
//         LogUtil.Companion.getInstance().print("screen point: " + screenResolution);
//        for (String previewSize : Pattern.compile(Regex.COMMA.getRegext()).split(previewSizeValueString)) {
//            previewSize = previewSize.trim();
//            int dimPosition = previewSize.indexOf('x');
//            if (dimPosition < 0) {
//                 LogUtil.Companion.getInstance().print("Bad preview-size: " + previewSize);
//                continue;
//            }
//            int newX;
//            int newY;
//            try {
//                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
//                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
//            } catch (NumberFormatException nfe) {
//                 LogUtil.Companion.getInstance().print("Bad preview-size: " + previewSize);
//                continue;
//            }
//            float newDiff = Math.abs(screenResolution.x * 1.0f / newY - screenResolution.y * 1.0f / newX);
////            float newDiff = Math.abs(newY - screenResolution.x) + Math.abs(newX - screenResolution.y);
//            if (newDiff == 0) {
//                bestX = newX;
//                bestY = newY;
//                break;
//            } else if (newDiff < diff) {
//                bestX = newX;
//                bestY = newY;
//                diff = newDiff;
//            }
//             LogUtil.Companion.getInstance().print("preview-size: " + previewSize + ", newDiff: " + newDiff + ", diff: " + diff);
//        }
//        if (bestX > 0 && bestY > 0) {
//            return new Point(bestX, bestY);
//        }
//        return null;
//    }
//
//    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
//        int tenBestValue = 0;
//        for (String stringValue : Pattern.compile(Regex.COMMA.getRegext()).split(stringValues)) {
//            stringValue = stringValue.trim();
//            double value;
//            try {
//                value = Double.parseDouble(stringValue);
//            } catch (NumberFormatException nfe) {
//                return tenDesiredZoom;
//            }
//            int tenValue = (int) (10.0 * value);
//            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
//                tenBestValue = tenValue;
//            }
//        }
//        return tenBestValue;
//    }
//
//    private void setFlash(Camera.Parameters parameters) {
//        if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) { // 3 = Cupcake
//            parameters.set("flash-value", 1);
//        } else {
//            parameters.set("flash-value", 2);
//        }
//        parameters.set("flash-mode", "off");
//    }
//
//    private void setZoom(Camera.Parameters parameters) {
//        String zoomSupportedString = parameters.get("zoom-supported");
//        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
//            return;
//        }
//        int tenDesiredZoom = TEN_DESIRED_ZOOM;
//        String maxZoomString = parameters.get("max-zoom");
//        if (maxZoomString != null) {
//            try {
//                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
//                if (tenDesiredZoom > tenMaxZoom) {
//                    tenDesiredZoom = tenMaxZoom;
//                }
//            } catch (NumberFormatException nfe) {
//                 LogUtil.Companion.getInstance().print("Bad max-zoom: " + maxZoomString);
//
//            }
//        }
//        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
//        if (takingPictureZoomMaxString != null) {
//            try {
//                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
//                if (tenDesiredZoom > tenMaxZoom) {
//                    tenDesiredZoom = tenMaxZoom;
//                }
//            } catch (NumberFormatException nfe) {
//                 LogUtil.Companion.getInstance().print("Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
//            }
//        }
//        String motZoomValuesString = parameters.get("mot-zoom-values");
//        if (motZoomValuesString != null) {
//            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
//        }
//        String motZoomStepString = parameters.get("mot-zoom-step");
//        if (motZoomStepString != null) {
//            try {
//                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
//                int tenZoomStep = (int) (10.0 * motZoomStep);
//                if (tenZoomStep > 1) {
//                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
//                }
//            } catch (NumberFormatException nfe) {
//                // continue
//            }
//        }
//        if (maxZoomString != null || motZoomValuesString != null) {
//            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
//        }
//        if (takingPictureZoomMaxString != null) {
//            parameters.set("taking-picture-zoom", tenDesiredZoom);
//        }
//    }
}
