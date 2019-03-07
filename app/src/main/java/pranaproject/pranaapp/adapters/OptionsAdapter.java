package pranaproject.pranaapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pranaproject.pranaapp.models.Option;
import pranaproject.pranaapp.R;

import java.util.List;

/**
 * Created by angel on 7/05/17.
 */

public class OptionsAdapter extends BaseAdapter {

    Context mCtx;
    List<Option> mOptions;
    int mPosition = -1;
    public OptionsAdapter(Context ctx, List<Option> options){
        mCtx = ctx;
        mOptions = options;
    }


    public void setData(List<Option> options){
        mOptions = options;
    }

    public void selectItem(int position){
        mPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_menu,null);
        }

        Option option = mOptions.get(position);

        LinearLayout llIndicator = (LinearLayout) convertView.findViewById(R.id.llIndicator);

        if(mPosition>=0){
            if(position == mPosition){
                convertView.setBackgroundColor(mCtx.getResources().getColor(R.color.colorAccentBlack));
                llIndicator.setVisibility(View.VISIBLE);
            }else{
                convertView.setBackgroundColor(mCtx.getResources().getColor(R.color.colorAccentBlack));
                llIndicator.setVisibility(View.INVISIBLE);
            }
        }else {
            convertView.setBackgroundColor(mCtx.getResources().getColor(R.color.colorAccentBlack));
            llIndicator.setVisibility(View.INVISIBLE);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        TextView txtOption = (TextView) convertView.findViewById(R.id.txtOption);

        txtOption.setText(option.getOption());
        imgIcon.setImageResource(option.getmIconResource());

        return convertView;
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
