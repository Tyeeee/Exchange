package com.hynet.heebit.components.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BaseTarget;
import com.hynet.heebit.components.constant.Constant;

public class GlideUtil {

    private static GlideUtil glideUtil;

    private GlideUtil() {
        // cannot be instantiated
    }

    public static synchronized GlideUtil getInstance() {
        if (glideUtil == null) {
            glideUtil = new GlideUtil();
        }
        return glideUtil;
    }

    public static void releaseInstance() {
        if (glideUtil != null) {
            glideUtil = null;
        }
    }

    public void with(Context ctx, Object originalSource, DiskCacheStrategy strategy, ImageView view) {
        with(ctx, originalSource, true, View.NO_ID, View.NO_ID, View.NO_ID, false, strategy, view);
    }

    public void with(Context ctx, Object originalSource, int width, int height, DiskCacheStrategy strategy, ImageView view) {
        with(ctx, originalSource, true, width, height, View.NO_ID, View.NO_ID, View.NO_ID, false, strategy, view);
    }

    public void with(Context ctx, Object originalSource, boolean isCircleCrop, DiskCacheStrategy strategy, ImageView view) {
        with(ctx, originalSource, true, View.NO_ID, View.NO_ID, View.NO_ID, isCircleCrop, strategy, view);
    }

    public void with(Context ctx, Object originalSource, boolean isSkipMemoryCache, int width, int height, int placeHolderResourceId, int fallbackResourceId, int errorResourceId, boolean isCircleCrop, DiskCacheStrategy strategy, BaseTarget baseTarget) {
        with(ctx,
             originalSource,
             null,
             View.NO_ID,
             null,
             isSkipMemoryCache,
             Constant.View.GLIDE_BITMAP,
             false,
             false,
             null,
             false,
             width,
             height,
             placeHolderResourceId,
             null,
             fallbackResourceId,
             null,
             errorResourceId,
             null,
             Constant.View.GLIDE_CENTER_CROP,
             isCircleCrop,
             strategy,
             null,
             baseTarget);
    }

    public void with(Context ctx, Object originalSource, int width, int height, int placeHolderResourceId, int fallbackResourceId, int errorResourceId, boolean isCircleCrop, DiskCacheStrategy strategy, ImageView view) {
        with(ctx,
             originalSource,
             null,
             View.NO_ID,
             null,
             true,
             Constant.View.GLIDE_BITMAP,
             false,
             false,
             null,
             false,
             width,
             height,
             placeHolderResourceId,
             null,
             fallbackResourceId,
             null,
             errorResourceId,
             null,
             Constant.View.GLIDE_CENTER_CROP,
             isCircleCrop,
             strategy,
             view,
             null);
    }

    public void with(Context ctx, Object originalSource, boolean isSkipMemoryCache, int placeHolderResourceId, int fallbackResourceId, int errorResourceId, boolean isCircleCrop, DiskCacheStrategy strategy, ImageView view) {
        with(ctx,
             originalSource,
             isSkipMemoryCache,
             View.NO_ID,
             View.NO_ID,
             placeHolderResourceId,
             fallbackResourceId,
             errorResourceId,
             isCircleCrop,
             strategy,
             view);
    }

    public void with(Context ctx, Object originalSource, boolean isSkipMemoryCache, int width, int height, int placeHolderResourceId, int fallbackResourceId, int errorResourceId, boolean isCircleCrop, DiskCacheStrategy strategy, ImageView view) {
        with(ctx,
             originalSource,
             null,
             View.NO_ID,
             null,
             isSkipMemoryCache,
             Constant.View.GLIDE_BITMAP,
             false,
             false,
             null,
             false,
             width,
             height,
             placeHolderResourceId,
             null,
             fallbackResourceId,
             null,
             errorResourceId,
             null,
             Constant.View.GLIDE_CENTER_CROP,
             isCircleCrop,
             strategy,
             view,
             null);
    }

    public void with(Context ctx,
                     Object originalSource,
                     Object thumbnailSource,
                     int thumbnailScale,
                     Priority priority,
                     boolean isSkipMemoryCache,
                     int displayType,
                     boolean hasGifDiskCacheStrategy,
                     boolean hasTransformation,
                     Transformation<Bitmap> transformation,
                     boolean hasAnimation,
                     int width,
                     int height,
                     int placeHolderResourceId,
                     Drawable placeHolderDrawable,
                     int fallbackResourceId,
                     Drawable fallbackDrawable,
                     int errorResourceId,
                     Drawable errorDrawable,
                     int displayMode,
                     boolean isCircleCrop,
                     DiskCacheStrategy strategy,
                     ImageView view,
                     BaseTarget baseTarget) {
        if (ctx != null) {
            GlideRequest<Drawable> glideRequest = GlideApp.with(ctx).load(originalSource);
            if (thumbnailSource != null) {
                glideRequest.thumbnail(Glide.with(ctx).load(thumbnailSource));
            }
            if (thumbnailScale != View.NO_ID) {
                glideRequest.thumbnail(thumbnailScale);
            }
            switch (displayType) {
                case Constant.View.GLIDE_BITMAP:
                    Glide.with(ctx).asBitmap();
                    break;
                case Constant.View.GLIDE_GIF:
                    if (hasGifDiskCacheStrategy) {
                        glideRequest.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                        GlideApp.with(ctx).asGif();
                    } else {
                        glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE);
                        GlideApp.with(ctx).asGif();
                    }
                    break;
                default:
                    break;
            }
            if (priority != null) {
                glideRequest.priority(priority);
            }
            glideRequest.skipMemoryCache(isSkipMemoryCache);
            if (!isSkipMemoryCache) {
                if (width != View.NO_ID && height != View.NO_ID) {
                    glideRequest.preload(width, height);
                } else {
                    glideRequest.preload();
                }
            }
            if (hasTransformation && transformation != null) {
                glideRequest.transform(transformation);
            } else {
                glideRequest.dontTransform();
            }
            if (hasAnimation) {
                //todo
            } else {
                glideRequest.dontAnimate();
            }
            if (width != View.NO_ID && height != View.NO_ID) {
                glideRequest.override(width, height);
            }
            if (placeHolderDrawable != null) {
                glideRequest.placeholder(placeHolderDrawable);
            }
            if (placeHolderResourceId != View.NO_ID) {
                glideRequest.placeholder(placeHolderResourceId);
            }
            if (fallbackResourceId != View.NO_ID) {
                glideRequest.error(fallbackResourceId);
            }
            if (fallbackDrawable != null) {
                glideRequest.error(fallbackDrawable);
            }
            if (errorDrawable != null) {
                glideRequest.error(errorDrawable);
            }
            if (errorResourceId != View.NO_ID) {
                glideRequest.error(errorResourceId);
            }
            if (strategy != null) {
                glideRequest.diskCacheStrategy(strategy);
            }
            if (!hasTransformation) {
                switch (displayMode) {
                    case Constant.View.GLIDE_CENTER_CROP:
                        glideRequest.centerCrop();
                        break;
                    case Constant.View.GLIDE_CENTER_INSIDE:
                        glideRequest.centerInside();
                        break;
                    case Constant.View.GLIDE_FIT_CENTER:
                        glideRequest.fitCenter();
                        break;
                    default:
                        break;
                }
            }
            if (isCircleCrop) {
                glideRequest.circleCrop();
            }
            if (view != null) {
                glideRequest.into(view);
            }
            if (baseTarget != null) {
                glideRequest.into(baseTarget);
            }
        }
    }
}
