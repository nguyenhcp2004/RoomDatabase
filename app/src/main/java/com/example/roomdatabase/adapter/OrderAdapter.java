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

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    
    private List<Order> orders;
    private OnOrderClickListener listener;
    
    public interface OnOrderClickListener {
        void onEditClick(Order order);
        void onDeleteClick(Order order);
    }
    
    public OrderAdapter(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
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
        private TextView tvOrderId, tvOrderCustomerId, tvOrderCakeId, tvOrderQuantity, tvOrderTotal, tvOrderDate, tvOrderDeliveryDate, tvOrderStatus, tvOrderSpecialInstructions;
        private Button btnEditOrder, btnDeleteOrder;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderCustomerId = itemView.findViewById(R.id.tvOrderCustomerId);
            tvOrderCakeId = itemView.findViewById(R.id.tvOrderCakeId);
            tvOrderQuantity = itemView.findViewById(R.id.tvOrderQuantity);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderDeliveryDate = itemView.findViewById(R.id.tvOrderDeliveryDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderSpecialInstructions = itemView.findViewById(R.id.tvOrderSpecialInstructions);
            btnEditOrder = itemView.findViewById(R.id.btnEditOrder);
            btnDeleteOrder = itemView.findViewById(R.id.btnDeleteOrder);
        }
        
        public void bind(Order order) {
            tvOrderId.setText("📋 Đơn Hàng #" + order.getId());
            tvOrderCustomerId.setText("👤 Khách: #" + order.getCustomerId());
            tvOrderCakeId.setText("🍰 Bánh: #" + order.getCakeId());
            tvOrderQuantity.setText("📦 SL: " + order.getQuantity());
            tvOrderTotal.setText("💰 " + String.format("%.0f", order.getTotalPrice()) + "đ");
            tvOrderDate.setText("📅 Đặt: " + order.getOrderDate());
            tvOrderDeliveryDate.setText("🚚 Giao: " + (order.getDeliveryDate() != null ? order.getDeliveryDate() : "Chưa xác định"));
            tvOrderStatus.setText("⏳ Trạng thái: " + order.getStatus());
            tvOrderSpecialInstructions.setText("📝 Ghi chú: " + (order.getSpecialInstructions() != null && !order.getSpecialInstructions().isEmpty() ? order.getSpecialInstructions() : "Không có"));
            
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
