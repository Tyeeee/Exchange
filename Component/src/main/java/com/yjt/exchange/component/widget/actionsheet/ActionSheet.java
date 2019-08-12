package com.hynet.heebit.components.widget.actionsheet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hynet.heebit.components.R;
import com.hynet.heebit.components.widget.actionsheet.adapter.ActionSheetAdapterBuilder;
import com.hynet.heebit.components.widget.actionsheet.listener.OnMenuItemClickListener;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.MenuRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;


public class ActionSheet {

    public static final int MODE_LIST = 0;
    public static final int MODE_GRID = 1;

    @DrawableRes
    private int backgroundDrawable;
    @DrawableRes
    private int dividerBackground;
    @DrawableRes
    private int itemBackground;
    @StyleRes
    private int theme;
    private int backgroundColor;
    private int itemTextColor;
    private int titleTextColor;
    private boolean expandOnStart = false;
    private int iconTintColor = View.NO_ID;
    private int itemGravity = View.NO_ID;
    private int mode;
    private Menu menu;
    private ActionSheetAdapterBuilder actionSheetAdapterBuilder;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Context context;
    private OnMenuItemClickListener onMenuItemClickListener;

    public ActionSheet(Context context, CoordinatorLayout coordinatorLayout) {
        this.context = context;
        this.coordinatorLayout = coordinatorLayout;
        this.actionSheetAdapterBuilder = new ActionSheetAdapterBuilder(this.context);
    }

    public ActionSheet(Context context) {
        this(context, 0);
    }

    public ActionSheet(Context context, @StyleRes int theme) {
        this.context = context;
        this.theme = theme;
        this.actionSheetAdapterBuilder = new ActionSheetAdapterBuilder(this.context);
    }

    public ActionSheet setItemGravity(int itemGravity) {
        this.itemGravity = itemGravity;
        actionSheetAdapterBuilder.setItemGravity(itemGravity);
        return this;
    }

    public ActionSheet setMode(int mode) {
        if (mode != MODE_LIST && mode != MODE_GRID) {
            throw new IllegalArgumentException("Mode must be one of ActionSheet.MODE_LIST or ActionSheet.MODE_GRID");
        }
        this.mode = mode;
        actionSheetAdapterBuilder.setMode(mode);
        return this;
    }

    public ActionSheet setItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
        return this;
    }

    public ActionSheet setMenu(@MenuRes int menu) {
        @SuppressWarnings("ConstantConditions")
        PopupMenu popupMenu = new PopupMenu(context, null);
        this.menu = popupMenu.getMenu();
        popupMenu.getMenuInflater().inflate(menu, this.menu);
        return setMenu(this.menu);
    }

    public ActionSheet setMenu(Menu menu) {
        this.menu = menu;
        actionSheetAdapterBuilder.setMenu(this.menu);
        return this;
    }

    public ActionSheet addTitleItem(@StringRes int title) {
        return addTitleItem(context.getString(title));
    }

    public ActionSheet addTitleItem(String title) {
        if (mode == ActionSheet.MODE_GRID) {
            throw new IllegalStateException("You can't add a title with MODE_GRID. Use MODE_LIST instead");
        }
        actionSheetAdapterBuilder.addTitleItem(title, titleTextColor);
        return this;
    }

    public ActionSheet addDividerItem() {
        if (mode == ActionSheet.MODE_GRID) {
            throw new IllegalStateException("You can't add a divider with MODE_GRID. Use MODE_LIST instead");
        }
        actionSheetAdapterBuilder.addDividerItem(dividerBackground);
        return this;
    }

    public ActionSheet addItem(int id, @StringRes int title, @DrawableRes int drawableId) {
        return addItem(id, context.getString(title), ContextCompat.getDrawable(context, drawableId));
    }

    public ActionSheet addItem(int id, @StringRes int title, Drawable drawable) {
        return addItem(id, context.getString(title), drawable);
    }

    public ActionSheet addItem(int id, String title, @DrawableRes int drawableId) {
        return addItem(id, title, ContextCompat.getDrawable(context, drawableId));
    }

    public ActionSheet addItem(int id, String title, Drawable drawable) {
        actionSheetAdapterBuilder.addItem(id, title, drawable, itemTextColor, itemBackground, iconTintColor);
        return this;
    }

    public ActionSheet setItemTextColor(@ColorInt int color) {
        this.itemTextColor = color;
        return this;
    }

    public ActionSheet setTitleTextColor(@ColorInt int color) {
        this.titleTextColor = color;
        return this;
    }

    public ActionSheet setBackgroundColor(@ColorInt int color) {
        this.backgroundColor = color;
        return this;
    }

    public ActionSheet setItemTextColorResource(@ColorRes int color) {
        this.itemTextColor = ResourcesCompat.getColor(context.getResources(), color, context.getTheme());
        return this;
    }

    public ActionSheet setTitleTextColorResource(@ColorRes int color) {
        this.titleTextColor = ResourcesCompat.getColor(context.getResources(), color, context.getTheme());
        return this;
    }

    public ActionSheet setBackground(@DrawableRes int background) {
        this.backgroundDrawable = background;
        return this;
    }

    public ActionSheet setBackgroundColorResource(@ColorRes int background) {
        this.backgroundColor = ResourcesCompat.getColor(context.getResources(), background, context.getTheme());
        return this;
    }

    public ActionSheet setDividerBackground(@DrawableRes int background) {
        this.dividerBackground = background;
        return this;
    }

    public ActionSheet setItemBackground(@DrawableRes int background) {
        this.itemBackground = background;
        return this;
    }

    public ActionSheet expandOnStart(boolean expand) {
        this.expandOnStart = expand;
        return this;
    }

    public ActionSheet setAppBarLayout(AppBarLayout appBarLayout) {
        this.appBarLayout = appBarLayout;
        return this;
    }

    public ActionSheet setIconTintColorResource(@ColorRes int color) {
        this.iconTintColor = ContextCompat.getColor(context, color);
        return this;
    }

    public ActionSheet setIconTintColor(int color) {
        this.iconTintColor = color;
        return this;
    }

    public View createView() {
        if (menu == null && actionSheetAdapterBuilder.getSheetItemListeners().isEmpty()) {
            throw new IllegalStateException("You need to provide at least one Menu or an item with addItem");
        }
        if (coordinatorLayout == null) {
            throw new IllegalStateException("You need to provide a coordinatorLayout so the view can be placed on it");
        }
        View sheet = actionSheetAdapterBuilder.createView(titleTextColor, backgroundDrawable, backgroundColor, dividerBackground, itemTextColor, itemBackground, iconTintColor, onMenuItemClickListener);
        ViewCompat.setElevation(sheet, context.getResources().getDimensionPixelSize(R.dimen.dp_16));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sheet.findViewById(R.id.fakeShadow).setVisibility(View.GONE);
        }
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.setBehavior(new BottomSheetBehavior());
        if (context.getResources().getBoolean(R.bool.tablet_landscape)) {
            layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.dp_500);
        }
        coordinatorLayout.addView(sheet, layoutParams);
        coordinatorLayout.postInvalidate();
        return sheet;
    }

    public ActionSheetMenu createActionSheetMenu() {
        if (menu == null && actionSheetAdapterBuilder.getSheetItemListeners().isEmpty()) {
            throw new IllegalStateException("You need to provide at least one Menu or an item with addItem");
        }
        ActionSheetMenu actionSheetMenu = theme == 0 ? new ActionSheetMenu(context, R.style.ActionSheet_DialogStyle) : new ActionSheetMenu(context, theme);
        if (theme != 0) {
            setupThemeColors(context.obtainStyledAttributes(theme, new int[]{R.attr.as_background_color, R.attr.as_item_text_color, R.attr.as_title_text_color}));
        } else {
            setupThemeColors(context.getTheme().obtainStyledAttributes(new int[]{R.attr.as_background_color, R.attr.as_item_text_color, R.attr.as_title_text_color}));
        }
        View sheet = actionSheetAdapterBuilder.createView(titleTextColor, backgroundDrawable, backgroundColor, dividerBackground, itemTextColor, itemBackground, iconTintColor, actionSheetMenu);
        sheet.findViewById(R.id.fakeShadow).setVisibility(View.GONE);
        actionSheetMenu.setAppBarLayout(appBarLayout);
        actionSheetMenu.expandOnStart(expandOnStart);
        actionSheetMenu.setBottomSheetItemClickListener(onMenuItemClickListener);
        if (context.getResources().getBoolean(R.bool.tablet_landscape)) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.dp_500), ViewGroup.LayoutParams.WRAP_CONTENT);
            actionSheetMenu.setContentView(sheet, layoutParams);
        } else {
            actionSheetMenu.setContentView(sheet);
        }
        return actionSheetMenu;
    }

    @SuppressWarnings("ResourceType")
    private void setupThemeColors(TypedArray typedArray) {
        int backgroundRes = typedArray.getResourceId(0, backgroundColor);
        int textRes = typedArray.getResourceId(1, itemTextColor);
        int titleRes = typedArray.getResourceId(2, titleTextColor);
        if (backgroundRes != backgroundColor) {
            backgroundColor = ContextCompat.getColor(context, backgroundRes);
        }
        if (titleRes != titleTextColor) {
            titleTextColor = ContextCompat.getColor(context, titleRes);
        }
        if (textRes != itemTextColor) {
            itemTextColor = ContextCompat.getColor(context, textRes);
        }
        typedArray.recycle();
    }
}
