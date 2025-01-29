package com.example.tasklist

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private val tasks = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FloatingActionButton
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            addNewTask()
        }

        // ListView и адаптер
        val listView: ListView = findViewById(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            showDeleteTaskDialog(position)
            true
        }

        loadTasks()
    }

    override fun onPause() {
        super.onPause()
        saveTasks()
    }

    private fun addNewTask() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Новая задача")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val title = input.text.toString()
                if (title.isNotEmpty()) {
                    tasks.add(title)
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteTaskDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Удалить задачу?")
            .setMessage("Вы уверены, что хотите удалить эту задачу?")
            .setPositiveButton("Удалить") { _, _ ->
                tasks.removeAt(position)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("tasks_pref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val tasksJson = Gson().toJson(tasks)
        editor.putString("tasks", tasksJson)
        editor.apply()
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("tasks_pref", MODE_PRIVATE)
        val tasksJson = sharedPreferences.getString("tasks", "[]")
        val type = object : TypeToken<List<String>>() {}.type
        val loadedTasks: List<String> = Gson().fromJson(tasksJson, type)

        tasks.clear()
        tasks.addAll(loadedTasks)
    }
}





