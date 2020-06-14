package com.lavish.indiscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddPicAdapter extends RecyclerView.Adapter<AddPicAdapter.AddPicViewHolder>{
    private int flag=1;


    public interface OnItemClickListener{

        void OnItemClick(int position);
    }

    public interface OnItemLongClickListener{
        boolean OnItemLongClick(View view,int position);
    }

    private ArrayList<ModelAddPic> mlist;
    private Context mContext;
    private int lastPosition = -1;

    private OnItemClickListener mListener;
    public void setOnItmeClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }

    private OnItemLongClickListener mLongListener;
    public void setOnItemLongClickListener(OnItemLongClickListener longListener) {this.mLongListener = longListener; }





    public static class AddPicViewHolder extends RecyclerView.ViewHolder{

        public TextView project_id;
        public TextView pic_id;
        public ImageView item_pic;


        public AddPicViewHolder(@NonNull final View itemView, final OnItemClickListener listener,
                                final OnItemLongClickListener longlistener, Context context)
        {
            super(itemView);

           project_id=itemView.findViewById(R.id.prject_id);
           pic_id=itemView.findViewById(R.id.pic_id);
           item_pic=itemView.findViewById(R.id.item_pic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.OnItemClick(position);
                        }
                    }
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(longlistener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            longlistener.OnItemLongClick(v,position);

                        }
                    }

                    return true;
                }
            });

        }
    }

    @NonNull
    @Override
    public AddPicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.addpicture_item,parent,false);
        AddPicViewHolder searchDonorsViewHolder=new AddPicViewHolder(v,mListener,mLongListener,mContext);
        return searchDonorsViewHolder;
    }

    public AddPicAdapter(ArrayList<ModelAddPic> list, Context context) {

        mlist=list;
        mContext=context;
    }

    @Override
    public void onBindViewHolder(@NonNull final AddPicViewHolder holder, int position) {
        ModelAddPic currentItem=mlist.get(position);

        holder.project_id.setText(currentItem.getProjectId());
        holder.pic_id.setText(currentItem.getPicId());
        holder.item_pic.setImageBitmap(currentItem.getBitmap());
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.pic_item_anim);
            animation.setDuration(animation.getDuration()+position*100);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}

