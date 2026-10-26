package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "AiAssistantViewModel"

sealed interface AiUiState {
  data object Idle : AiUiState
  data object Loading : AiUiState
  data class Streaming(val text: String) : AiUiState
  data class Success(val text: String) : AiUiState
  data class Error(val message: String) : AiUiState
}

data class ChatMessage(
  val isUser: Boolean,
  val text: String,
  val time: String = ""
)

class AiAssistantViewModel(application: Application) : AndroidViewModel(application) {

  // Lazy evaluation: evaluated upon first inference rather than eagerly at class construction time
  private val model by lazy {
    Firebase.ai.generativeModel("gemini-3.8-flash")
  }

  private val _edaranUiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
  val edaranUiState: StateFlow<AiUiState> = _edaranUiState.asStateFlow()

  private val _waBillingUiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
  val waBillingUiState: StateFlow<AiUiState> = _waBillingUiState.asStateFlow()

  private val _kasAnalysisUiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
  val kasAnalysisUiState: StateFlow<AiUiState> = _kasAnalysisUiState.asStateFlow()

  private val _chatUiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
  val chatUiState: StateFlow<AiUiState> = _chatUiState.asStateFlow()

  private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        isUser = false,
        text = "Halo! Saya Asisten AI Paguyuban Perum Griya Sedayu Selaras (GRISES). Saya siap membantu Anda membuat draf surat edaran resmi, menyusun pesan WhatsApp penagihan iuran yang santun, menganalisis laporan keuangan kas, atau menjawab pertanyaan seputar lingkungan perumahan kita. Ada yang bisa saya bantu hari ini?"
      )
    )
  )
  val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

  /**
   * Generates a formal and warm community announcement / surat edaran with streaming.
   */
  fun generateSuratEdaran(
    judul: String,
    kategori: String,
    tanggalKegiatan: String,
    waktu: String,
    lokasi: String,
    instruksiKhusus: String
  ) {
    val prompt = """
      Bertindaklah sebagai Sekretaris Paguyuban Pengurus Perumahan Griya Sedayu Selaras (GRISES).
      Buatkan naskah draf Pengumuman / Surat Edaran resmi warga yang rapi, santun, hangat, dan profesional, sangat cocok untuk di-share di WhatsApp Grup Warga maupun dicetak.
      
      Detail Acara/Kegiatan:
      - Judul Kegiatan: $judul
      - Kategori: $kategori
      - Hari / Tanggal: $tanggalKegiatan
      - Waktu: $waktu
      - Tempat / Lokasi: $lokasi
      - Catatan & Himbauan Tambahan: $instruksiKhusus
      
      Struktur Format:
      1. Header: PENGURUS PAGUYUBAN WARGA PERUM GRIYA SEDAYU SELARAS (GRISES)
      2. Salam pembuka islami & umum yang ramah ("Assalamu'alaikum Wr. Wb. & Selamat Sejahtera Bapak/Ibu Warga GRISES yang kami hormati,")
      3. Penjelasan maksud dan tujuan kegiatan gotong royong/kebersamaan
      4. Rincian Pelaksanaan (Hari, Tanggal, Waktu, Tempat, Perlengkapan/Dresscode)
      5. Himbauan partisipasi aktif & guyub rukun
      6. Salam penutup, doa, dan penandatangan: Hormat kami, Pengurus Paguyuban Griya Sedayu Selaras.
      Sertakan emoji yang pas agar enak dibaca di WhatsApp.
    """.trimIndent()

    streamContent(prompt, _edaranUiState)
  }

  /**
   * Generates a polite, personalized WhatsApp dues reminder.
   */
  fun generateWaBillingMessage(
    namaWarga: String,
    blokRumah: String,
    periode: String,
    nominalIuran: Long,
    nominalJimpitan: Long,
    gayaBahasa: String,
    metodeBayar: String
  ) {
    val totalTagihan = nominalIuran + nominalJimpitan
    val prompt = """
      Bertindaklah sebagai Bendahara Paguyuban Perum Griya Sedayu Selaras (GRISES).
      Buatkan draf pesan pengingat tagihan iuran warga via WhatsApp yang sangat ramah, santun, tidak menyinggung, dan mengedepankan asas kekeluargaan serta transparansi.
      
      Data Warga:
      - Nama Kepala Keluarga / Warga: $namaWarga
      - Blok / No Rumah: $blokRumah
      - Periode Tagihan: $periode
      - Iuran Pengelolaan Lingkungan & Kebersihan: Rp $nominalIuran
      - Jimpitan Ronda Harian (Rp 500/hari): Rp $nominalJimpitan
      - Total Nominal: Rp $totalTagihan
      - Gaya Bahasa yang Diinginkan: $gayaBahasa (misal: "Sangat Santun & Akrab Guyub", "Formal Sopan Pengurus", atau "Pengingat Halus Warga Baru")
      - Petunjuk Pembayaran: $metodeBayar (bisa transfer ke rekening bendahara atau titip tunai saat ronda/ke rumah bendahara)
      
      Buat pesannya langsung siap disalin dan dikirimkan ke WhatsApp Bapak/Ibu $namaWarga.
    """.trimIndent()

    streamContent(prompt, _waBillingUiState)
  }

  /**
   * Generates a comprehensive real-time financial audit and summary for citizen assembly.
   */
  fun generateKasAnalysis(
    saldoSaatIni: Long,
    totalPemasukan: Long,
    totalPengeluaran: Long,
    totalJimpitanBulanIni: Long,
    jumlahWargaLunas: Int,
    jumlahWargaBelumLunas: Int,
    totalWarga: Int
  ) {
    val prompt = """
      Bertindaklah sebagai Auditor Keuangan Independen & Penasihat Paguyuban Warga Perum Griya Sedayu Selaras (GRISES).
      Lakukan analisis keuangan cerdas dan buatkan ringkasan laporan kas yang transparan, mudah dipahami warga biasa, akuntabel, dan berikan rekomendasi strategis untuk pengurus.
      
      Data Keuangan Real-Time GRISES:
      - Saldo Kas Tersedia Saat Ini: Rp $saldoSaatIni
      - Akumulasi Total Pemasukan: Rp $totalPemasukan
      - Akumulasi Total Pengeluaran: Rp $totalPengeluaran
      - Akumulasi Jimpitan Ronda Bulan Ini (Rp 500/rumah/hari): Rp $totalJimpitanBulanIni
      - Kepatuhan Pembayaran Iuran Bulan Ini: $jumlahWargaLunas dari $totalWarga KK sudah lunas ($jumlahWargaBelumLunas KK belum lunas)
      
      Format Analisis:
      1. 📊 Status Kesehatan Finansial Paguyuban (Surplus/Defisit, Cadangan Kas)
      2. 💡 Analisis Realisasi Jimpitan Ronda & Efektivitas Pengumpulan
      3. 🔍 Transparansi dan Tingkat Partisipasi Iuran Warga
      4. 🛡️ 3 Rekomendasi Alokasi & Efisiensi Anggaran (Dana Darurat, Perawatan Fasum/Lampu Jalan, Pengadaan Inventaris Warga)
      5. 📝 Pesan Singkat Transparansi untuk dibagikan ke forum rembug warga / WAG GRISES.
    """.trimIndent()

    streamContent(prompt, _kasAnalysisUiState)
  }

  /**
   * Handles interactive conversational Q&A for residents and board members.
   */
  fun sendChatMessage(userMessage: String) {
    if (userMessage.isBlank()) return

    val currentList = _chatHistory.value.toMutableList()
    currentList.add(ChatMessage(isUser = true, text = userMessage))
    _chatHistory.value = currentList

    val systemContext = """
      Anda adalah "Asisten Pintar GRISES", asisten AI resmi Paguyuban Perum Griya Sedayu Selaras.
      Konteks Perumahan:
      - Nama: Perum Griya Sedayu Selaras (GRISES) di Sedayu, Bantul, D.I. Yogyakarta.
      - Terdiri dari 58 Kepala Keluarga / nomor rumah (Blok A hingga Blok F).
      - Program rutin: Jimpitan ronda malam Rp 500/rumah/hari (ditaruh di wadah jimpitan depan rumah), iuran bulanan pengelolaan lingkungan, kerja bakti berkala, siskamling ronda malam, serta peminjaman inventaris perumahan (tenda tratak, kursi lipat, sound system, mesin rumput).
      - Nilai utama: Transparansi keuangan kas, keguyuban, gotong royong, keamanan, dan kerukunan warga.
      
      Jawablah pertanyaan warga atau pengurus berikut secara jelas, ramah, solutif, dan mengedepankan etika bertetangga yang baik:
      "$userMessage"
    """.trimIndent()

    viewModelScope.launch {
      _chatUiState.value = AiUiState.Loading
      val accumulated = StringBuilder()
      try {
        withTimeout(60_000L) {
          model.generateContentStream(systemContext).collect { chunk ->
            val chunkText = chunk.text.orEmpty()
            accumulated.append(chunkText)
            _chatUiState.value = AiUiState.Streaming(accumulated.toString())
          }
        }
        val finalText = accumulated.toString()
        if (finalText.isNotBlank()) {
          _chatUiState.value = AiUiState.Success(finalText)
          val updated = _chatHistory.value.toMutableList()
          updated.add(ChatMessage(isUser = false, text = finalText))
          _chatHistory.value = updated
        } else {
          _chatUiState.value = AiUiState.Error("Model tidak memberikan respon.")
        }
      } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "Chat generation timed out", e)
        _chatUiState.value = AiUiState.Error("Waktu permintaan habis (60 detik). Silakan coba lagi.")
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e(TAG, "Chat generation failed", e)
        _chatUiState.value = AiUiState.Error(e.localizedMessage ?: "Gagal memproses pesan.")
      }
    }
  }

  private fun streamContent(prompt: String, targetState: MutableStateFlow<AiUiState>) {
    viewModelScope.launch {
      targetState.value = AiUiState.Loading
      val accumulated = StringBuilder()
      try {
        withTimeout(60_000L) {
          model.generateContentStream(prompt).collect { chunk ->
            val chunkText = chunk.text.orEmpty()
            accumulated.append(chunkText)
            targetState.value = AiUiState.Streaming(accumulated.toString())
          }
        }
        val finalText = accumulated.toString()
        if (finalText.isNotBlank()) {
          targetState.value = AiUiState.Success(finalText)
        } else {
          targetState.value = AiUiState.Error("Model mengembalikan respon kosong.")
        }
      } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "Inference timed out", e)
        targetState.value = AiUiState.Error("Permintaan menghasilkan batas waktu (60s). Silakan ulangi.")
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e(TAG, "Inference failed", e)
        targetState.value = AiUiState.Error(e.localizedMessage ?: "Gagal menghasilkan konten AI.")
      }
    }
  }

  fun clearEdaranState() {
    _edaranUiState.value = AiUiState.Idle
  }

  fun clearWaBillingState() {
    _waBillingUiState.value = AiUiState.Idle
  }

  fun clearKasAnalysisState() {
    _kasAnalysisUiState.value = AiUiState.Idle
  }
}
