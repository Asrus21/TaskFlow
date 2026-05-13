package com.taskflow.app

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeLayout: LinearLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var contentFrame: FrameLayout
    private lateinit var navHome: LinearLayout
    private lateinit var navTodo: LinearLayout
    private lateinit var navMissions: LinearLayout
    private lateinit var navDaily: LinearLayout
    private lateinit var etWelcomeName: EditText
    private lateinit var btnWelcomeEnter: Button
    private lateinit var tvUsername: TextView

    private val BG     = Color.parseColor("#0F0F14")
    private val SIDEBAR = Color.parseColor("#16161E")
    private val CARD   = Color.parseColor("#1E1E2E")
    private val CARD2  = Color.parseColor("#252535")
    private val ACCENT = Color.parseColor("#7C6AF7")
    private val GREEN  = Color.parseColor("#4ADE80")
    private val RED    = Color.parseColor("#F87171")
    private val YELLOW = Color.parseColor("#FACC15")
    private val TEXT   = Color.parseColor("#E2E8F0")
    private val TEXT2  = Color.parseColor("#94A3B8")
    private val BORDER = Color.parseColor("#2E2E3F")

    private var currentPage = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUI()
        val username = DataManager.getUsername(this)
        if (username.isEmpty()) {
            showWelcome()
        } else {
            tvUsername.text = "Ola, $username"
            showMain()
            showPage("home")
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun buildUI() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
            layoutParams = ViewGroup.LayoutParams(-1, -1)
        }
        setContentView(root)

        // ── Welcome ──────────────────────────────────────────────────────────
        welcomeLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundColor(BG)
            setPadding(dp(32), dp(32), dp(32), dp(32))
            layoutParams = LinearLayout.LayoutParams(-1, -1)
        }

        val tvIcon = TextView(this).apply {
            text = "TaskFlow"
            textSize = 28f
            setTextColor(ACCENT)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = android.view.Gravity.CENTER
        }
        val tvSub = TextView(this).apply {
            text = "Como voce quer ser chamado?"
            textSize = 15f
            setTextColor(TEXT2)
            gravity = android.view.Gravity.CENTER
            setPadding(0, dp(8), 0, dp(20))
        }
        etWelcomeName = EditText(this).apply {
            hint = "Digite seu nome..."
            setHintTextColor(TEXT2)
            setTextColor(TEXT)
            setBackgroundColor(CARD)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            textSize = 15f
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) }
        }
        btnWelcomeEnter = Button(this).apply {
            text = "Entrar"
            setBackgroundColor(ACCENT)
            setTextColor(Color.WHITE)
            textSize = 15f
            layoutParams = LinearLayout.LayoutParams(-1, dp(50))
        }
        btnWelcomeEnter.setOnClickListener {
            val name = etWelcomeName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, digite seu nome!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DataManager.setUsername(this, name)
            tvUsername.text = "Ola, $name"
            showMain()
            showPage("home")
        }
        welcomeLayout.addView(tvIcon)
        welcomeLayout.addView(tvSub)
        welcomeLayout.addView(etWelcomeName)
        welcomeLayout.addView(btnWelcomeEnter)
        root.addView(welcomeLayout)

        // ── Main ─────────────────────────────────────────────────────────────
        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
            layoutParams = LinearLayout.LayoutParams(-1, -1)
            visibility = View.GONE
        }

        // Content
        contentFrame = FrameLayout(this).apply {
            setBackgroundColor(BG)
            layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
        }
        mainLayout.addView(contentFrame)

        // Bottom nav
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(SIDEBAR)
            layoutParams = LinearLayout.LayoutParams(-1, dp(60))
        }

        fun navBtn(icon: String, label: String): LinearLayout {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, -1, 1f)
                val tvI = TextView(this@MainActivity).apply {
                    text = icon; textSize = 18f; gravity = android.view.Gravity.CENTER
                    setTextColor(TEXT2)
                }
                val tvL = TextView(this@MainActivity).apply {
                    text = label; textSize = 10f; gravity = android.view.Gravity.CENTER
                    setTextColor(TEXT2)
                }
                addView(tvI); addView(tvL)
                tag = arrayOf(tvI, tvL)
            }
        }

        navHome     = navBtn("H", "Inicio")
        navTodo     = navBtn("T", "To-Do")
        navMissions = navBtn("M", "Missoes")
        navDaily    = navBtn("D", "Diarias")

        tvUsername = TextView(this).apply {
            text = ""; textSize = 11f; setTextColor(TEXT2)
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(-1, -2)
        }

        nav.addView(navHome); nav.addView(navTodo)
        nav.addView(navMissions); nav.addView(navDaily)
        mainLayout.addView(nav)

        navHome.setOnClickListener     { showPage("home") }
        navTodo.setOnClickListener     { showPage("todos") }
        navMissions.setOnClickListener { showPage("missions") }
        navDaily.setOnClickListener    { showPage("daily") }

        root.addView(mainLayout)
    }

    private fun showWelcome() { welcomeLayout.visibility = View.VISIBLE; mainLayout.visibility = View.GONE }
    private fun showMain()    { welcomeLayout.visibility = View.GONE; mainLayout.visibility = View.VISIBLE }

    private fun setNavActive(active: LinearLayout) {
        for (nav in listOf(navHome, navTodo, navMissions, navDaily)) {
            val arr = nav.tag as Array<*>
            val bg = if (nav == active) ACCENT else SIDEBAR
            nav.setBackgroundColor(bg)
            (arr[0] as TextView).setTextColor(TEXT)
            (arr[1] as TextView).setTextColor(TEXT)
        }
    }

    private fun showPage(page: String) {
        currentPage = page
        contentFrame.removeAllViews()
        when (page) {
            "home"     -> { setNavActive(navHome);     buildHome() }
            "todos"    -> { setNavActive(navTodo);     buildTodos() }
            "missions" -> { setNavActive(navMissions); buildMissions() }
            "daily"    -> { setNavActive(navDaily);    buildDaily() }
        }
    }

    // ── HOME ──────────────────────────────────────────────────────────────────
    private fun buildHome() {
        val scroll = ScrollView(this).apply { setBackgroundColor(BG); layoutParams = FrameLayout.LayoutParams(-1,-1) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(24))
        }
        val username = DataManager.getUsername(this)
        val hour = LocalTime.now().hour
        val greet = when { hour < 12 -> "Bom dia"; hour < 18 -> "Boa tarde"; else -> "Boa noite" }
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        root.addView(textView("$greet, $username!", 22f, TEXT, bold = true))
        root.addView(textView(date, 12f, TEXT2).apply { setPadding(0,0,0,dp(20)) })

        val todos   = DataManager.getTodos(this)
        val missions= DataManager.getMissions(this)
        val daily   = DataManager.getDailyTasks(this)
        val today   = LocalDate.now().toString()

        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(-1,-2).apply { bottomMargin = dp(20) }
        }
        fun statCard(label: String, value: String, color: Int) = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(CARD)
            setPadding(dp(14),dp(14),dp(14),dp(14))
            layoutParams = LinearLayout.LayoutParams(0,-2,1f).apply { marginEnd = dp(8) }
            addView(textView(label, 10f, TEXT2))
            addView(textView(value, 20f, color, bold = true))
        }
        statsRow.addView(statCard("To-Do", "${todos.count{it.done}}/${todos.size}", ACCENT))
        statsRow.addView(statCard("Missoes", "${missions.count{it.done}}/${missions.size}", GREEN))
        statsRow.addView(statCard("Hoje", "${daily.count{it.doneDate==today}}/${daily.size}", YELLOW))
        root.addView(statsRow)

        root.addView(textView("TAREFAS PENDENTES", 11f, TEXT2, bold = true).apply { setPadding(0,0,0,dp(8)) })
        val pending = todos.filter { !it.done }.take(5)
        if (pending.isEmpty()) root.addView(textView("Nenhuma tarefa pendente!", 13f, TEXT2))
        for (t in pending) {
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(CARD)
                setPadding(dp(14),dp(12),dp(14),dp(12))
                layoutParams = LinearLayout.LayoutParams(-1,-2).apply { bottomMargin = dp(6) }
            }
            item.addView(textView("o  ${t.title}", 13f, TEXT))
            root.addView(item)
        }
        scroll.addView(root)
        contentFrame.addView(scroll)
    }

    // ── TO-DO ─────────────────────────────────────────────────────────────────
    private fun buildTodos() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
            layoutParams = FrameLayout.LayoutParams(-1,-1)
        }
        root.addView(textView("To-Do Lists", 22f, TEXT, bold = true).apply { setPadding(dp(20),dp(20),dp(20),dp(12)) })

        val inputRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16),0,dp(16),dp(12))
        }
        val et = editText("Nova tarefa...")
        et.layoutParams = LinearLayout.LayoutParams(0,-2,1f).apply { marginEnd = dp(8) }
        val btnAdd = accentButton("+ Adicionar")
        inputRow.addView(et); inputRow.addView(btnAdd)
        root.addView(inputRow)

        val rv = RecyclerView(this).apply {
            setBackgroundColor(BG)
            layoutManager = LinearLayoutManager(this@MainActivity)
            setPadding(dp(16),0,dp(16),dp(16))
        }
        val todos = DataManager.getTodos(this)
        val adapter = TodoAdapter(todos,
            onToggle = { item ->
                item.done = !item.done
                DataManager.saveTodos(this, todos)
                rv.adapter?.notifyDataSetChanged()
            },
            onDelete = { item ->
                todos.remove(item)
                DataManager.saveTodos(this, todos)
                rv.adapter?.notifyDataSetChanged()
            }
        )
        rv.adapter = adapter
        root.addView(rv)

        btnAdd.setOnClickListener {
            val title = et.text.toString().trim()
            if (title.isEmpty()) return@setOnClickListener
            todos.add(0, TodoItem(title = title))
            DataManager.saveTodos(this, todos)
            et.setText("")
            adapter.notifyDataSetChanged()
        }
        contentFrame.addView(root)
    }

    // ── MISSIONS ──────────────────────────────────────────────────────────────
    private fun buildMissions() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
            layoutParams = FrameLayout.LayoutParams(-1,-1)
        }
        root.addView(textView("Missoes", 22f, TEXT, bold = true).apply { setPadding(dp(20),dp(20),dp(20),dp(12)) })

        val form = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16),0,dp(16),dp(12)) }
        val etName = editText("Nome da missao...")
        val etDesc = editText("Descricao (opcional)...")
        etDesc.layoutParams = LinearLayout.LayoutParams(-1,-2).apply { topMargin = dp(8); bottomMargin = dp(8) }
        val btnAdd = accentButton("+ Adicionar Missao")
        form.addView(etName); form.addView(etDesc); form.addView(btnAdd)
        root.addView(form)

        val rv = RecyclerView(this).apply {
            setBackgroundColor(BG)
            layoutManager = LinearLayoutManager(this@MainActivity)
            setPadding(dp(16),0,dp(16),dp(16))
        }
        val missions = DataManager.getMissions(this)
        val adapter = MissionAdapter(missions,
            onToggle = { m ->
                m.done = !m.done
                DataManager.saveMissions(this, missions)
                rv.adapter?.notifyDataSetChanged()
            },
            onDelete = { m ->
                missions.remove(m)
                DataManager.saveMissions(this, missions)
                rv.adapter?.notifyDataSetChanged()
            }
        )
        rv.adapter = adapter
        root.addView(rv)

        btnAdd.setOnClickListener {
            val title = etName.text.toString().trim()
            if (title.isEmpty()) return@setOnClickListener
            missions.add(0, Mission(title = title, desc = etDesc.text.toString().trim()))
            DataManager.saveMissions(this, missions)
            etName.setText(""); etDesc.setText("")
            adapter.notifyDataSetChanged()
        }
        contentFrame.addView(root)
    }

    // ── DAILY ─────────────────────────────────────────────────────────────────
    private fun buildDaily() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
            layoutParams = FrameLayout.LayoutParams(-1,-1)
        }
        root.addView(textView("Tarefas Diarias", 22f, TEXT, bold = true).apply { setPadding(dp(20),dp(20),dp(20),dp(4)) })
        root.addView(textView("Resetam todo dia. Com alarme de horario!", 11f, TEXT2).apply { setPadding(dp(20),0,dp(20),dp(12)) })

        val form = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16),0,dp(16),dp(12)) }
        val etName  = editText("Ex: Beber agua, Estudar...")
        val etAlarm = editText("Alarme HH:MM (ex: 08:30)")
        etAlarm.layoutParams = LinearLayout.LayoutParams(-1,-2).apply { topMargin = dp(8); bottomMargin = dp(8) }
        val btnAdd = accentButton("+ Adicionar Tarefa")
        form.addView(etName); form.addView(etAlarm); form.addView(btnAdd)
        root.addView(form)

        val rv = RecyclerView(this).apply {
            setBackgroundColor(BG)
            layoutManager = LinearLayoutManager(this@MainActivity)
            setPadding(dp(16),0,dp(16),dp(16))
        }
        val tasks = DataManager.getDailyTasks(this)
        val today = LocalDate.now().toString()
        val adapter = DailyAdapter(tasks, today,
            onToggle = { t ->
                t.doneDate = if (t.doneDate == today) null else today
                DataManager.saveDailyTasks(this, tasks)
                rv.adapter?.notifyDataSetChanged()
            },
            onDelete = { t ->
                tasks.remove(t)
                DataManager.saveDailyTasks(this, tasks)
                rv.adapter?.notifyDataSetChanged()
            }
        )
        rv.adapter = adapter
        root.addView(rv)

        btnAdd.setOnClickListener {
            val title = etName.text.toString().trim()
            if (title.isEmpty()) return@setOnClickListener
            val alarm = etAlarm.text.toString().trim()
            val task = DailyTask(title = title, alarm = alarm)
            tasks.add(0, task)
            DataManager.saveDailyTasks(this, tasks)
            if (alarm.isNotEmpty()) scheduleAlarm(task)
            etName.setText(""); etAlarm.setText("")
            adapter.notifyDataSetChanged()
        }
        contentFrame.addView(root)
    }

    // ── Alarm scheduling ──────────────────────────────────────────────────────
    private fun scheduleAlarm(task: DailyTask) {
        try {
            val parts = task.alarm.split(":")
            if (parts.size != 2) return
            val h = parts[0].trim().toInt()
            val m = parts[1].trim().toInt()
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
            }
            val intent = Intent(this, AlarmReceiver::class.java).apply {
                putExtra("title", task.title)
            }
            val pi = PendingIntent.getBroadcast(this, task.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
        } catch (e: Exception) { e.printStackTrace() }
    }

    // ── View helpers ──────────────────────────────────────────────────────────
    private fun textView(text: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        this.text = text; textSize = size; setTextColor(color)
        if (bold) typeface = android.graphics.Typeface.DEFAULT_BOLD
        layoutParams = LinearLayout.LayoutParams(-2, -2)
    }

    private fun editText(hint: String) = EditText(this).apply {
        this.hint = hint; setHintTextColor(TEXT2); setTextColor(TEXT)
        setBackgroundColor(CARD); setPadding(dp(14), dp(12), dp(14), dp(12))
        textSize = 14f; layoutParams = LinearLayout.LayoutParams(-1, -2)
    }

    private fun accentButton(label: String) = Button(this).apply {
        text = label; setBackgroundColor(ACCENT); setTextColor(Color.WHITE); textSize = 14f
        layoutParams = LinearLayout.LayoutParams(-1, dp(46))
    }
}

// ── Adapters ──────────────────────────────────────────────────────────────────
class TodoAdapter(
    private val items: MutableList<TodoItem>,
    private val onToggle: (TodoItem) -> Unit,
    private val onDelete: (TodoItem) -> Unit
) : RecyclerView.Adapter<TodoAdapter.VH>() {

    private val CARD   = Color.parseColor("#1E1E2E")
    private val ACCENT = Color.parseColor("#7C6AF7")
    private val GREEN  = Color.parseColor("#4ADE80")
    private val RED    = Color.parseColor("#F87171")
    private val TEXT   = Color.parseColor("#E2E8F0")
    private val TEXT2  = Color.parseColor("#94A3B8")

    inner class VH(val row: LinearLayout) : RecyclerView.ViewHolder(row)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val ctx = parent.context
        val dp = { v: Int -> (v * ctx.resources.displayMetrics.density).toInt() }
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(CARD)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(6) }
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        return VH(row)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val ctx = holder.row.context
        val dp = { v: Int -> (v * ctx.resources.displayMetrics.density).toInt() }
        holder.row.removeAllViews()

        val chk = TextView(ctx).apply {
            text = if (item.done) "[x]" else "[ ]"
            textSize = 16f
            setTextColor(if (item.done) GREEN else TEXT2)
            setPadding(0, 0, dp(12), 0)
            setOnClickListener { onToggle(item) }
        }

        val tv = TextView(ctx).apply {
            text = item.title
            textSize = 14f
            setTextColor(if (item.done) TEXT2 else TEXT)
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            if (item.done) paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        val del = TextView(ctx).apply {
            text = "X"
            textSize = 14f
            setTextColor(RED)
            setPadding(dp(12), 0, 0, 0)
            setOnClickListener { onDelete(item) }
        }

        holder.row.addView(chk)
        holder.row.addView(tv)
        holder.row.addView(del)
    }

    override fun getItemCount() = items.size
}

class MissionAdapter(
    private val items: MutableList<Mission>,
    private val onToggle: (Mission) -> Unit,
    private val onDelete: (Mission) -> Unit
) : RecyclerView.Adapter<MissionAdapter.VH>() {

    private val CARD   = Color.parseColor("#1E1E2E")
    private val ACCENT = Color.parseColor("#7C6AF7")
    private val GREEN  = Color.parseColor("#4ADE80")
    private val RED    = Color.parseColor("#F87171")
    private val TEXT   = Color.parseColor("#E2E8F0")
    private val TEXT2  = Color.parseColor("#94A3B8")
    private val BORDER = Color.parseColor("#2E2E3F")

    inner class VH(val row: LinearLayout) : RecyclerView.ViewHolder(row)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val ctx = parent.context
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(CARD)
            layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = (6 * ctx.resources.displayMetrics.density).toInt()
            }
        }
        return VH(row)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = items[position]
        val ctx = holder.row.context
        val dp = { v: Int -> (v * ctx.resources.displayMetrics.density).toInt() }
        holder.row.removeAllViews()

        val bar = View(ctx).apply {
            setBackgroundColor(if (m.done) GREEN else ACCENT)
            layoutParams = LinearLayout.LayoutParams(dp(4), -1)
        }

        val body = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        }
        val tvTitle = TextView(ctx).apply {
            text = m.title; textSize = 14f
            setTextColor(if (m.done) TEXT2 else TEXT)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            if (m.done) paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        body.addView(tvTitle)
        if (m.desc.isNotEmpty()) {
            body.addView(TextView(ctx).apply {
                text = m.desc; textSize = 11f; setTextColor(TEXT2)
            })
        }
        body.addView(TextView(ctx).apply {
            text = "Criada: ${m.created}"; textSize = 10f; setTextColor(BORDER)
        })

        val btns = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(dp(10), 0, dp(10), 0)
        }
        val chkBtn = TextView(ctx).apply {
            text = if (m.done) "Concluida" else "Concluir"
            textSize = 11f; setTextColor(if (m.done) GREEN else ACCENT)
            setOnClickListener { onToggle(m) }
        }
        val delBtn = TextView(ctx).apply {
            text = "Remover"; textSize = 11f; setTextColor(RED)
            setPadding(0, dp(6), 0, 0)
            setOnClickListener { onDelete(m) }
        }
        btns.addView(chkBtn); btns.addView(delBtn)

        holder.row.addView(bar); holder.row.addView(body); holder.row.addView(btns)
    }

    override fun getItemCount() = items.size
}

class DailyAdapter(
    private val items: MutableList<DailyTask>,
    private val today: String,
    private val onToggle: (DailyTask) -> Unit,
    private val onDelete: (DailyTask) -> Unit
) : RecyclerView.Adapter<DailyAdapter.VH>() {

    private val CARD   = Color.parseColor("#1E1E2E")
    private val GREEN  = Color.parseColor("#4ADE80")
    private val RED    = Color.parseColor("#F87171")
    private val YELLOW = Color.parseColor("#FACC15")
    private val TEXT   = Color.parseColor("#E2E8F0")
    private val TEXT2  = Color.parseColor("#94A3B8")
    private val ACCENT = Color.parseColor("#7C6AF7")

    inner class VH(val row: LinearLayout) : RecyclerView.ViewHolder(row)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val ctx = parent.context
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(CARD)
            setPadding(
                (14*ctx.resources.displayMetrics.density).toInt(), (12*ctx.resources.displayMetrics.density).toInt(),
                (14*ctx.resources.displayMetrics.density).toInt(), (12*ctx.resources.displayMetrics.density).toInt()
            )
            layoutParams = LinearLayout.LayoutParams(-1,-2).apply {
                bottomMargin = (6*ctx.resources.displayMetrics.density).toInt()
            }
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        return VH(row)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        val done = t.doneDate == today
        val ctx = holder.row.context
        val dp = { v: Int -> (v * ctx.resources.displayMetrics.density).toInt() }
        holder.row.removeAllViews()

        val info = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0,-2,1f)
        }
        val tvTitle = TextView(ctx).apply {
            text = t.title; textSize = 14f
            setTextColor(if (done) TEXT2 else TEXT)
            if (done) paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        info.addView(tvTitle)
        if (t.alarm.isNotEmpty()) {
            info.addView(TextView(ctx).apply {
                text = "Alarme: ${t.alarm}"; textSize = 11f
                setTextColor(if (done) GREEN else YELLOW)
                setPadding(0, dp(4), 0, 0)
            })
        }

        val btns = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(dp(8), 0, 0, 0)
        }
        val chkBtn = TextView(ctx).apply {
            text = if (done) "[x] Feita" else "[ ] Marcar"
            textSize = 11f; setTextColor(if (done) GREEN else ACCENT)
            setOnClickListener { onToggle(t) }
        }
        val delBtn = TextView(ctx).apply {
            text = "Remover"; textSize = 11f; setTextColor(RED)
            setPadding(0, dp(6), 0, 0)
            setOnClickListener { onDelete(t) }
        }
        btns.addView(chkBtn); btns.addView(delBtn)

        holder.row.addView(info); holder.row.addView(btns)
    }

    override fun getItemCount() = items.size
}
