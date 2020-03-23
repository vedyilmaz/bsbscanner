package systems.bitsense.barcodescanner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    var DEFAULT_URI_KEY = "uri"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPreference:Utils=Utils(this)

        tv_saved_uri.setText(sharedPreference.getValueString(DEFAULT_URI_KEY))

        btn_save.setOnClickListener{

            val default_uri = et_default_uri.text.toString()
            sharedPreference.save(DEFAULT_URI_KEY,default_uri)
            Toast.makeText(this@SettingsActivity,R.string.msg_data_stored,Toast.LENGTH_SHORT).show()
        }

    }


}
