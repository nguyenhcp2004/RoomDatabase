package com.example.roomdatabase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomdatabase.R;
import com.example.roomdatabase.entity.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    
    private List<Customer> customers;
    private OnCustomerClickListener listener;
    
    public interface OnCustomerClickListener {
        void onEditClick(Customer customer);
        void onDeleteClick(Customer customer);
    }
    
    public CustomerAdapter(List<Customer> customers) {
        this.customers = customers != null ? customers : new ArrayList<>();
    }
    
    public void setOnCustomerClickListener(OnCustomerClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new CustomerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        if (customer == null) return;
        holder.bind(customer);
    }
    
    @Override
    public int getItemCount() {
        if (customers == null) return 0;
        return customers.size();
    }
    
    public void updateCustomers(List<Customer> newCustomers) {
        this.customers = newCustomers;
        notifyDataSetChanged();
    }
    
    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName, tvCustomerEmail, tvCustomerPhone, tvCustomerAddress, tvCustomerType;
        private Button btnEditCustomer, btnDeleteCustomer;
        
        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerEmail = itemView.findViewById(R.id.tvCustomerEmail);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvCustomerType = itemView.findViewById(R.id.tvCustomerType);
            btnEditCustomer = itemView.findViewById(R.id.btnEditCustomer);
            btnDeleteCustomer = itemView.findViewById(R.id.btnDeleteCustomer);
        }
        
        public void bind(Customer customer) {
            tvCustomerName.setText(customer.getName());
            tvCustomerEmail.setText("📧 " + customer.getEmail());
            tvCustomerPhone.setText("📞 " + customer.getPhone());
            tvCustomerAddress.setText("📍 " + customer.getAddress());
            
            // Set customer type badge with appropriate color
            tvCustomerType.setText(customer.getCustomerType());
            switch (customer.getCustomerType()) {
                case "VIP":
                    tvCustomerType.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_light));
                    break;
                case "Regular":
                    tvCustomerType.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
                    break;
                case "New":
                    tvCustomerType.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
                    break;
                default:
                    tvCustomerType.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
                    break;
            }
            
            btnEditCustomer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(customer);
                }
            });
            
            btnDeleteCustomer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(customer);
                }
            });
        }
    }
}
