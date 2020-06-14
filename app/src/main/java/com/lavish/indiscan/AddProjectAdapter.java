package com.lavish.indiscan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddProjectAdapter extends RecyclerView.Adapter<AddProjectAdapter.AddProjectViewHolder> {

    public interface OnItemClickListener{

        void OnItemClick(int position);
    }

    private ArrayList<ModelAddProject> mlist;
    private Context mContext;
    private int lastPosition = -1;


    private OnItemClickListener mListener;
    public void setOnItmeClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }





    public static class AddProjectViewHolder extends RecyclerView.ViewHolder{

        public TextView project_id;
        public TextView project_name;
        public TextView project_date;


        public AddProjectViewHolder(@NonNull View itemView, final OnItemClickListener listener, Context context) {
            super(itemView);

            project_id=itemView.findViewById(R.id.item_project_id);
            project_date=itemView.findViewById(R.id.item_project_date);
            project_name=itemView.findViewById(R.id.item_project_name);

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

        }
    }

    @NonNull
    @Override
    public AddProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.addproject_item,parent,false);
        AddProjectViewHolder searchDonorsViewHolder=new AddProjectViewHolder(v,mListener,mContext);
        return searchDonorsViewHolder;
    }

    public AddProjectAdapter(ArrayList<ModelAddProject> list, Context context) {

        mlist=list;
        mContext=context;
    }

    @Override
    public void onBindViewHolder(@NonNull final AddProjectViewHolder holder, int position) {
        ModelAddProject currentItem=mlist.get(position);

        holder.project_id.setText(currentItem.getProject_id());
        holder.project_name.setText(currentItem.getProject_name());
        holder.project_date.setText(currentItem.getProject_date());
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
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.project_item_anim);
            animation.setDuration(animation.getDuration()+position*100);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}


