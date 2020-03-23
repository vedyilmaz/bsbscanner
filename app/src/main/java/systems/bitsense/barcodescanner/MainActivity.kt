package systems.bitsense.barcodescanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.UnsupportedOperationException

class MainActivity : AppCompatActivity() {

    var scannedResult: String = ""
    var defaultUri: String = ""
    var DEFAULT_URI_KEY = "uri"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addListeners()

        val sharedPref: Utils = Utils(this)

        if (sharedPref.checkKey(DEFAULT_URI_KEY)) {
            defaultUri = sharedPref.getValueString(DEFAULT_URI_KEY).toString()
            til_barcode.setHint(defaultUri)
            loadWebPage(defaultUri)
        }else{
            Toast.makeText(this@MainActivity,R.string.msg_no_default_uri,Toast.LENGTH_SHORT).show()
        }

        btn_uri.setOnClickListener {
            Toast.makeText(this@MainActivity,"scanned barcode:" + et_barcode.text.toString(),Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_main, menu)

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
//            msgShow("Settings")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.action_barcode -> {
            run {
                IntentIntegrator(this@MainActivity).initiateScan();
            }
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun msgShow(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState?.putString("scannedResult", scannedResult)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            scannedResult = it.getString("scannedResult")
            et_barcode.setText(scannedResult)
        }
    }

    fun addListeners() {
        et_barcode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                scannedResult =  removeUriPref(et_barcode.text.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                et_barcode.setText(removeUriPref(result.contents))
            } else {
                et_barcode.setText("scan failed")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Throws(UnsupportedOperationException::class)
    fun buildUri(myuri: String): Uri{
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority(myuri)

        return builder.build()
    }

    fun removeUriPref(uriPref: String) : String {

        return uriPref.replace("http://","")
            .replace("https://", "")
    }

    fun loadWebPage(def_uri: String){
        val product : String = et_barcode.text.toString()
        webview.loadUrl("")
        val uri: Uri

        try {
            uri = buildUri(def_uri)
            Toast.makeText(this@MainActivity, uri.toString(),Toast.LENGTH_SHORT).show()
            webview.loadUrl(uri.toString())
        }catch (e: UnsupportedOperationException){
            e.printStackTrace()
        }
    }
}
