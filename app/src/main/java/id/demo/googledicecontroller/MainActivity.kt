package id.demo.googledicecontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("dice", MODE_PRIVATE)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(28,32,28,24) }
        layout.addView(TextView(this).apply { text = "Google Dice Controller"; textSize = 24f })
        layout.addView(TextView(this).apply { text = "Target total (contoh: 34 untuk 9 dadu d6)"; textSize = 16f; setPadding(0,24,0,8) })
        val target = EditText(this).apply { inputType = 2; setText(prefs.getInt("target",34).toString()); hint = "Masukkan target" }
        layout.addView(target)
        layout.addView(TextView(this).apply { text = "Aplikasi hanya membaca hasil yang tampil dan mencoba menekan Roll/Lempar. Tidak bisa memaksa angka acak Google."; setPadding(0,16,0,16) })
        layout.addView(Button(this).apply {
            text = "Simpan target"
            setOnClickListener {
                val n = target.text.toString().toIntOrNull()
                if (n == null || n !in 9..54) Toast.makeText(this@MainActivity,"Target harus 9–54 untuk 9 dadu d6",Toast.LENGTH_LONG).show()
                else { prefs.edit().putInt("target",n).apply(); Toast.makeText(this@MainActivity,"Target tersimpan: $n",Toast.LENGTH_SHORT).show() }
            }
        })
        layout.addView(Button(this).apply { text = "Aktifkan Accessibility"; setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) } })
        layout.addView(Button(this).apply { text = "Buka Google"; setOnClickListener {
            val i = packageManager.getLaunchIntentForPackage("com.google.android.googlequicksearchbox")
                ?: packageManager.getLaunchIntentForPackage("com.android.chrome")
            if (i != null) startActivity(i) else Toast.makeText(this@MainActivity,"Google/Chrome tidak ditemukan",Toast.LENGTH_LONG).show()
        } })
        setContentView(ScrollView(this).apply { addView(layout) })
    }
}
