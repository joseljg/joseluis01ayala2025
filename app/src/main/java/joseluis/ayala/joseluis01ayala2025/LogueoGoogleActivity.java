package joseluis.ayala.joseluis01ayala2025;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.ClearCredentialStateRequest;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executors;

public class LogueoGoogleActivity extends AppCompatActivity {

    private static final String TAG = "LogueoGoogle";

    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;

    private Button btnGoogle, btnCerrar;
    private TextView tvStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logueo_google);

        // Ajuste de padding por insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Credential Manager
        credentialManager = CredentialManager.create(this);

        // UI
        btnGoogle = findViewById(R.id.btnGoogle);
        btnCerrar = findViewById(R.id.btnCerrarSesion);
        tvStatus = findViewById(R.id.tvStatus);

        btnGoogle.setOnClickListener(v -> launchCredentialManager());

        btnCerrar.setOnClickListener(v -> cerrarSesionUser());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // -------------------------
    // GOOGLE SIGN-IN
    // -------------------------
    private void launchCredentialManager() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignIn(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "Error Google Sign-In", e);

                        runOnUiThread(() -> {
                            if (e instanceof androidx.credentials.exceptions.NoCredentialException) {
                                Toast.makeText(
                                        LogueoGoogleActivity.this,
                                        "No hay cuentas de Google disponibles",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        LogueoGoogleActivity.this,
                                        "Error Google Sign-In",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    }
                }
        );
    }

    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential customCredential &&
                credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

            GoogleIdTokenCredential googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(customCredential.getData());

            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());

        } else {
            Log.w(TAG, "Credencial no es Google ID");
            Toast.makeText(this, "Credencial inválida", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Google sign-in success");
                        updateUI(user);
                    } else {
                        Log.w(TAG, "Google sign-in failed", task.getException());
                        Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    // -------------------------
    // CERRAR SESIÓN
    // -------------------------
    private void cerrarSesionUser() {
        // Firebase sign out
        mAuth.signOut();

        // Limpiar credenciales guardadas
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(@NonNull Void result) {
                        updateUI(null);
                        Toast.makeText(LogueoGoogleActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e(TAG, "Error al limpiar credenciales", e);
                    }
                }
        );
    }

    // -------------------------
    // UI
    // -------------------------
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            tvStatus.setText("Bienvenido: " + user.getDisplayName() + "\n" + user.getEmail());
            Toast.makeText(this, "Usuario autenticado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LogueoGoogleActivity.this, CrudArticulos.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            tvStatus.setText("No hay usuario autenticado");
        }


    }
}
