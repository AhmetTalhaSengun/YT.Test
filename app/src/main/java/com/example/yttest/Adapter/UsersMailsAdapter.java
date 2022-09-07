package com.example.yttest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.yttest.Model.UsersData;
import com.example.yttest.R;

import java.util.ArrayList;

public class UsersMailsAdapter extends RecyclerView.Adapter<UsersMailsAdapter.UsersDataHolder> {

    private ArrayList<UsersData> UsersDataArrayList;
    LayoutInflater inflater;
    private UsersMailsAdapterListener adapterListener;
    public UsersMailsAdapter(ArrayList<UsersData> usersDataArrayList) {
        UsersDataArrayList = usersDataArrayList;
    }

    public UsersMailsAdapter(Context context, ArrayList<UsersData> usersDataArrayList, UsersMailsAdapterListener adapterListener){
        inflater = LayoutInflater.from(context);
        this.UsersDataArrayList =usersDataArrayList;
        this.adapterListener = adapterListener;
    }

    @Override
    public UsersDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_row, parent, false);
        UsersDataHolder holder = new UsersDataHolder(view, adapterListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(UsersDataHolder holder, int position) {
        UsersData selectedProduct = UsersDataArrayList.get(position);
        holder.setData(selectedProduct, position);

    }

    @Override
    public int getItemCount() {
        return UsersDataArrayList.size();
    }

    class UsersDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, email;
        UsersMailsAdapterListener aListener;
        public UsersDataHolder(View itemView, UsersMailsAdapterListener listener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.recyclerViewUserNameText);
            email = (TextView) itemView.findViewById(R.id.recyclerViewUseEmailText);
            aListener = listener;

            itemView.setOnClickListener(this);
        }

        public void setData(UsersData selectedData, int position) {

            this.name.setText(selectedData.name);
            this.email.setText(selectedData.email);
        }


        @Override
        public void onClick(View v) {
            aListener.onClick(getAdapterPosition(),v);
        }


    }
}
