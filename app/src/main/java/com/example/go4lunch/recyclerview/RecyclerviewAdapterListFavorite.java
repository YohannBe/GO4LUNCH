package com.example.go4lunch.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.viewmodel.UserViewModel;

import java.util.List;

public class RecyclerviewAdapterListFavorite extends RecyclerView.Adapter<RecyclerviewAdapterListFavorite.ViewHolder> {

    private final List<String> listFavorite;
    private final Context context;
    private final UserViewModel userViewModel;
    private final String userId;

    public RecyclerviewAdapterListFavorite(Context context, UserViewModel userViewModel, List<String> listFavorite, String userId) {
        this.context = context;
        this.userViewModel = userViewModel;
        this.listFavorite = listFavorite;
        this.userId = userId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String[] getName = listFavorite.get(position).split("/");
        if (getName.length == 2) {
            holder.name.setText(getName[1]);
            holder.icon.setTag("favorite");
        }


        holder.icon.setOnClickListener(v -> {
            String toast;
            if (holder.icon.getTag().equals("favorite")) {
                userViewModel.deleteFavoriteFromList(userId, listFavorite.get(position));
                holder.icon.setImageResource(R.drawable.favorite_false_icons);
                holder.icon.setTag("not favorite");
                toast = context.getString(R.string.delete_from_favorite);
            } else {
                userViewModel.createFavoriteList(userId, listFavorite.get(position));
                holder.icon.setImageResource(R.drawable.favorite_true_icons);
                holder.icon.setTag("favorite");
                toast = context.getString(R.string.add_place_favorite);
            }

            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        if (listFavorite != null)
            return listFavorite.size();
        else return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_item_favorite);
            icon = itemView.findViewById(R.id.icon_item_favorite);
        }
    }
}
