package com.example.roomdatabase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomdatabase.R;
import com.example.roomdatabase.entity.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    
    private List<Order> orders;
    private OnOrderClickListener listener;
    
    public interface OnOrderClickListener {
        void onEditClick(Order order);
        void onDeleteClick(Order order);
    }
    
    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }
    
    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }
    
    @Override
    public int getItemCount() {
        return orders.size();
    }
    
    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }
    
    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId, tvOrderUserId, tvOrderProductId, tvOrderQuantity, tvOrderTotal, tvOrderDate, tvOrderStatus;
        private Button btnEditOrder, btnDeleteOrder;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderUserId = itemView.findViewById(R.id.tvOrderUserId);
            tvOrderProductId = itemView.findViewById(R.id.tvOrderProductId);
            tvOrderQuantity = itemView.findViewById(R.id.tvOrderQuantity);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnEditOrder = itemView.findViewById(R.id.btnEditOrder);
            btnDeleteOrder = itemView.findViewById(R.id.btnDeleteOrder);
        }
        
        public void bind(Order order) {
            tvOrderId.setText("Order #" + order.getId());
            tvOrderUserId.setText("User ID: " + order.getUserId());
            tvOrderProductId.setText("Product ID: " + order.getProductId());
            tvOrderQuantity.setText("Qty: " + order.getQuantity());
            tvOrderTotal.setText("$" + String.format("%.2f", order.getTotalPrice()));
            tvOrderDate.setText("Date: " + order.getOrderDate());
            tvOrderStatus.setText("Status: " + order.getStatus());
            
            btnEditOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(order);
                }
            });
            
            btnDeleteOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(order);
                }
            });
        }
    }
}
