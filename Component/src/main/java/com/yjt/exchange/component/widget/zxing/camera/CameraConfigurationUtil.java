package com.hynet.heebit.components.widget.zxing.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;

import com.hynet.heebit.components.constant.Regex;
import com.hynet.heebit.components.utils.LogUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class CameraConfigurationUtil {

    private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
    private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
    private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final int MIN_FPS = 10;
    private static final int MAX_FPS = 20;
    private static final int AREA_PER_1000 = 400;

    private CameraConfigurationUtil() {
    }

    public static void setFocus(Camera.Parameters parameters, boolean autoFocus, boolean disableContinuous, boolean safeMode) {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        String focusMode = null;
        if (autoFocus) {
            if (safeMode || disableContinuous) {
                focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_AUTO);
            } else {
                focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
        // Maybe selected auto-focus but not available, so fall through here:
        if (!safeMode && focusMode == null) {
            focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_MACRO, Camera.Parameters.FOCUS_MODE_EDOF);
        }
        if (focusMode != null) {
            if (focusMode.equals(parameters.getFocusMode())) {
                 LogUtil.Companion.getInstance().print("Focus mode already set to " + focusMode);
            } else {
                parameters.setFocusMode(focusMode);
            }
        }
    }

    public static void setTorch(Camera.Parameters parameters, boolean on) {
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        String flashMode;
        if (on) {
            flashMode = findSettableValue("flash mode", supportedFlashModes, Camera.Parameters.FLASH_MODE_TORCH, Camera.Parameters.FLASH_MODE_ON);
        } else {
            flashMode = findSettableValue("flash mode", supportedFlashModes, Camera.Parameters.FLASH_MODE_OFF);
        }
        if (flashMode != null) {
            if (flashMode.equals(parameters.getFlashMode())) {
                 LogUtil.Companion.getInstance().print("Flash mode already set to " + flashMode);
            } else {
                 LogUtil.Companion.getInstance().print("Setting flash mode to " + flashMode);
                parameters.setFlashMode(flashMode);
            }
        }
    }

    public static void setBestExposure(Camera.Parameters parameters, boolean lightOn) {
        int minExposure = parameters.getMinExposureCompensation();
        int maxExposure = parameters.getMaxExposureCompensation();
        float step = parameters.getExposureCompensationStep();
        if ((minExposure != 0 || maxExposure != 0) && step > 0.0f) {
            // Set low when light is on
            float targetCompensation = lightOn ? MIN_EXPOSURE_COMPENSATION : MAX_EXPOSURE_COMPENSATION;
            int compensationSteps = Math.round(targetCompensation / step);
            float actualCompensation = step * compensationSteps;
            // Clamp value:
            compensationSteps = Math.max(Math.min(compensationSteps, maxExposure), minExposure);
            if (parameters.getExposureCompensation() == compensationSteps) {
                 LogUtil.Companion.getInstance().print("Exposure compensation already set to " + compensationSteps + " / " + actualCompensation);
            } else {
                 LogUtil.Companion.getInstance().print("Setting exposure compensation to " + compensationSteps + " / " + actualCompensation);
                parameters.setExposureCompensation(compensationSteps);
            }
        } else {
             LogUtil.Companion.getInstance().print("Camera does not support exposure compensation");
        }
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters) {
        setBestPreviewFPS(parameters, MIN_FPS, MAX_FPS);
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters, int minFPS, int maxFPS) {
        List<int[]> supportedPreviewFpsRanges = parameters.getSupportedPreviewFpsRange();
         LogUtil.Companion.getInstance().print("Supported FPS ranges: " + toString(supportedPreviewFpsRanges));
        if (supportedPreviewFpsRanges != null && !supportedPreviewFpsRanges.isEmpty()) {
            int[] suitableFPSRange = null;
            for (int[] fpsRange : supportedPreviewFpsRanges) {
                int thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
                int thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
                if (thisMin >= minFPS * 1000 && thisMax <= maxFPS * 1000) {
                    suitableFPSRange = fpsRange;
                    break;
                }
            }
            if (suitableFPSRange == null) {
                 LogUtil.Companion.getInstance().print("No suitable FPS range?");
            } else {
                int[] currentFpsRange = new int[2];
                parameters.getPreviewFpsRange(currentFpsRange);
                if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
                     LogUtil.Companion.getInstance().print("FPS range already set to " + Arrays.toString(suitableFPSRange));
                } else {
                     LogUtil.Companion.getInstance().print("Setting FPS range to " + Arrays.toString(suitableFPSRange));
                    parameters.setPreviewFpsRange(suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
                }
            }
        }
    }

    public static void setFocusArea(Camera.Parameters parameters) {
        if (parameters.getMaxNumFocusAreas() > 0) {
             LogUtil.Companion.getInstance().print("Old focus areas: " + toString(parameters.getFocusAreas()));
            List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
             LogUtil.Companion.getInstance().print("Setting focus area to : " + toString(middleArea));
            parameters.setFocusAreas(middleArea);
        } else {
             LogUtil.Companion.getInstance().print("Device does not support focus areas");
        }
    }

    public static void setMetering(Camera.Parameters parameters) {
        if (parameters.getMaxNumMeteringAreas() > 0) {
             LogUtil.Companion.getInstance().print("Old metering areas: " + parameters.getMeteringAreas());
            List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
             LogUtil.Companion.getInstance().print("Setting metering area to : " + toString(middleArea));
            parameters.setMeteringAreas(middleArea);
        } else {
             LogUtil.Companion.getInstance().print("Device does not support metering areas");
        }
    }

    private static List<Camera.Area> buildMiddleArea(int areaPer1000) {
        return Collections.singletonList(new Camera.Area(new Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1));
    }

    public static void setVideoStabilization(Camera.Parameters parameters) {
        if (parameters.isVideoStabilizationSupported()) {
            if (parameters.getVideoStabilization()) {
                 LogUtil.Companion.getInstance().print("Video stabilization already enabled");
            } else {
                 LogUtil.Companion.getInstance().print("Enabling video stabilization...");
                parameters.setVideoStabilization(true);
            }
        } else {
             LogUtil.Companion.getInstance().print("This device does not support video stabilization");
        }
    }

    public static void setBarcodeSceneMode(Camera.Parameters parameters) {
        if (Camera.Parameters.SCENE_MODE_BARCODE.equals(parameters.getSceneMode())) {
             LogUtil.Companion.getInstance().print("Barcode scene mode already set");
            return;
        }
        String sceneMode = findSettableValue("scene mode", parameters.getSupportedSceneModes(), Camera.Parameters.SCENE_MODE_BARCODE);
        if (sceneMode != null) {
            parameters.setSceneMode(sceneMode);
        }
    }

    public static void setZoom(Camera.Parameters parameters, double targetZoomRatio) {
        if (parameters.isZoomSupported()) {
            Integer zoom = indexOfClosestZoom(parameters, targetZoomRatio);
            if (zoom == null) {
                return;
            }
            if (parameters.getZoom() == zoom) {
                 LogUtil.Companion.getInstance().print("Zoom is already set to " + zoom);
            } else {
                 LogUtil.Companion.getInstance().print("Setting zoom to " + zoom);
                parameters.setZoom(zoom);
            }
        } else {
             LogUtil.Companion.getInstance().print("Zoom is not supported");
        }
    }

    private static Integer indexOfClosestZoom(Camera.Parameters parameters, double targetZoomRatio) {
        List<Integer> ratios = parameters.getZoomRatios();
         LogUtil.Companion.getInstance().print("Zoom ratios: " + ratios);
        int maxZoom = parameters.getMaxZoom();
        if (ratios == null || ratios.isEmpty() || ratios.size() != maxZoom + 1) {
             LogUtil.Companion.getInstance().print("Invalid zoom ratios!");
            return null;
        }
        double target100 = 100.0 * targetZoomRatio;
        double smallestDiff = Double.POSITIVE_INFINITY;
        int closestIndex = 0;
        for (int i = 0; i < ratios.size(); i++) {
            double diff = Math.abs(ratios.get(i) - target100);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                closestIndex = i;
            }
        }
         LogUtil.Companion.getInstance().print("Chose zoom ratio of " + (ratios.get(closestIndex) / 100.0));
        return closestIndex;
    }

    public static void setInvertColor(Camera.Parameters parameters) {
        if (Camera.Parameters.EFFECT_NEGATIVE.equals(parameters.getColorEffect())) {
             LogUtil.Companion.getInstance().print("Negative effect already set");
            return;
        }
        String colorMode = findSettableValue("color effect", parameters.getSupportedColorEffects(), Camera.Parameters.EFFECT_NEGATIVE);
        if (colorMode != null) {
            parameters.setColorEffect(colorMode);
        }
    }

    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
             LogUtil.Companion.getInstance().print("Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        StringBuilder previewSizesString = new StringBuilder();
        for (Camera.Size size : rawSupportedSizes) {
            previewSizesString.append(size.width).append('x').append(size.height).append(Regex.SPACE.getRegext());
        }
         LogUtil.Companion.getInstance().print("Supported preview sizes: " + previewSizesString);
        double screenAspectRatio = screenResolution.y / (double) screenResolution.x;
//        double screenAspectRatio = screenResolution.x / (double) screenResolution.y;
        // Find a suitable size, with max resolution
        int maxResolution = 0;
        Camera.Size maxResPreviewSize = null;
        for (Camera.Size size : rawSupportedSizes) {
            int realWidth = size.width;
            int realHeight = size.height;
            int resolution = realWidth * realHeight;
            if (resolution < MIN_PREVIEW_PIXELS) {
                continue;
            }
            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                continue;
            }
            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                Point exactPoint = new Point(realWidth, realHeight);
                 LogUtil.Companion.getInstance().print("Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }
            // Resolution is suitable; record the one with max resolution
            if (resolution > maxResolution) {
                maxResolution = resolution;
                maxResPreviewSize = size;
            }
        }
        // If no exact match, use largest preview size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (maxResPreviewSize != null) {
            Point largestSize = new Point(maxResPreviewSize.width, maxResPreviewSize.height);
             LogUtil.Companion.getInstance().print("Using largest suitable preview size: " + largestSize);
            return largestSize;
        }
        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
         LogUtil.Companion.getInstance().print("No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
    }

    private static String findSettableValue(String name, Collection<String> supportedValues, String... desiredValues) {
         LogUtil.Companion.getInstance().print("Requesting " + name + " value from among: " + Arrays.toString(desiredValues));
         LogUtil.Companion.getInstance().print("Supported " + name + " values: " + supportedValues);
        if (supportedValues != null) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                     LogUtil.Companion.getInstance().print("Can set " + name + " to: " + desiredValue);
                    return desiredValue;
                }
            }
        }
         LogUtil.Companion.getInstance().print("No supported values match");
        return null;
    }

    private static String toString(Collection<int[]> arrays) {
        if (arrays == null || arrays.isEmpty()) {
            return "[]";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        Iterator<int[]> iterator = arrays.iterator();
        while (iterator.hasNext()) {
            buffer.append(Arrays.toString(iterator.next()));
            if (iterator.hasNext()) {
                buffer.append(Regex.COMMA.getRegext());
            }
        }
        buffer.append(']');
        return buffer.toString();
    }

    private static String toString(Iterable<Camera.Area> areas) {
        if (areas == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Camera.Area area : areas) {
            result.append(area.rect).append(Regex.COLON.getRegext()).append(area.weight).append(Regex.SPACE.getRegext());
        }
        return result.toString();
    }

    public static String collectStats(Camera.Parameters parameters) {
        return collectStats(parameters.flatten());
    }

    public static String collectStats(CharSequence flattenedParams) {
        StringBuilder stringBuilder = new StringBuilder(1000);
        stringBuilder.append("BOARD=").append(Build.BOARD).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("BRAND=").append(Build.BRAND).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("CPU_ABI=").append(Build.CPU_ABI).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("DEVICE=").append(Build.DEVICE).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("DISPLAY=").append(Build.DISPLAY).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("FINGERPRINT=").append(Build.FINGERPRINT).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("HOST=").append(Build.HOST).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("ID=").append(Build.ID).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("MANUFACTURER=").append(Build.MANUFACTURER).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("MODEL=").append(Build.MODEL).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("PRODUCT=").append(Build.PRODUCT).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("TAGS=").append(Build.TAGS).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("TIME=").append(Build.TIME).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("TYPE=").append(Build.TYPE).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("USER=").append(Build.USER).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("VERSION.CODENAME=").append(Build.VERSION.CODENAME).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("VERSION.INCREMENTAL=").append(Build.VERSION.INCREMENTAL).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("VERSION.RELEASE=").append(Build.VERSION.RELEASE).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        stringBuilder.append("VERSION.SDK_INT=").append(Build.VERSION.SDK_INT).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
        if (flattenedParams != null) {
            String[] params = Pattern.compile(Regex.SEMICOLON.getRegext()).split(flattenedParams);
            Arrays.sort(params);
            for (String param : params) {
                stringBuilder.append(param).append(System.getProperties().getProperty(Regex.LINE_SEPARATOR.getRegext()));
            }
        }

        return stringBuilder.toString();
    }
}
