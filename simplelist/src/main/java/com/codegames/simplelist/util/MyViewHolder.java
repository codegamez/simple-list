package com.codegames.simplelist.util;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    protected View itemView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = super.itemView;
    }

}
