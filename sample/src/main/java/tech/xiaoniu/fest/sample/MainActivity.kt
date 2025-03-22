package tech.xiaoniu.fest.sample

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tech.xiaoniu.fest.Fest

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    val textView: TextView by lazy { findViewById(R.id.textView) }

    val httpServer by lazy { Fest.getBean(HttpServer::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textView.setOnClickListener {
            httpServer.sayHi(this)
        }

        httpServer.start()
    }
}