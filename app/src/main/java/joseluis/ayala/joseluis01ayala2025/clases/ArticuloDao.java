package joseluis.ayala.joseluis01ayala2025.clases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticuloDao {
    @Query("SELECT * FROM articulos")
    List<Articulo> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Articulo> articulos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Articulo articulo);

    @Delete
    void delete(Articulo articulo);

    @Query("DELETE FROM articulos")
    void deleteAll();
}
