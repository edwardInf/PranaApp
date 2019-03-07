package pranaproject.pranaapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import pranaproject.pranaapp.R;

public class CoverAdapter extends BaseAdapter {

    Context mCtx;
    int[] mResources;

    public CoverAdapter(Context ctx, int[] resources){
        mCtx = ctx;
        mResources = resources;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.layout_cover_item,null);
        }

        ImageView imgCover = (ImageView) convertView.findViewById(R.id.imgCover);
        imgCover.setImageResource(mResources[position]);

        return convertView;
    }
}
