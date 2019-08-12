package com.hynet.heebit.components.widget.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.hynet.heebit.components.utils.LogUtil;

import java.io.IOException;

public final class CameraManager {

    private static CameraManager cameraManager;
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
    private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080

    private final Context context;
    private final CameraConfigurationManager cameraConfigurationManager;
    private OpenCamera openCamera;
    private AutoFocusCallback autoFocusCallback;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    private int requestedCameraId = OpenCameraWrapper.NO_REQUESTED_CAMERA;
    private int requestedFramingRectWidth;
    private int requestedFramingRectHeight;
    private final PreviewCallback previewCallback;

    public CameraManager(Context context) {
        this.context = context;
        this.cameraConfigurationManager = new CameraConfigurationManager(context);
        this.previewCallback = new PreviewCallback(cameraConfigurationManager);
    }

    public static CameraManager getInstance(Context context) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(context);
        }
        return cameraManager;
    }

    public static CameraManager get() {
        return cameraManager;
    }

    public static void releaseInstance() {
        if (cameraManager != null) {
            cameraManager = null;
        }
    }

    public synchronized void openDriver(SurfaceHolder surfaceHolder) throws IOException {
        OpenCamera openCamera = this.openCamera;
        if (openCamera == null) {
            openCamera = OpenCameraWrapper.open(requestedCameraId);
            if (openCamera == null) {
                throw new IOException("Camera.open() failed to return object from driver");
            }
            this.openCamera = openCamera;
        }
        if (!initialized) {
            initialized = true;
            cameraConfigurationManager.initializeFromCameraParameters(openCamera);
            if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
                setManualFramingRect(requestedFramingRectWidth, requestedFramingRectHeight);
                requestedFramingRectWidth = 0;
                requestedFramingRectHeight = 0;
            }
        }
        Camera camera = openCamera.getCamera();
        Camera.Parameters parameters = camera.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save these, temporarily
        try {
            cameraConfigurationManager.setDesiredCameraParameters(openCamera, false);
        } catch (RuntimeException e1) {
            // Driver failed
             LogUtil.Companion.getInstance().print("Camera rejected parameters. Setting only minimal safe-mode parameters");
             LogUtil.Companion.getInstance().print("Resetting to saved openCamera params: " + parametersFlattened);
            if (parametersFlattened != null) {
                parameters = camera.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    camera.setParameters(parameters);
                    cameraConfigurationManager.setDesiredCameraParameters(openCamera, true);
                } catch (RuntimeException e2) {
                     LogUtil.Companion.getInstance().print("Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }
        camera.setPreviewDisplay(surfaceHolder);
    }

    public synchronized boolean isOpen() {
        return openCamera != null;
    }

    public synchronized void closeDriver() {
        if (openCamera != null) {
            openCamera.getCamera().release();
            openCamera = null;
            framingRect = null;
            framingRectInPreview = null;
        }
    }

    public synchronized void startPreview() {
        OpenCamera openCamera = this.openCamera;
        if (openCamera != null && !previewing) {
            openCamera.getCamera().startPreview();
            previewing = true;
            autoFocusCallback = new AutoFocusCallback(context, openCamera.getCamera());
        }
    }

    public synchronized void stopPreview() {
        if (autoFocusCallback != null) {
            autoFocusCallback.stop();
            autoFocusCallback = null;
        }
        if (openCamera != null && previewing) {
            openCamera.getCamera().stopPreview();
            previewCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    public synchronized void turnOn() {
        if (openCamera != null && openCamera.getCamera() != null) {
            Camera.Parameters parameter = openCamera.getCamera().getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            openCamera.getCamera().setParameters(parameter);
        }
    }

    public synchronized void turnOff() {
        if (openCamera != null && openCamera.getCamera() != null) {
            Camera.Parameters parameter = openCamera.getCamera().getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            openCamera.getCamera().setParameters(parameter);
        }
    }

    public void requestPreviewFrame(Handler handler, int message) {
        OpenCamera openCamera = this.openCamera;
        if (openCamera != null && previewing) {
            previewCallback.setHandler(handler, message);
            openCamera.getCamera().setOneShotPreviewCallback(previewCallback);
        }
    }

    public synchronized Rect getFramingRect() {
        if (framingRect == null) {
            if (openCamera == null) {
                return null;
            }
            Point screenResolution = cameraConfigurationManager.getScreenResolution();
            if (screenResolution == null) {
                return null;
            }
            int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
            int height = width;
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 3;
//            int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
//            int height = width;
//            int leftOffset = (screenResolution.x - width) / 2;
//            int topOffset = (screenResolution.y - height) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
             LogUtil.Companion.getInstance().print("scan_frame x:" + screenResolution.x);
             LogUtil.Companion.getInstance().print("scan_frame y:" + screenResolution.y);
             LogUtil.Companion.getInstance().print("scan_frame leftOffset:" + leftOffset);
             LogUtil.Companion.getInstance().print("scan_frame topOffset:" + topOffset);
             LogUtil.Companion.getInstance().print("scan_frame rightOffset:" + leftOffset + width);
             LogUtil.Companion.getInstance().print("scan_frame bottomOffset:" + topOffset + height);
             LogUtil.Companion.getInstance().print("Calculated framing rect: " + framingRect + ", width: " + framingRect.width() + ", height: " + framingRect.height());
        }
        return framingRect;
    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
        int dimension = 5 * resolution / 8; // Target 5/8 of each dimension
        if (dimension < hardMin) {
            return hardMin;
        }
        if (dimension > hardMax) {
            return hardMax;
        }
        return dimension;
    }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview
     * frame, not UI / screen.
     */
    public synchronized Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect framingRect = getFramingRect();
            if (framingRect == null) {
                return null;
            }
            Rect rect = new Rect(framingRect);
            Point cameraResolution = cameraConfigurationManager.getCameraResolution();
            Point screenResolution = cameraConfigurationManager.getScreenResolution();
            if (cameraResolution == null || screenResolution == null) {
                return null;
            }
            if (screenResolution.x < screenResolution.y) {
                // portrait
                rect.left = rect.left * cameraResolution.y / screenResolution.x;
                rect.right = rect.right * cameraResolution.y / screenResolution.x;
                rect.top = rect.top * cameraResolution.x / screenResolution.y;
                rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            } else {
                // landscape
                rect.left = rect.left * cameraResolution.x / screenResolution.x;
                rect.right = rect.right * cameraResolution.x / screenResolution.x;
                rect.top = rect.top * cameraResolution.y / screenResolution.y;
                rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
            }
            framingRectInPreview = rect;
             LogUtil.Companion.getInstance().print("rect.lef" + rect.left);
             LogUtil.Companion.getInstance().print("rect.right" + rect.right);
             LogUtil.Companion.getInstance().print("rect.top" + rect.top);
             LogUtil.Companion.getInstance().print("rect.bottom" + rect.bottom);
        }
        return framingRectInPreview;
    }

    public synchronized void setManualCameraId(int cameraId) {
        requestedCameraId = cameraId;
    }

    public synchronized void setManualFramingRect(int width, int height) {
        if (initialized) {
            Point screenResolution = cameraConfigurationManager.getScreenResolution();
            if (width > screenResolution.x) {
                width = screenResolution.x;
            }
            if (height > screenResolution.y) {
                height = screenResolution.y;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
             LogUtil.Companion.getInstance().print("Calculated manual framing rect: " + framingRect);
            framingRectInPreview = null;
        } else {
            requestedFramingRectWidth = width;
            requestedFramingRectHeight = height;
        }
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview();
        if (rect == null) {
            return null;
        }
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
    }

    public Context getContext() {
        return context;
    }

//    private static CameraManager cameraManager;
//
//    static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
//
//    static {
//        int sdkInt;
//        try {
//            sdkInt = Integer.parseInt(Build.VERSION.SDK);
//        } catch (NumberFormatException nfe) {
//            // Just to be safe
//            sdkInt = 10000;
//        }
//        SDK_INT = sdkInt;
//    }
//
//    private final Context context;
//    private final CameraConfigurationManager cameraConfigurationManager;
//    private static Camera camera;
//    private Rect framingRect;
//    private Rect framingRectInPreview;
//    private boolean initialized;
//    private boolean previewing;
//    private final boolean useOneShotPreviewCallback;
//    private final PreviewCallback previewCallback;
//    private final AutoFocusCallback autoFocusCallback;
//
//    public static void initialize(Context context) {
//        if (cameraManager == null) {
//            cameraManager = new CameraManager(context);
//        }
//    }
//
//    public Camera getCamera() {
//        return camera;
//    }
//
//    public static CameraManager get() {
//        return cameraManager;
//    }
//
//    private CameraManager(Context context) {
//        this.context = context;
//        this.cameraConfigurationManager = new CameraConfigurationManager(context);
//        useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.CUPCAKE;
//        previewCallback = new PreviewCallback(cameraConfigurationManager, useOneShotPreviewCallback);
//        autoFocusCallback = new AutoFocusCallback();
//    }
//
//    public void openDriver(SurfaceHolder holder) throws IOException {
//        if (camera == null) {
//            camera = Camera.open();
//            if (camera == null) {
//                throw new IOException();
//            }
//            camera.setPreviewDisplay(holder);
//            if (!initialized) {
//                initialized = true;
//                cameraConfigurationManager.initializeFromCameraParameters(camera);
//            }
//            cameraConfigurationManager.setDesiredCameraParameters(camera);
//        } else {
//        }
//    }
//
//    public void flash() {
//        //camera.startPreview();
//        if (camera != null) {
//            Camera.Parameters parameters = camera.getParameters();
//            if (Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
//                turnOn(parameters);
//            } else if (Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
//                turnOff(parameters);
//            }
//        }
//    }
//
//    private void turnOn(Camera.Parameters parameters) {
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//        camera.setParameters(parameters);
//    }
//
//    private void turnOff(Camera.Parameters parameters) {
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//        camera.setParameters(parameters);
//    }
//
//    public void closeDriver() {
//        if (camera != null) {
//            camera.release();
//            camera = null;
//        }
//    }
//
//    public void startPreview() {
//        if (camera != null && !previewing) {
//            camera.startPreview();
//            previewing = true;
//        }
//    }
//
//    public void stopPreview() {
//        if (camera != null && previewing) {
//            if (!useOneShotPreviewCallback) {
//                camera.setPreviewCallback(null);
//            }
//            camera.stopPreview();
//            previewCallback.setHandler(null, 0);
//            autoFocusCallback.setHandler(null, 0);
//            previewing = false;
//        }
//    }
//
//    public void requestPreviewFrame(Handler handler, int message) {
//        if (camera != null && previewing) {
//            previewCallback.setHandler(handler, message);
//            if (useOneShotPreviewCallback) {
//                camera.setOneShotPreviewCallback(previewCallback);
//            } else {
//                camera.setPreviewCallback(previewCallback);
//            }
//        }
//    }
//
//    public void requestAutoFocus(Handler handler, int message) {
//        if (camera != null && previewing) {
//            autoFocusCallback.setHandler(handler, message);
//            try {
//                camera.autoFocus(autoFocusCallback);
//            } catch (Exception e) {
//                 LogUtil.Companion.getInstance().e("camera", "autoFocus failed");
//            }
//        }
//    }
//
//    public Rect getFramingRect() {
//        Point screenResolution = cameraConfigurationManager.getScreenResolution();
//        if (framingRect == null) {
//            if (camera == null) {
//                return null;
//            }
////            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
////            int width = (int)(displayMetrics.widthPixels * 0.6);
////            int height = (int)(width * 0.9);
////            int leftOffset = (screenResolution.x - width) / 2;
////            int topOffset = (screenResolution.y - height) / 3;
////            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
//            int width = screenResolution.x * 5 / 8;
//            int height = screenResolution.x * 5 / 8;
//             LogUtil.Companion.getInstance().print("scan_frame width:" + width);
//             LogUtil.Companion.getInstance().print("scan_frame height:" + height);
//            int leftOffset = (screenResolution.x - width) / 2;
//            int topOffset = (screenResolution.y - height) / 3;
////            int topOffset = (screenResolution.y - height) / 5 * 2 - DensityUtil.getInstance(context).px2dp(100);
//            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
//             LogUtil.Companion.getInstance().print("scan_frame x:" + screenResolution.x);
//             LogUtil.Companion.getInstance().print("scan_frame y:" + screenResolution.y);
//             LogUtil.Companion.getInstance().print("scan_frame leftOffset:" + leftOffset);
//             LogUtil.Companion.getInstance().print("scan_frame topOffset:" + topOffset);
//             LogUtil.Companion.getInstance().print("scan_frame rightOffset:" + leftOffset + width);
//             LogUtil.Companion.getInstance().print("scan_frame bottomOffset:" + topOffset + height);
//        }
//        return framingRect;
//    }
//
//    private Rect getFramingRectInPreview() {
//        if (framingRectInPreview == null) {
//            Rect rect = new Rect(getFramingRect());
//            Point cameraResolution = cameraConfigurationManager.getCameraResolution();
//            Point screenResolution = cameraConfigurationManager.getScreenResolution();
////            rect.left = rect.left * cameraResolution.y / screenResolution.x;
////            rect.right = rect.right * cameraResolution.y / screenResolution.x;
////            rect.top = rect.top * cameraResolution.x / screenResolution.y;
////            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
//            rect.left = rect.left * cameraResolution.y / screenResolution.x;
//            rect.right = rect.right * cameraResolution.y / screenResolution.x;
//            rect.top = rect.top * cameraResolution.x / screenResolution.y;
//            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
//             LogUtil.Companion.getInstance().print("rect.lef" + rect.left);
//             LogUtil.Companion.getInstance().print("rect.right" + rect.right);
//             LogUtil.Companion.getInstance().print("rect.top" + rect.top);
//             LogUtil.Companion.getInstance().print("rect.bottom" + rect.bottom);
//            framingRectInPreview = rect;
//        }
//        return framingRectInPreview;
//    }
//
//    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
//        Rect rect = getFramingRectInPreview();
//        int previewFormat = cameraConfigurationManager.getPreviewFormat();
//        String previewFormatString = cameraConfigurationManager.getPreviewFormatString();
//        switch (previewFormat) {
//            case PixelFormat.YCbCr_420_SP:
//            case PixelFormat.YCbCr_422_SP:
//                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
//            default:
//                if ("yuv420p".equals(previewFormatString)) {
//                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
//                }
//        }
//        throw new IllegalArgumentException("Unsupported picture format: " + previewFormat + '/' + previewFormatString);
//    }
//
//    public Context getContext() {
//        return context;
//    }
}
