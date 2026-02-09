package joseluis.ayala.joseluis01ayala2025.clases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Articulo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract ArticuloDao articuloDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "articulos_db"
                    ).allowMainThreadQueries() // SOLO para proyectos académicos
                    .build();
        }
        return INSTANCE;
    }
}