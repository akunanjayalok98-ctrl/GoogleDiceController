package id.demo.googledicecontroller

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class DiceAccessibilityService : AccessibilityService() {
    private var lastAction = 0L
    private var lastSignature = ""
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg != "com.android.chrome" && pkg != "com.google.android.googlequicksearchbox") return
        val root = rootInActiveWindow ?: return
        val values = mutableListOf<Int>()
        collect(root, values)
        val faces = values.filter { it in 1..6 }
        if (faces.size >= 9) {
            val selected = faces.takeLast(9)
            val total = selected.sum()
            val signature = selected.joinToString(",")
            val target = getSharedPreferences("dice", MODE_PRIVATE).getInt("target",34)
            if (signature != lastSignature) {
                lastSignature = signature
                if (total == target) {
                    Toast.makeText(this, "Target terdeteksi: $total", Toast.LENGTH_LONG).show()
                    return
                }
            }
            if (System.currentTimeMillis() - lastAction > 2500) {
                val button = findRoll(root)
                if (button != null) {
                    lastAction = System.currentTimeMillis()
                    button.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Toast.makeText(this, "Total terbaca $total; mencoba lempar lagi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun collect(node: AccessibilityNodeInfo, out: MutableList<Int>) {
        val s = node.text?.toString()?.trim()
        val n = s?.toIntOrNull()
        if (n != null && n in 1..6) out.add(n)
        for (i in 0 until node.childCount) node.getChild(i)?.let { collect(it, out) }
    }
    private fun findRoll(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val label = (node.text?.toString() ?: node.contentDescription?.toString() ?: "").lowercase()
        if ((label == "roll" || label == "lempar" || label.contains("roll dice") || label.contains("lempar dadu")) && node.isClickable) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findRoll(child)
            if (found != null) return found
        }
        return null
    }
    override fun onInterrupt() {}
}
