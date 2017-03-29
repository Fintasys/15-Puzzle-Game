package de.fintasys.the_15_puzzle_game.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.fintasys.the_15_puzzle_game.R;


public class MediaGalleryAdapter extends RecyclerView.Adapter<MediaGalleryAdapter.ViewHolder>  {

    private Context mContext;
    private View.OnClickListener mListener;
    private List<String> mValues;
    private int mWidth;

    public MediaGalleryAdapter(Context context, List<String> items, View.OnClickListener listener, int width) {
        mContext = context;
        mValues = items;
        mListener = listener;
        mWidth = width;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_media_gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mView.setTag(position);
        Picasso
                .with(mContext)
                .load(mValues.get(position))
                .centerCrop()
                .resize(mWidth, mWidth)
                .noPlaceholder()
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.image);
            mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mWidth));

            mView.setOnClickListener(mListener);
        }
    }
}

