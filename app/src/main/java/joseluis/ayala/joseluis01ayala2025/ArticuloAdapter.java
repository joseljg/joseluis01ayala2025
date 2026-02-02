package joseluis.ayala.joseluis01ayala2025;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import joseluis.ayala.joseluis01ayala2025.clases.Articulo;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ViewHolder> {

    private List<Articulo> lista;
    private DatabaseReference db;

    public ArticuloAdapter(List<Articulo> lista) {
        this.lista = lista;
        db = FirebaseDatabase.getInstance().getReference("articulos");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_articulo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Articulo a = lista.get(position);

        holder.tvNombre.setText(a.getNombre());
        holder.tvPrecio.setText("€ " + a.getPrecio());

        // Cargar imagen desde URL
        Glide.with(holder.ivFoto.getContext())
                .load(a.getFoto()) // tu URL HTTP
                .placeholder(R.drawable.placeholder) // imagen temporal mientras carga
                .error(R.drawable.error)             // imagen si falla
                .into(holder.ivFoto);

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Eliminar")
                    .setMessage("¿Seguro que quieres borrar este artículo?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        db.child(a.getCodigo()).removeValue();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), InsertEditArticuloActivity.class);
            intent.putExtra("articulo", a);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio;
        Button btnEditar, btnEliminar;
        ImageView ivFoto;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            ivFoto = itemView.findViewById(R.id.ivFoto);
        }
    }
}
