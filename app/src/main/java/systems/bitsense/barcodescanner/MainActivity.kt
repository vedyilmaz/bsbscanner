package systems.bitsense.barcodescanner

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var scannedResult: String = ""
    var defaultUri: String = ""
    var DEFAULT_URI_KEY = "uri"
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addListeners()

        val sharedPref: Utils = Utils(this)

        if (sharedPref.checkKey(DEFAULT_URI_KEY)) {
            defaultUri = sharedPref.getValueString(DEFAULT_URI_KEY).toString()
            til_barcode.setHint(defaultUri)
        }

        btn_clear.setOnClickListener {
            et_barcode.setText("")
        }

        initMobAd()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_main, menu)

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_share -> {
//            shareApp(R.string.app_share_msg.toString())

            if(!et_barcode.text.toString().equals(R.string.err_scan_failed.toString(),true) &&
                !et_barcode.text.toString().equals("")){
                    shareBarcode("new scanned barcode", et_barcode.text.toString())
                    true
            }else{
                Toast.makeText(this, R.string.msg_no_data, Toast.LENGTH_LONG).show()
                true
            }
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
                scannedResult =  et_barcode.text.toString()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                et_barcode.setText(result.contents)
            } else {
                et_barcode.setText(R.string.err_scan_failed)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun shareApp(shareSubject: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
            var shareMessage = "\nGive this password manager app a try - :\n\n"
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share Using"))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    fun shareBarcode(shareSubject: String?, barcode: String?) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, barcode)
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    resources.getString(R.string.share_barcode)
                )
            )
        } catch (e: java.lang.Exception) {
            //e.toString();
        }
    }

    private fun initMobAd() {
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        mAdView = findViewById(R.id.adv_main)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }
}
