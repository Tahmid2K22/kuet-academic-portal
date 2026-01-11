package com.example.kuet_academic_portal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kuet_academic_portal.R;
import com.example.kuet_academic_portal.model.Contact;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private final List<Contact> contactList;
    private final boolean isAdmin;
    private final OnContactClickListener listener;

    public interface OnContactClickListener {
        void onDeleteClick(Contact contact);
    }

    public ContactListAdapter(List<Contact> contactList, boolean isAdmin, OnContactClickListener listener) {
        this.contactList = contactList;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvDesignation.setText(contact.getDesignation());
        holder.tvDepartment.setText(contact.getDepartment());
        holder.tvEmail.setText(contact.getEmail());
        holder.tvPhone.setText(contact.getPhone());

        if (isAdmin) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(contact);
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation, tvDepartment, tvEmail, tvPhone;
        ImageView btnDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
