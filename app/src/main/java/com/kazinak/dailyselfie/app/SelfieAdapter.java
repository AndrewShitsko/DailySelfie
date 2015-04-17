package com.kazinak.dailyselfie.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class SelfieAdapter extends RecyclerView.Adapter<SelfieAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ImageUtils imageUtils;
    private OnItemClickListener mItemClickListener;
    private List<Selfie> data = Collections.emptyList();
    private Context context;

    public SelfieAdapter(Context context, List<Selfie> data) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        imageUtils = new ImageUtils();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.selfie_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Selfie current = data.get(i);
        viewHolder.title.setText(current.getTitle());
        viewHolder.photo.setImageBitmap(current.getPhoto());
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateList(List<Selfie> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        Selfie currentSelfie = this.data.get(position);
        imageUtils.removeImageFile(currentSelfie.getPhotoPath());
        this.data.remove(position);
        notifyItemRemoved(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public ImageView photo;
        public ImageView deletePhoto;
        public View view;

        public ViewHolder(final View itemView) {
            super(itemView);

            view = ((ViewGroup)itemView).getChildAt(0);

            view.setOnClickListener(this);

            title = (TextView) view.findViewById(R.id.title);
            photo = (ImageView) view.findViewById(R.id.photo);
            deletePhoto = (ImageView) view.findViewById(R.id.deletePhoto);
            deletePhoto.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                            .setMessage("Вы действительно хотите удалить это селфи?")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeItem(getPosition());
                                }
                            })
                            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                            break;
                        default:
                            break;
                    }

                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }
}
