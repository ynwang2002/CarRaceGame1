package tw.edu.pu.s1090350.carracegame

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import tw.edu.pu.s1090350.carracegame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), GameTask {
    lateinit var rootLayout: LinearLayout
    lateinit var startBtn: Button
    lateinit var mGameView: GameView
    lateinit var score: TextView
    lateinit var name: EditText
    lateinit var num: Button
    lateinit var updata : Button
    var db = FirebaseFirestore.getInstance()
    lateinit var binding: ActivityMainBinding
    var user: MutableMap<String, Any> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startBtn = findViewById(R.id.startBtn)
        rootLayout = findViewById(R.id.rootLayout)
        score = findViewById(R.id.score)
        name = findViewById(R.id.edt)
        num = findViewById(R.id.num)
        updata =findViewById(R.id.updata)
        mGameView = GameView(this, this)

        //開始遊戲
        startBtn.setOnClickListener {
            mGameView.setBackgroundResource(R.drawable.road)
            rootLayout.addView(mGameView)
            startBtn.visibility = View.GONE
            score.visibility = View.GONE
            num.visibility = View.GONE
            name.visibility = View.GONE

            updata.visibility = View.GONE

        }
        //排行榜
        binding.num.setOnClickListener ({
            db.collection("BS")
                //.whereLessThan("分數", 19)
                .orderBy("分數", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var msg: String = ""
                        for (document in task.result!!) {
                            msg += "名字：" + document.data["名字"] +
                                    "   分數：" + document.data["分數"].toString()+"\n"
                        }
                        if (msg != "") {
                            val build =AlertDialog.Builder(this)
                            build.setTitle("成績紀錄\n(依照上傳時間排列)\n\n")
                            build.setMessage(msg)
                            build.setNegativeButton("關閉",null)
                            build.show()
                        } else {
                            val build =AlertDialog.Builder(this)
                            build.setTitle("成績紀錄")
                            build.setMessage("查無資料")
                            build.setNegativeButton("關閉",null)
                            build.show()
                        }
                    }
                }
        })
        //上傳資料
        binding.updata.setOnClickListener({
            var str = binding.score.text.toString()
            str=str.removeRange(0,5)


            user["名字"] = binding.edt.text.toString()
            user["分數"] = str.toInt()
            db.collection("BS")
                //.document(binding.edt.text.toString())
                //.set(user)
                .add(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "新增資料成功",
                        Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "新增資料失敗：" + e.toString(),
                        Toast.LENGTH_LONG).show()
                }

        })

    }

    override fun closeGame(mScore: Int) {
        score.text = "分數 ： $mScore"
        rootLayout.removeView(mGameView)
        startBtn.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        num.visibility = View.VISIBLE
        name.visibility = View.VISIBLE
        updata.visibility = View.VISIBLE
    }



}