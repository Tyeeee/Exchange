package com.hynet.heebit.components.widget.actionsheet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.hynet.heebit.components.R;
import com.hynet.heebit.components.widget.actionsheet.ActionSheet;
import com.hynet.heebit.components.widget.actionsheet.ActionSheetDivider;
import com.hynet.heebit.components.widget.actionsheet.ActionSheetHeader;
import com.hynet.heebit.components.widget.actionsheet.ActionSheetMenuItem;
import com.hynet.heebit.components.widget.actionsheet.listener.OnMenuItemClickListener;
import com.hynet.heebit.components.widget.actionsheet.listener.OnSheetItemListener;

import java.util.List;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActionSheetAdapterBuilder {

    private List<OnSheetItemListener> onSheetItemListeners;
    private int titles;
    private int mode;
    private Menu menu;
    private boolean fromMenu;
    private Context context;
    private int itemGravity = View.NO_ID;


    public ActionSheetAdapterBuilder(Context context) {
        this.context = context;
        this.onSheetItemListeners = Lists.newArrayList();
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        fromMenu = true;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setItemGravity(int itemGravity) {
        this.itemGravity = itemGravity;
    }

    public void addTitleItem(String title, int titleTextColor) {
        onSheetItemListeners.add(new ActionSheetHeader(title, titleTextColor));
    }

    public void addDividerItem(int dividerBackground) {
        onSheetItemListeners.add(new ActionSheetDivider(dividerBackground));
    }

    @SuppressLint("RestrictedApi")
    public void addItem(int id, String title, Drawable icon, int itemTextColor, int itemBackground, int tintColor) {
        if (menu == null) {
            menu = new MenuBuilder(context);
        }
        MenuItem menuItem = menu.add(Menu.NONE, id, Menu.NONE, title);
        menuItem.setIcon(icon);
        onSheetItemListeners.add(new ActionSheetMenuItem(menuItem, itemTextColor, itemBackground, tintColor));
    }

    @SuppressLint("InflateParams")
    public View createView(int titleTextColor, int backgroundDrawable, int backgroundColor, int dividerBackground, int itemTextColor, int itemBackground, int tintColor, OnMenuItemClickListener onMenuItemClickListener) {
        if (fromMenu) {
            onSheetItemListeners = createAdapterItems(dividerBackground, titleTextColor, itemTextColor, itemBackground, tintColor);
        }
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View sheet = mode == ActionSheet.MODE_GRID ? layoutInflater.inflate(R.layout.view_sheet_grid, null) : layoutInflater.inflate(R.layout.view_sheet_list, null);
        final RecyclerView recyclerView = sheet.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        if (backgroundDrawable != 0) {
            sheet.setBackgroundResource(backgroundDrawable);
        } else {
            if (backgroundColor != 0) {
                sheet.setBackgroundColor(backgroundColor);
            }
        }
        if (titles == 1 && mode == ActionSheet.MODE_LIST) {
            OnSheetItemListener onSheetItemListener = onSheetItemListeners.get(0);
            TextView headerTextView = sheet.findViewById(R.id.textView);
            if (onSheetItemListener instanceof ActionSheetHeader) {
                headerTextView.setVisibility(View.VISIBLE);
                headerTextView.setText(onSheetItemListener.getTitle());
                if (titleTextColor != 0) {
                    headerTextView.setTextColor(titleTextColor);
                }
                onSheetItemListeners.remove(0);
            }
        }
        final ActionSheetAdapter actionSheetAdapter = new ActionSheetAdapter(onSheetItemListeners, mode, onMenuItemClickListener);
        if (mode == ActionSheet.MODE_LIST) {
            actionSheetAdapter.setItemGravity(itemGravity);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(actionSheetAdapter);
        } else {
            @SuppressLint("ResourceType") final int columns = context.getResources().getInteger(4);
            actionSheetAdapter.setItemGravity(itemGravity);
            recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    float margin = context.getResources().getDimensionPixelSize(R.dimen.dp_24);
                    actionSheetAdapter.setItemWidth((int) ((recyclerView.getWidth() - 2 * margin) / columns));
                    recyclerView.setAdapter(actionSheetAdapter);
                }
            });
        }
        return sheet;
    }

    public List<OnSheetItemListener> getSheetItemListeners() {
        return onSheetItemListeners;
    }

    private List<OnSheetItemListener> createAdapterItems(int dividerBackground, int titleTextColor, int itemTextColor, int itemBackground, int tintColor) {
        List<OnSheetItemListener> onSheetItemListeners = Lists.newArrayList();
        titles = 0;
        boolean addedSubMenu = false;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isVisible()) {
                if (menuItem.hasSubMenu()) {
                    SubMenu subMenu = menuItem.getSubMenu();
                    if (i != 0 && addedSubMenu) {
                        if (mode == ActionSheet.MODE_GRID) {
                            throw new IllegalArgumentException("MODE_GRID can't have submenus. Use MODE_LIST instead");
                        }
                        onSheetItemListeners.add(new ActionSheetDivider(dividerBackground));
                    }
                    CharSequence title = menuItem.getTitle();
                    if (title != null && !title.equals("")) {
                        onSheetItemListeners.add(new ActionSheetHeader(title.toString(), titleTextColor));
                        titles++;
                    }
                    for (int j = 0; j < subMenu.size(); j++) {
                        MenuItem subItem = subMenu.getItem(j);
                        if (subItem.isVisible()) {
                            onSheetItemListeners.add(new ActionSheetMenuItem(subItem, itemTextColor, itemBackground, tintColor));
                            addedSubMenu = true;
                        }
                    }
                } else {
                    onSheetItemListeners.add(new ActionSheetMenuItem(menuItem, itemTextColor, itemBackground, tintColor));
                }
            }
        }
        return onSheetItemListeners;
    }
}
