package joseluis.ayala.joseluis01ayala2025;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import joseluis.ayala.joseluis01ayala2025.clases.Articulo;

public class InsertEditArticuloActivity extends AppCompatActivity {

    EditText etCodigo, etNombre, etPrecio, etFoto;
    Button btnGuardar;
    DatabaseReference db;

    boolean esEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_edit_articulo);

        etCodigo = findViewById(R.id.etCodigo);
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etFoto = findViewById(R.id.etFoto);
        btnGuardar = findViewById(R.id.btnGuardar);

        db = FirebaseDatabase.getInstance().getReference("articulos");

        // Revisar si venimos a editar un artículo
        if (getIntent().hasExtra("articulo")) {
            esEdicion = true;
            Articulo a = (Articulo) getIntent().getSerializableExtra("articulo");

            etCodigo.setText(a.getCodigo());
            etCodigo.setEnabled(false); // No cambiamos el código al editar
            etNombre.setText(a.getNombre());
            etPrecio.setText(String.valueOf(a.getPrecio()));
            etFoto.setText(a.getFoto());
        }

        btnGuardar.setOnClickListener(v -> guardarArticulo());
    }

    private void guardarArticulo() {
        String codigo = etCodigo.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String foto = etFoto.getText().toString().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Articulo articulo = new Articulo(codigo, nombre, precio, foto);
        db.child(codigo).setValue(articulo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, esEdicion ? "Artículo actualizado" : "Artículo insertado", Toast.LENGTH_SHORT).show();
                    finish(); // Volvemos al CRUD
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });
    }
}

