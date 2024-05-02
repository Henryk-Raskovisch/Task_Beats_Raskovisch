package com.example.taskbeatsraskovisch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class TaskDetailActivity : AppCompatActivity() {

    private var task: Task? = null
    private lateinit var btnDone: Button

    companion object{
        private const val TASK_DETAIL_EXTRA = "task.extra.detail"

        fun start(context: Context, task: Task?): Intent{
            val intent = Intent(context, TaskDetailActivity::class.java)
                .apply {
                    putExtra(TASK_DETAIL_EXTRA, task)
                }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        // Recover task
        task = intent.getSerializableExtra(TASK_DETAIL_EXTRA) as Task?

        val edtTitle = findViewById<EditText>(R.id.edt_task_title)
        val edtDescription = findViewById<EditText>(R.id.edt_task_description)
        btnDone = findViewById<Button>(R.id.btn_done)

        if (task != null) {
            edtTitle.setText(task!!.title)
            edtDescription.setText(task!!.description)
        }

        btnDone.setOnClickListener{
            val title = edtTitle.text.toString()
            val description = edtDescription.text.toString()

            if(title.isNotEmpty() && description.isNotEmpty()){
                if (task == null) {
                    addOrUpdateTask(0, title, description, ActionType.CREATE)
                }else{
                    addOrUpdateTask(task!!.id, title, description, ActionType.UPDATE)
                }

            }else{
                showMessage(it, "Fields are required")
            }
        }

        //Recover field from XML
        //showTitle = findViewById(R.id.task_title_detail)

        //Set a new text on the screen
        //showTitle.text = task?.title?: "Add a task"
    }

    private fun addOrUpdateTask(
        id: Int,
        title: String,
        description: String,
        actionType: ActionType
    ) {
        val task = Task(id, title, description)
        returnAction(task, actionType)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_task -> {
                if(task != null){
                    returnAction(task!!, ActionType.DELETE)
                }else {
                    showMessage(btnDone, "Item not found")
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun returnAction(task: Task, actionType: ActionType){
        val intent = Intent()
            .apply {
                val taskAction = TaskAction(task, actionType.name)
                putExtra(TASK_ACTION_RESULT, taskAction)
            }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

}