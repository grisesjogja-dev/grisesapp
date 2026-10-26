package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AgendaKegiatan
import com.example.data.model.Inventaris
import com.example.data.model.IuranRecord
import com.example.data.model.JimpitanRecord
import com.example.data.model.KasTransaction
import com.example.data.model.Warga
import kotlinx.coroutines.flow.Flow

@Dao
interface WargaDao {
  @Query("SELECT * FROM warga ORDER BY blok ASC, noRumah ASC")
  fun getAllWarga(): Flow<List<Warga>>

  @Query("SELECT * FROM warga WHERE id = :id LIMIT 1")
  suspend fun getWargaById(id: Long): Warga?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWarga(warga: Warga): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(list: List<Warga>)

  @Update
  suspend fun updateWarga(warga: Warga)

  @Delete
  suspend fun deleteWarga(warga: Warga)

  @Query("DELETE FROM warga WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT COUNT(*) FROM warga")
  suspend fun getCount(): Int

  @Query("DELETE FROM warga")
  suspend fun clearAllWarga()
}

@Dao
interface IuranDao {
  @Query("SELECT * FROM iuran ORDER BY tahun DESC, bulan DESC, alamat ASC")
  fun getAllIuran(): Flow<List<IuranRecord>>

  @Query("SELECT * FROM iuran WHERE bulan = :bulan AND tahun = :tahun ORDER BY alamat ASC")
  fun getIuranByPeriode(bulan: Int, tahun: Int): Flow<List<IuranRecord>>

  @Query("SELECT * FROM iuran WHERE wargaId = :wargaId ORDER BY tahun DESC, bulan DESC")
  fun getIuranByWarga(wargaId: Long): Flow<List<IuranRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIuran(iuran: IuranRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(list: List<IuranRecord>)

  @Update
  suspend fun updateIuran(iuran: IuranRecord)

  @Delete
  suspend fun deleteIuran(iuran: IuranRecord)

  @Query("DELETE FROM iuran WHERE wargaId = :wargaId")
  suspend fun deleteByWargaId(wargaId: Long)

  @Query("DELETE FROM iuran")
  suspend fun clearAllIuran()
}

@Dao
interface JimpitanDao {
  @Query("SELECT * FROM jimpitan ORDER BY tanggal DESC, timestamp DESC")
  fun getAllJimpitan(): Flow<List<JimpitanRecord>>

  @Query("SELECT * FROM jimpitan WHERE tanggal = :tanggal ORDER BY alamat ASC")
  fun getJimpitanByTanggal(tanggal: String): Flow<List<JimpitanRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertJimpitan(jimpitan: JimpitanRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(list: List<JimpitanRecord>)

  @Update
  suspend fun updateJimpitan(jimpitan: JimpitanRecord)

  @Delete
  suspend fun deleteJimpitan(jimpitan: JimpitanRecord)

  @Query("UPDATE jimpitan SET isSetorKas = 1 WHERE tanggal = :tanggal")
  suspend fun markSetorKas(tanggal: String)

  @Query("DELETE FROM jimpitan WHERE wargaId = :wargaId")
  suspend fun deleteByWargaId(wargaId: Long)

  @Query("DELETE FROM jimpitan")
  suspend fun clearAllJimpitan()
}

@Dao
interface KasDao {
  @Query("SELECT * FROM kas_transaksi ORDER BY tanggal DESC, timestamp DESC")
  fun getAllKas(): Flow<List<KasTransaction>>

  @Query("SELECT * FROM kas_transaksi WHERE tipe = :tipe ORDER BY tanggal DESC, timestamp DESC")
  fun getKasByTipe(tipe: String): Flow<List<KasTransaction>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertKas(kas: KasTransaction): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(list: List<KasTransaction>)

  @Delete
  suspend fun deleteKas(kas: KasTransaction)
}

@Dao
interface InventarisDao {
  @Query("SELECT * FROM inventaris ORDER BY namaBarang ASC")
  fun getAllInventaris(): Flow<List<Inventaris>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInventaris(inventaris: Inventaris): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(list: List<Inventaris>)

  @Update
  suspend fun updateInventaris(inventaris: Inventaris)

  @Delete
  suspend fun deleteInventaris(inventaris: Inventaris)
}

@Dao
interface AgendaDao {
  @Query("SELECT * FROM agenda ORDER BY tanggal ASC, jam ASC")
  fun getAllAgenda(): Flow<List<AgendaKegiatan>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAgenda(agenda: AgendaKegiatan): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(list: List<AgendaKegiatan>)

  @Update
  suspend fun updateAgenda(agenda: AgendaKegiatan)

  @Delete
  suspend fun deleteAgenda(agenda: AgendaKegiatan)
}
