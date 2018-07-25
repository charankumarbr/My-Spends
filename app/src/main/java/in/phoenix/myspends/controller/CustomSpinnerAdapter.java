package in.phoenix.myspends.controller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import in.phoenix.myspends.R;
import in.phoenix.myspends.model.Category;
import in.phoenix.myspends.model.PaymentType;

/**
 * Created by Charan.Br on 4/2/2018.
 */

public final class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final List<?> items;
    private final int mResource;

    private String mSelectionText = null;

    public CustomSpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
                                @NonNull List objects) {
        super(context, resource, 0, objects);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }

    @Override
    public int getCount() {
        return (null == items ? 0 : items.size() + 1);
    }

    @Override
    public String getItem(int position) {
        return position == 0 ? null : items.get(position - 1).toString();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position >= 1 ? (position - 1) : position - 1;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, Boolean.TRUE);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent, Boolean.FALSE);
    }

    private View createItemView(int position, View convertView, ViewGroup parent, Boolean isDropdown) {

        if (position == 0) {
            View view;
            if (isDropdown) {
                view = mInflater.inflate(R.layout.layout_spinner_dropdown, parent, false);
                TextView tvInfo = view.findViewById(android.R.id.text1);
                tvInfo.setText(mSelectionText);

            } else {
                view = mInflater.inflate(mResource, parent, false);
                TextView tvInfo = view.findViewById(R.id.lss_tv_name);
                //view.findViewById(R.id.lpt_switch_active).setVisibility(View.GONE);
                tvInfo.setText(mSelectionText);
            }

            if (items.get(0) instanceof PaymentType) {
                view.setTag(null);

            } else if (items.get(0) instanceof Category) {
                view.setTag(-1);
            }

            return view;
        }

        position--;
        final View view = mInflater.inflate(mResource, parent, false);

        TextView offTypeTv = view.findViewById(R.id.lss_tv_name);
        //view.findViewById(R.id.lpt_switch_active).setVisibility(View.GONE);

        //if (position > 0) {
        Object type = items.get(position);
        if (type instanceof PaymentType) {
            PaymentType offerData = (PaymentType) type;
            offTypeTv.setText(offerData.getName());
            view.setTag(offerData.getKey());

        } else if (type instanceof Category) {
            Category category = (Category) type;
            offTypeTv.setText(category.getName());
            view.setTag(category.getId());
        }


        /*} else {
            view.setTag(null);
        }*/

        return view;
    }

    public void setSelectionText(String selectionText) {
        mSelectionText = selectionText;
    }
}
