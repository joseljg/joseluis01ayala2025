package joseluis.ayala.joseluis01ayala2025;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import joseluis.ayala.joseluis01ayala2025.clases.Articulo;

public class CrudArticulos extends AppCompatActivity {

    EditText etBuscar;
    Button btnBuscar, btnInsertar;
    RecyclerView recyclerView;

    DatabaseReference db;
    List<Articulo> listaCompleta; // contiene todos los artículos
    List<Articulo> lista;         // lista que se muestra en RecyclerView
    ArticuloAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_articulos);

        etBuscar = findViewById(R.id.etBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnInsertar = findViewById(R.id.btnInsertar);
        recyclerView = findViewById(R.id.recyclerArticulos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaCompleta = new ArrayList<>();
        lista = new ArrayList<>();
        adapter = new ArticuloAdapter(lista);
        recyclerView.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference("articulos");

        cargarArticulos();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrudArticulos.this.buscar();
            }
        });
        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CrudArticulos.this, InsertEditArticuloActivity.class));            }
        });
    }

    private void cargarArticulos() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCompleta.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    listaCompleta.add(ds.getValue(Articulo.class));
                }

                // Siempre actualizamos la lista mostrada
                lista.clear();
                lista.addAll(listaCompleta);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void buscar() {
        String texto = etBuscar.getText().toString().toLowerCase();

        if (texto.isEmpty()) {
            // Si no hay texto, mostramos todos
            lista.clear();
            lista.addAll(listaCompleta);
        } else {
            List<Articulo> filtrados = new ArrayList<>();
            for (Articulo a : listaCompleta) {
                if (a.getNombre().toLowerCase().contains(texto) ||
                        a.getCodigo().toLowerCase().contains(texto)) {
                    filtrados.add(a);
                }
            }
            lista.clear();
            lista.addAll(filtrados);
        }

        adapter.notifyDataSetChanged();
        }
    public void actualizarLista(List<Articulo> nuevaLista) {
        lista.clear();
        lista.addAll(nuevaLista);
        adapter.notifyDataSetChanged();
    }
}
