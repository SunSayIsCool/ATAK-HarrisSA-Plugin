package com.atakmap.android.harrissaspr;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atakmap.android.harrissaspr.plugin.R;

public class IconsAdapter extends BaseAdapter {

    Context context;
    int icons[];
    String units[];
    LayoutInflater inflter;

    public IconsAdapter(Context applicationContext, int[] icons, String[] units) {
        this.context = applicationContext;
        this.icons = icons;
        this.units = units;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView text = (TextView) view.findViewById(R.id.tv);
        icon.setImageResource(icons[position]);
        text.setText("");
        text.setTextSize(0);
        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.spinner_item, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView text = (TextView) view.findViewById(R.id.tv);
        icon.setImageResource(icons[position]);
        text.setText(units[position]);
        text.setTextSize(16);
        return view;
    }
}
