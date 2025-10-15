package com.example.roomdatabase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomdatabase.R;
import com.example.roomdatabase.entity.Cake;

import java.util.ArrayList;
import java.util.List;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.CakeViewHolder> {
    
    private List<Cake> cakes;
    private OnCakeClickListener listener;
    
    public interface OnCakeClickListener {
        void onEditClick(Cake cake);
        void onDeleteClick(Cake cake);
    }
    
    public CakeAdapter(List<Cake> cakes) {
        this.cakes = cakes != null ? cakes : new ArrayList<>();
    }
    
    public void setOnCakeClickListener(OnCakeClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CakeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CakeViewHolder holder, int position) {
        Cake cake = cakes.get(position);
        holder.bind(cake);
    }
    
    @Override
    public int getItemCount() {
        return cakes.size();
    }
    
    public void updateCakes(List<Cake> newCakes) {
        this.cakes = newCakes;
        notifyDataSetChanged();
    }
    
    class CakeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCakeName, tvCakeDescription, tvCakePrice, tvCakeStock, tvCakeCategory, tvCakeSize, tvCakeFlavor;
        private Button btnEditCake, btnDeleteCake;
        
        public CakeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCakeName = itemView.findViewById(R.id.tvCakeName);
            tvCakeDescription = itemView.findViewById(R.id.tvCakeDescription);
            tvCakePrice = itemView.findViewById(R.id.tvCakePrice);
            tvCakeStock = itemView.findViewById(R.id.tvCakeStock);
            tvCakeCategory = itemView.findViewById(R.id.tvCakeCategory);
            tvCakeSize = itemView.findViewById(R.id.tvCakeSize);
            tvCakeFlavor = itemView.findViewById(R.id.tvCakeFlavor);
            btnEditCake = itemView.findViewById(R.id.btnEditCake);
            btnDeleteCake = itemView.findViewById(R.id.btnDeleteCake);
        }
        
        public void bind(Cake cake) {
            tvCakeName.setText("🍰 " + cake.getName());
            tvCakeDescription.setText(cake.getDescription());
            java.util.Locale viVN = new java.util.Locale("vi", "VN");
            java.text.NumberFormat vnd = java.text.NumberFormat.getCurrencyInstance(viVN);
            vnd.setMaximumFractionDigits(0);
            tvCakePrice.setText("💰 " + vnd.format(cake.getPrice()));
            tvCakeStock.setText("📦 Còn: " + cake.getStock());
            tvCakeCategory.setText("🏷️ Loại: " + cake.getCategory());
            tvCakeSize.setText("📏 Size: " + cake.getSize());
            tvCakeFlavor.setText("🍓 Vị: " + cake.getFlavor());
            
            btnEditCake.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(cake);
                }
            });
            
            btnDeleteCake.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(cake);
                }
            });
        }
    }
}
