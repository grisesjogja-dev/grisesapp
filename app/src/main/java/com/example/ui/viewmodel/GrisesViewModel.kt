package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.GrisesDatabase
import com.example.data.model.AgendaKegiatan
import com.example.data.model.Inventaris
import com.example.data.model.IuranRecord
import com.example.data.model.JimpitanRecord
import com.example.data.model.JimpitanSummary
import com.example.data.model.KasSummary
import com.example.data.model.KasTransaction
import com.example.data.model.Warga
import com.example.data.repository.GrisesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GrisesViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: GrisesRepository
  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

  // Admin / Mode Pengurus vs Mode Warga toggle
  private val _isAdminMode = MutableStateFlow(true)
  val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

  // Selected Period for Iuran
  private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
  val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

  private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
  val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

  // Selected Date for Jimpitan (default today)
  private val _selectedJimpitanDate = MutableStateFlow(dateFormat.format(Date()))
  val selectedJimpitanDate: StateFlow<String> = _selectedJimpitanDate.asStateFlow()

  // Search queries
  private val _wargaSearchQuery = MutableStateFlow("")
  val wargaSearchQuery: StateFlow<String> = _wargaSearchQuery.asStateFlow()

  private val _selectedBlokFilter = MutableStateFlow("Semua")
  val selectedBlokFilter: StateFlow<String> = _selectedBlokFilter.asStateFlow()

  private val _selectedStatusFilter = MutableStateFlow("Semua")
  val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

  init {
    val db = GrisesDatabase.getDatabase(application, viewModelScope)
    repository = GrisesRepository(db)
    viewModelScope.launch {
      // Automatically load the official 58 warga from checklist if not yet loaded
      if (db.wargaDao().getCount() < 58) {
        repository.resetInitialData()
      }
    }
  }

  fun reloadOfficialChecklistData() {
    viewModelScope.launch {
      repository.resetInitialData()
    }
  }

  fun toggleAdminMode(enabled: Boolean) {
    _isAdminMode.value = enabled
  }

  fun setWargaSearchQuery(query: String) {
    _wargaSearchQuery.value = query
  }

  fun setSelectedBlokFilter(blok: String) {
    _selectedBlokFilter.value = blok
  }

  fun setSelectedStatusFilter(status: String) {
    _selectedStatusFilter.value = status
  }

  fun setSelectedMonth(month: Int) {
    _selectedMonth.value = month
  }

  fun setSelectedYear(year: Int) {
    _selectedYear.value = year
  }

  fun setSelectedJimpitanDate(date: String) {
    _selectedJimpitanDate.value = date
  }

  // --- WARGA STATE ---
  val allWarga: StateFlow<List<Warga>> = repository.allWarga
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredWarga: StateFlow<List<Warga>> = combine(
    allWarga,
    _wargaSearchQuery,
    _selectedBlokFilter,
    _selectedStatusFilter
  ) { list, query, blok, status ->
    list.filter { w ->
      val matchesQuery = query.isBlank() ||
        w.namaKK.contains(query, ignoreCase = true) ||
        w.blok.contains(query, ignoreCase = true) ||
        w.noRumah.contains(query, ignoreCase = true) ||
        w.statusHunian.contains(query, ignoreCase = true) ||
        w.catatan.contains(query, ignoreCase = true) ||
        w.noHp.contains(query)

      val matchesBlok = (blok == "Semua") || (w.blok.equals(blok, ignoreCase = true))

      val matchesStatus = when (status) {
        "Semua" -> true
        "Dihuni Pemilik" -> w.statusHunian == "Dihuni Pemilik"
        "Dihuni Kontrak" -> w.statusHunian == "Dihuni Kontrak"
        "Kosong (Bayar Iuran)" -> w.statusHunian == "Kosong (Bayar Iuran)"
        "Kosong" -> !w.isDihuni
        else -> true
      }

      matchesQuery && matchesBlok && matchesStatus
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val wargaStats: StateFlow<Triple<Int, Int, Int>> = allWarga.combine(_isAdminMode) { list, _ ->
    val totalRumah = list.size
    val totalJiwa = list.sumOf { it.jumlahAnggota }
    val totalDihuni = list.count { it.isDihuni }
    Triple(totalRumah, totalJiwa, totalDihuni)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0, 0))

  fun addWarga(warga: Warga) {
    viewModelScope.launch {
      repository.insertWarga(warga)
    }
  }

  fun updateWarga(warga: Warga) {
    viewModelScope.launch {
      repository.updateWarga(warga)
    }
  }

  fun deleteWarga(warga: Warga) {
    viewModelScope.launch {
      repository.deleteWarga(warga)
    }
  }

  // --- IURAN STATE ---
  val allIuran: StateFlow<List<IuranRecord>> = repository.allIuran
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val currentPeriodIuran: StateFlow<List<IuranRecord>> = combine(allIuran, _selectedMonth, _selectedYear) { list, m, y ->
    list.filter { it.bulan == m && it.tahun == y }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun markIuranPaid(iuran: IuranRecord, metode: String = "Tunai", penerima: String = "Bendahara") {
    viewModelScope.launch {
      repository.markIuranPaid(iuran, metode, penerima)
    }
  }

  fun markIuranUnpaid(iuran: IuranRecord) {
    viewModelScope.launch {
      repository.markIuranUnpaid(iuran)
    }
  }

  // --- JIMPITAN STATE ---
  val allJimpitan: StateFlow<List<JimpitanRecord>> = repository.allJimpitan
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val currentJimpitanList: StateFlow<List<JimpitanRecord>> = combine(allJimpitan, _selectedJimpitanDate, allWarga) { jimpitans, date, wargas ->
    val existingForDate = jimpitans.filter { it.tanggal == date }
    if (existingForDate.isNotEmpty()) {
      existingForDate
    } else {
      // Create preview list for occupied warga with default 500
      wargas.filter { it.isDihuni }.map { w ->
        JimpitanRecord(
          wargaId = w.id,
          namaKK = w.namaKK,
          alamat = w.alamatLengkap,
          tanggal = date,
          nominal = 500L,
          isCollected = false,
          petugasRonda = "Regu Ronda Malam",
          isSetorKas = false
        )
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val jimpitanSummary: StateFlow<JimpitanSummary> = combine(allJimpitan, _selectedJimpitanDate) { list, selectedDate ->
    val dateRecords = list.filter { it.tanggal == selectedDate }
    val totalHariIni = dateRecords.filter { it.isCollected }.sumOf { it.nominal }
    val jumlahRumah = dateRecords.count { it.isCollected }
    val totalBulanIni = list.filter {
      it.isCollected && it.tanggal.startsWith(selectedDate.take(7))
    }.sumOf { it.nominal }
    val totalBelumSetor = list.filter { it.isCollected && !it.isSetorKas }.sumOf { it.nominal }

    JimpitanSummary(
      totalHariIni = totalHariIni,
      jumlahRumahHariIni = jumlahRumah,
      totalBulanIni = totalBulanIni,
      totalBelumSetor = totalBelumSetor
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), JimpitanSummary())

  fun toggleJimpitanForWarga(record: JimpitanRecord) {
    viewModelScope.launch {
      if (record.id == 0L) {
        // Not saved yet, toggle to collected and save
        repository.saveOrUpdateJimpitanList(listOf(record.copy(isCollected = !record.isCollected)))
      } else {
        repository.toggleJimpitanStatus(record)
      }
    }
  }

  fun markAllJimpitanCollected(date: String, nominal: Long = 500L, petugas: String = "Regu Ronda Malam") {
    viewModelScope.launch {
      val wargas = allWarga.value.filter { it.isDihuni }
      val records = wargas.map { w ->
        JimpitanRecord(
          wargaId = w.id,
          namaKK = w.namaKK,
          alamat = w.alamatLengkap,
          tanggal = date,
          nominal = nominal,
          isCollected = true,
          petugasRonda = petugas,
          isSetorKas = false
        )
      }
      repository.saveOrUpdateJimpitanList(records)
    }
  }

  fun setorJimpitanToKas(tanggal: String, totalNominal: Long, petugas: String) {
    viewModelScope.launch {
      repository.setorJimpitanToKas(tanggal, totalNominal, petugas)
    }
  }

  // --- KAS MASUK & KELUAR STATE ---
  val allKas: StateFlow<List<KasTransaction>> = repository.allKas
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val kasSummary: StateFlow<KasSummary> = allKas.combine(_isAdminMode) { list, _ ->
    val masuk = list.filter { it.tipe == "MASUK" }.sumOf { it.nominal }
    val keluar = list.filter { it.tipe == "KELUAR" }.sumOf { it.nominal }
    KasSummary(
      totalMasuk = masuk,
      totalKeluar = keluar,
      saldoSaatIni = masuk - keluar
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KasSummary())

  fun addKasTransaction(
    tipe: String,
    kategori: String,
    nominal: Long,
    keterangan: String,
    pencatat: String,
    tanggal: String = dateFormat.format(Date())
  ) {
    viewModelScope.launch {
      repository.addKasTransaction(
        KasTransaction(
          tipe = tipe,
          kategori = kategori,
          nominal = nominal,
          tanggal = tanggal,
          keterangan = keterangan,
          pencatat = pencatat,
          noBukti = "KAS-${if (tipe == "MASUK") "IN" else "OUT"}-${System.currentTimeMillis() % 10000}"
        )
      )
    }
  }

  fun deleteKasTransaction(transaction: KasTransaction) {
    viewModelScope.launch {
      repository.deleteKasTransaction(transaction)
    }
  }

  // --- INVENTARIS STATE ---
  val allInventaris: StateFlow<List<Inventaris>> = repository.allInventaris
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun addInventaris(inventaris: Inventaris) {
    viewModelScope.launch {
      repository.addInventaris(inventaris)
    }
  }

  fun updateInventaris(inventaris: Inventaris) {
    viewModelScope.launch {
      repository.updateInventaris(inventaris)
    }
  }

  fun deleteInventaris(inventaris: Inventaris) {
    viewModelScope.launch {
      repository.deleteInventaris(inventaris)
    }
  }

  fun pinjamInventaris(item: Inventaris, namaPeminjam: String, keperluan: String, rencanaKembali: String, jumlah: Int) {
    viewModelScope.launch {
      repository.pinjamInventaris(item, namaPeminjam, keperluan, rencanaKembali, jumlah)
    }
  }

  fun kembalikanInventaris(item: Inventaris) {
    viewModelScope.launch {
      repository.kembalikanInventaris(item)
    }
  }

  // --- AGENDA STATE ---
  val allAgenda: StateFlow<List<AgendaKegiatan>> = repository.allAgenda
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun addAgenda(agenda: AgendaKegiatan) {
    viewModelScope.launch {
      repository.addAgenda(agenda)
    }
  }

  fun updateAgenda(agenda: AgendaKegiatan) {
    viewModelScope.launch {
      repository.updateAgenda(agenda)
    }
  }

  fun deleteAgenda(agenda: AgendaKegiatan) {
    viewModelScope.launch {
      repository.deleteAgenda(agenda)
    }
  }
}
