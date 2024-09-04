package flwr.android_client
import android.Manifest
import android.Manifest.permission
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import dev.flower.flower_tflite.FlowerClient
import dev.flower.flower_tflite.FlowerServiceRunnable
import dev.flower.flower_tflite.SampleSpec
import dev.flower.flower_tflite.createFlowerService
import dev.flower.flower_tflite.helpers.classifierAccuracy
import dev.flower.flower_tflite.helpers.loadMappedAssetFile
import dev.flower.flower_tflite.helpers.negativeLogLikelihoodLoss
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private const val TAG = "MainActivity"
private var isFLEnabled = false
typealias FeatureArray = FloatArray

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val scope = MainScope()
    lateinit var flowerClient: FlowerClient<FeatureArray, FloatArray>
    lateinit var flowerServiceRunnable: FlowerServiceRunnable<FeatureArray, FloatArray>
    private lateinit var ip: EditText
    private lateinit var port: EditText
    private lateinit var loadDataButton: Button
    private lateinit var trainButton: Button
    private lateinit var resultText: TextView
    private lateinit var deviceId: EditText
    private lateinit var switchButton: Button
    private lateinit var engineStatusText: TextView
    private lateinit var viewFlipper: ViewFlipper
    lateinit var db: Db
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(this, Db::class.java, "model-db").build()
        isFLEnabled = false
        setContentView(R.layout.activity_main)
        resultText = findViewById(R.id.grpc_response_text)
        resultText.movementMethod = ScrollingMovementMethod()
        deviceId = findViewById(R.id.device_id_edit_text)
        ip = findViewById(R.id.serverIP)
        port = findViewById(R.id.serverPort)
        loadDataButton = findViewById(R.id.load_data)
        trainButton = findViewById(R.id.trainFederated)
        viewFlipper = findViewById(R.id.view_flipper)
        switchButton = findViewById(R.id.button_switch)
        engineStatusText = findViewById(R.id.engineStatusText)

        val switchButton: Button = findViewById(R.id.button_switch)
        switchButton.setOnClickListener {
            if (isFLEnabled) {
                viewFlipper.showNext()
                if (switchButton.text == "Go to FL Settings") {
                    switchButton.text = "Go to Main Screen"
                }
                else {
                    switchButton.text = "Go to FL Settings"
                }
            }
        }
        val activateFLSwitch: Switch = findViewById(R.id.switch1)
        activateFLSwitch.setOnCheckedChangeListener {_, isChecked ->
            isFLEnabled = isChecked
        }

        // Initialize the requestPermissionLauncher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("D","Permission granted! Starting getAndPrint... [after request]")
                getAndPrintEngineStatus()
            } else {
                Log.d("D","Permissions not granted!")
                Toast.makeText(this, "Cannot look for Engine Data as permission was not granted.", Toast.LENGTH_SHORT).show()
            }
        }
        val getEngineButton: Button = findViewById(R.id.getEngineButton)
        getEngineButton.setOnClickListener {
            Log.d("D","Engine Button Clicked!")
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                Log.d("D","Permission granted! Starting getAndPrint... [directly]")
                getAndPrintEngineStatus()
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d("D","Should show is true!")
                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("This app needs storage access to read Engine Data. Please grant the required permission.")
                    .setPositiveButton("Grant Permission") { dialog: DialogInterface?, which: Int ->
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton(
                        "Abort"
                    ) { dialog: DialogInterface?, which: Int ->
                        Log.d(TAG, "Permission request canceled by user")
                    }
                    .create()
                    .show()
            }
            else {
                Log.d("D","Launching permission request!")
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        createFlowerClient()
        scope.launch { restoreInput() }
    }

    private fun getAndPrintEngineStatus(){
        var engineStatus = 0
        val filePath = "/storage"
        try {
            Log.d("D", "Filepath is: "+ filePath)
            val file = File(filePath, "engineData.json")
            if (file.exists()) {
                val fileContent = file.readText()
                var egData = ReadEngineDataJSON(fileContent)
                egData = NormalizeSample(egData)
                if (isFLEnabled) {
                    val s = SampleSpec<FeatureArray, FloatArray>(
                        { it.toTypedArray() },
                        { it.toTypedArray() },
                        { Array(it) { FloatArray(CLASSES.size) } },
                        ::negativeLogLikelihoodLoss,
                        ::classifierAccuracy,
                    )
                    var legData = mutableListOf<FeatureArray>()
                    legData.add(egData)
                    Log.d("D","All clear! Making inference...")
                    val inferenceResult = flowerClient.inference(flowerClient.spec.convertX(legData))
                    val firstResult = inferenceResult[0]
                    var maxIndex = -1
                    var maxValue = Float.MIN_VALUE
                    for ((index, value) in firstResult.withIndex()){
                        Log.d("D", "Prediction for label $index = $value")
                        if (value > maxValue){
                            maxValue = value
                            maxIndex = index
                        }
                    }
                    engineStatus = maxIndex
                }
                else {
                    engineStatus = sendToServer(egData)
                }
                if (engineStatus == 0){
                    engineStatusText.text = "Engine Status: All checks OK. Normal Operating Conditions."
                    engineStatusText.setTextColor(Color.parseColor("#00DD00"))
                }
                else if (engineStatus == 1) {
                    engineStatusText.text = "Engine Status: Rich Mixture detected. Fault code: 1."
                    engineStatusText.setTextColor(Color.parseColor("#DD0000"))
                }
                else if (engineStatus == 2) {
                    engineStatusText.text = "Engine Status: Lean Mixture detected. Fault code: 2."
                    engineStatusText.setTextColor(Color.parseColor("#DD0000"))
                }
                else if (engineStatus == 3) {
                    engineStatusText.text = "Engine Status: Low voltage detected. Fault code: 3."
                    engineStatusText.setTextColor(Color.parseColor("#DD0000"))
                }
                else {
                    engineStatusText.text = "Engine Status: Unknown fault detected. Fault code: 1337"
                    engineStatusText.setTextColor(Color.parseColor("#DD0000"))
                }
            } else {
                Toast.makeText(this, "Cannot find engine data.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendToServer(engineData: FloatArray) : Int{
        /* This function is only called if data needs to be sent to the server,
         * i.e., the user did not consent to Federated Learning
         * At the moment, it just simulates the reply from the server, generating it randomly
         */
        return Random.nextInt(0, 4)
    }

    private fun createFlowerClient() {
        val buffer = loadMappedAssetFile(this, "model/enginefaultdb.tflite")
        val layersSizes = intArrayOf(504, 36, 144, 16)
        val sampleSpec = SampleSpec<FeatureArray, FloatArray>(
            { it.toTypedArray() },
            { it.toTypedArray() },
            { Array(it) { FloatArray(CLASSES.size) } },
            ::negativeLogLikelihoodLoss,
            ::classifierAccuracy,
        )
        flowerClient = FlowerClient(buffer, layersSizes, sampleSpec)
    }

    suspend fun restoreInput() {
        val input = db.inputDao().get() ?: return
        runOnUiThread {
            deviceId.text.append(input.device_id)
            ip.text.append(input.ip)
            port.text.append(input.port)
        }
    }

    fun setResultText(text: String) {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.GERMANY)
        val time = dateFormat.format(Date())
        resultText.append("\n$time   $text")
    }

    suspend fun runWithStacktrace(call: suspend () -> Unit) {
        try {
            call()
        } catch (err: Error) {
            Log.e(TAG, Log.getStackTraceString(err))
        }
    }

    suspend fun <T> runWithStacktraceOr(or: T, call: suspend () -> T): T {
        return try {
            call()
        } catch (err: Error) {
            Log.e(TAG, Log.getStackTraceString(err))
            or
        }
    }

    fun loadData(@Suppress("UNUSED_PARAMETER") view: View) {
        if (deviceId.text.isEmpty() || !(1..3).contains(deviceId.text.toString().toInt())) {
            Toast.makeText(
                this,
                "Please enter a client partition ID between 1 and 3 (inclusive)",
                Toast.LENGTH_LONG
            ).show()
        } else {
            hideKeyboard()
            setResultText("Loading the local training dataset in memory. It will take several seconds.")
            deviceId.isEnabled = false
            loadDataButton.isEnabled = false
            scope.launch {
                loadDataInBackground()
            }
            scope.launch {
                db.inputDao().upsertAll(
                    Input(
                        1,
                        deviceId.text.toString(),
                        ip.text.toString(),
                        port.text.toString()
                    )
                )
            }
        }
    }

    suspend fun loadDataInBackground() {
        val result = runWithStacktraceOr("Failed to load training dataset.") {
            loadData(this, flowerClient, deviceId.text.toString().toInt())
            "Training dataset is loaded in memory. Ready to train!"
        }
        runOnUiThread {
            setResultText(result)
            trainButton.isEnabled = true
        }
    }

    fun runGrpc(@Suppress("UNUSED_PARAMETER") view: View) {
        val host = ip.text.toString()
        val portStr = port.text.toString()
        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(portStr) || !Patterns.IP_ADDRESS.matcher(
                host
            ).matches()
        ) {
            Toast.makeText(
                this,
                "Please enter the correct IP and port of the FL server",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val port = if (TextUtils.isEmpty(portStr)) 0 else portStr.toInt()
            scope.launch {
                runWithStacktrace {
                    runGrpcInBackground(host, port)
                }
            }
            hideKeyboard()
            ip.isEnabled = false
            this.port.isEnabled = false
            trainButton.isEnabled = false
            setResultText("Started training.")
        }
    }

    suspend fun runGrpcInBackground(host: String, port: Int) {
        val address = "dns:///$host:$port"
        val result = runWithStacktraceOr("Failed to connect to the FL server \n") {
            flowerServiceRunnable = createFlowerService(address, false, flowerClient) {
                runOnUiThread {
                    setResultText(it)
                }
            }
            "Connection to the FL server successful \n"
        }
        runOnUiThread {
            setResultText(result)
            trainButton.isEnabled = false
        }
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}