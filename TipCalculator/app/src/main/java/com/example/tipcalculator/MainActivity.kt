package com.example.tipcalculator

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Configuration.UI_MODE_NIGHT_NO
//                Configuration.UI_MODE_NIGHT_UNDEFINED
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TipCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun TipCalculatorScreen(){
    val image = painterResource(R.drawable.barcode_laser_code_vector_graphic_pixabay_3)
    var costInput by remember { mutableStateOf("") }
    val cost = costInput.toDoubleOrNull() ?: 0.0

    var tipInput by remember { mutableStateOf("") }
    var roundUp by remember { mutableStateOf(false) }
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(cost, tipPercent, roundUp)

    val totalBill = calculateTotalBill(cost, tip)

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        TipCalculatorTitle(modifier = Modifier.align(Alignment.CenterHorizontally))

        EditNumberField(
            label = R.string.bill_amount,
            value = costInput,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            onValueChange = { costInput = it })

        EditNumberField(
            label = R.string.how_was_the_service,
            value = tipInput,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            onValueChange = { tipInput = it }
        )

        RoundTheTipRow(
            roundUp = roundUp,
            onRoundUpChanged = { roundUp = it })

        Spacer(modifier = Modifier.weight(1.0f))

        Divider(
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
                .padding(start = 20.dp, end = 20.dp)
        )

        Row(modifier = Modifier
            .align(Alignment.Start)
            .padding(top = 24.dp),){
            Text(
                text = stringResource(R.string.tip_amount),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Text(
                text = NumberFormat.getCurrencyInstance().format(tip),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Row(modifier = Modifier
            .align(Alignment.Start)
            .padding(bottom = 24.dp),){
            Text(
                text = stringResource(R.string.total_amount),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Text(
                text = totalBill,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = stringResource(R.string.thank_you),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Image(painter = image,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 30.dp),
            contentScale = ContentScale.FillWidth)
    }
    Row(verticalAlignment = Alignment.Bottom){
        repeat(30) {
            RecieptBottom(MaterialTheme.colors.secondary)
        }
    }
}

@Composable
fun TipCalculatorTitle(
    modifier: Modifier,
){
    Text(
        text = stringResource(R.string.calculate_tip),
        fontSize = 36.sp,
        fontWeight = Bold,
        modifier = modifier,
    )

    Divider(
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .width(1.dp)
            .padding(start = 20.dp, end = 20.dp)
    )

    Spacer(Modifier.height(16.dp))
}

@Composable
fun EditNumberField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {


    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        singleLine = true,
        keyboardOptions =  keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.round_up_tip),
            fontSize = 16.sp,
        )

        Switch(
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray,
                checkedThumbColor = MaterialTheme.colors.primary
//                Purple700
            ),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
        )
    }
}

@Composable
fun RecieptBottom(color: Color){
    Canvas(modifier =
    Modifier
        .width(25.dp)
        .height(35.dp)) {
            val path = Path()
            val halfWidth = size.width/2
        path.moveTo(halfWidth, 0f)
        path.lineTo(0f,size.height)
        path.lineTo(size.width, size.height)
        path.lineTo(halfWidth, 0f)

        drawPath(
            path = path,
            //brush = SolidColor(Color.LightGray)
            brush = SolidColor(color)
        )
    }
}
@VisibleForTesting
internal fun calculateTip(
    amount: Double,
    tipPercent: Double = 15.0,
    roundUp: Boolean
): Double {
    var tip = tipPercent / 100 * amount
    if (roundUp)
        tip = kotlin.math.ceil(tip)
    return tip
}

private fun calculateTotalBill(
    bill: Double,
    tip: Double = 0.0,
): String {
    var total = bill + tip
    return NumberFormat.getCurrencyInstance().format(total)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipCalculatorScreenPreview() {
    TipCalculatorTheme {
        TipCalculatorScreen()
    }
}