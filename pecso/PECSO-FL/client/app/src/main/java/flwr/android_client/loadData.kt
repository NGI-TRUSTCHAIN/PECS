package flwr.android_client

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import dev.flower.flower_tflite.FlowerClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ExecutionException
import org.json.JSONArray
import org.json.JSONObject

suspend fun readAssetLines(
    context: Context,
    fileName: String,
    call: suspend (Int, String) -> Unit
) {
    withContext(Dispatchers.IO) {
        BufferedReader(InputStreamReader(context.assets.open(fileName))).useLines {
            it.forEachIndexed { i, l -> launch { call(i, l) } }
        }
    }
}

/**
 * Load training data from disk.
 *
 * If you want to use raw EngineFaultDB parameters as input,
 * you can run a MinMaxScaler with these parameters to normalize the dataset:
 *
 * Minimum values for each feature: [4.530000e-01 3.820000e-01 2.580000e+00 4.650000e-01
 *  1.066452e+03 1.917000e+00 5.187000e+00 2.275700e+01 4.210000e-01
 *  1.787000e+00 8.649000e+00 2.030000e-01 6.950000e-01 1.021000e+01]
 *
 * Maximum values for each feature: [4.547000e+00 4.048000e+00 1.537118e+03 3.394600e+01
 *  5.013402e+03 1.481000e+01 2.004300e+01 1.075390e+02 1.013200e+01
 *  9.756570e+02 1.512900e+01 1.151000e+00 1.149000e+00 1.689300e+01]
 */
@Throws
suspend fun loadData(
    context: Context,
    flowerClient: FlowerClient<FeatureArray, FloatArray>,
    device_id: Int
) {
    if(device_id == 0) {
        readAssetLines(context, "data/train_1.csv") { index, line ->
            addSample(context, flowerClient, line, true)
        }
        readAssetLines(context, "data/test_1.csv") { index, line ->
            addSample(context, flowerClient, line, false)
        }
    } else if(device_id == 1) {
        readAssetLines(context, "data/train_2.csv") { index, line ->
            addSample(context, flowerClient, line, true)
        }
        readAssetLines(context, "data/test_1.csv") { index, line ->
            addSample(context, flowerClient, line, false)
        }
    } else if (device_id == 2) {
        readAssetLines(context, "data/test_1.csv") { index, line ->
            addSample(context, flowerClient, line, false)
        }
    }
}

@Throws
private fun addSample(
    context: Context,
    flowerClient: FlowerClient<FeatureArray, FloatArray>,
    row: String,
    isTraining: Boolean
) {
    val parts = row.split(",".toRegex())
    val index = parts[0].toInt()
    val className = CLASSES[index]
    val labelArray = classToArray(className)
    val features = parts.subList(1, parts.size).map { it.toFloat() }.toFloatArray()

    // add to the list.
    try {
        flowerClient.addSample(features, labelArray, isTraining)
    } catch (e: ExecutionException) {
        throw RuntimeException("Failed to add sample to model", e.cause)
    } catch (e: InterruptedException) {
        // no-op
    }
}

fun classToArray(className: String): FloatArray {
    return CLASSES.map {
        if (className == it) 1f else 0f
    }.toFloatArray()
}

// Normalizes one sample with MinMax Scaling calculated on the whole EngineFaultDB.
// If you generate new data that does not come from the dataset, this function will also make sure you cannot
// pass values that would yield abnormal values (i.e., lower than 0 or higher than 1)
fun NormalizeSample(input: FeatureArray): FeatureArray {
    val mins = floatArrayOf(0.453f, 0.382f, 2.58f, 0.465f, 1066.452f, 1.917f, 5.187f, 22.757f, 0.421f, 1.787f, 8.649f, 0.203f, 0.695f, 10.21f)
    val maxs = floatArrayOf(4.547f, 4.048f, 1537.118f, 33.946f, 5013.402f, 14.81f, 20.043f, 107.539f, 10.132f, 975.657f, 15.129f, 1.151f, 1.149f, 16.893f)
    val result = FloatArray(INPUT_LAYER_SIZE)
    for (i in 0 until INPUT_LAYER_SIZE) {
        if (input[i] < mins[i]) {
            input[i] = mins[i]
        }
        else if (input[i] > maxs[i]) {
            input[i] = maxs[i]
        }
        result[i] = (input[i] - mins[i]) / (maxs[i] - mins[i])
    }
    return result
}

fun ReadEngineDataJSON(jsonString: String): FeatureArray {
    //val jsonArray = JSONArray(jsonString)
    val jsonObj = JSONObject(jsonString)
    var result = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val engineData = jsonObj.getJSONArray("engineData")
    for (j in 0 until engineData.length()) {
        val dataItem = engineData.getJSONObject(j)
        if (dataItem.getString("name") == "maf-pressure") {
            result[0] = dataItem.getDouble("value").toFloat()
            Log.d("D", "Got maf-pressure: ${result[0]}")
        }
        else if (dataItem.getString("name") == "throttle-position") {
            result[1] = dataItem.getDouble("value").toFloat()
            Log.d("D", "Got throttle-position: ${result[1]}")
        }
        else if (dataItem.getString("name") == "???? Force") {
            result[2] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "???? Power") {
            result[3] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "rpm") {
            result[4] = dataItem.getDouble("value").toFloat()
            Log.d("D", "Got rpm: ${result[4]}")
        }
        else if (dataItem.getString("name") == "???? Consumption L/H") {
            result[5] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "???? Consumption L/100KM") {
            result[6] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "vehicle-speed") {
            result[7] = dataItem.getDouble("value").toFloat()
            Log.d("D", "Got vehicle-speed: ${result[7]}")
        }
        else if (dataItem.getString("name") == "???? CO") {
            result[8] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "???? HC") {
            result[9] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "???? CO2") {
            result[10] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "O2-sensors") {
            result[11] = dataItem.getDouble("value").toFloat()
            Log.d("D", "Got O2-sensors: ${result[11]}")
        }
        else if (dataItem.getString("name") == "???? Lambda") {
            result[12] = dataItem.getDouble("value").toFloat()
        }
        else if (dataItem.getString("name") == "air-fuel-ratio") {
            result[13] = dataItem.getDouble("value").toFloat()
            Log.d("D", "Got air-fuel-ratio: ${result[13]}")

        }
    }
    return result
}

const val INPUT_LAYER_SIZE = 14

val CLASSES = listOf(
    "NO FAULT",
    "RICH MIXTURE",
    "LEAN MIXTURE",
    "LOW VOLTAGE",
)

val FEATURES = listOf(
    "Fault",
    "MAP",
    "TPS",
    "Force",
    "Power",
    "RPM",
    "Consumption L/H",
    "Consumption L/100KM",
    "Speed",
    "CO",
    "HC",
    "CO2",
    "O2",
    "Lambda",
    "AFR"
)