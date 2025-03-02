package com.example.calculatorkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("light") { CalculatorApp(navController) }
        composable("dark") { DarkTheme(navController) }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // نمایش اسپلش برای ۲ ثانیه
        isVisible = false
        navController.navigate("light") // رفتن به صفحه اصلی
    }

    AnimatedVisibility(visible = isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFDDE9FF)), // رنگ پس‌زمینه اسپلش
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.icon),
                contentDescription = "App Icon",
                modifier = Modifier.size(250.dp) // اندازه دلخواه
            )
        }
    }

}


@Composable
fun CalculatorApp(navController: NavController) {

    BackHandler {
        System.exit(0)
    }

    val bold = FontFamily(Font(R.font.nunitobold))
    val regular = FontFamily(Font(R.font.nunitoregular))

    var displayText by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf("") }
    var secondOperand by remember { mutableStateOf("") }
    var currentOperator by remember { mutableStateOf("") }
    var shouldReset by remember { mutableStateOf(false) }
    var isOffClicked by remember { mutableStateOf(false) }

    fun handleButtonClick(label: String) {
        when (label) {
            "Off" -> {
                isOffClicked = !isOffClicked
                if(isOffClicked) {
                    firstOperand = ""
                    secondOperand = ""
                    currentOperator = ""
                    displayText = ""
                } else {
                    firstOperand = ""
                    secondOperand = ""
                    currentOperator = ""
                    displayText = "0"
                }
            }
            "C" -> {
                firstOperand = ""
                secondOperand = ""
                currentOperator = ""
                displayText = "0"
            }
            "←" -> {
                displayText = if (displayText.length > 1) displayText.dropLast(1) else "0"
            }
            "÷", "×", "−", "+" -> {
                if (firstOperand.isNotEmpty() && secondOperand.isNotEmpty()) {
                    displayText = calculateResult(firstOperand, secondOperand, currentOperator)
                    firstOperand = displayText
                    secondOperand = ""
                }
                currentOperator = label
                firstOperand = displayText
                shouldReset = true
            }
            "=" -> {
                if (firstOperand.isNotEmpty() && secondOperand.isNotEmpty()) {
                    displayText = calculateResult(firstOperand, secondOperand, currentOperator)
                    firstOperand = displayText
                    secondOperand = ""
                    currentOperator = ""
                    shouldReset = true
                }
            }
            else -> {
                if (shouldReset) {
                    displayText = label
                    shouldReset = false
                } else {
                    displayText = if (displayText == "0") label else displayText + label
                }
                if (currentOperator.isEmpty()) {
                    firstOperand = displayText
                } else {
                    secondOperand = displayText
                }
            }
        }
    }

    Surface(modifier = Modifier
        .fillMaxSize(),
        color = Color.White
        ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            Image(
                painter = painterResource(R.drawable.sun),
                contentDescription = "Backspace",
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .padding(20.dp)
                    .clickable(
                        indication = null, // حذف افکت کلیک
                        interactionSource = remember { MutableInteractionSource() } // حذف تعامل‌ها
                    ) {
                        navController.navigate("dark")
                    },
                alignment = Alignment.Center
            )

        }

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = displayText,
                fontSize = 55.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                fontFamily = bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            val buttons = listOf(
                listOf("Off", "C", "M", "←"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "÷"),
                listOf("1", "2", "3", "+"),
                listOf(".", "0", "=", "-")
            )

            buttons.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { label ->
                        ButtonCard(label, bold, isOffClicked) { handleButtonClick(label) }
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonCard(label: String, fontFamily: FontFamily,isOffClicked: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(70.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (label == "←") {
                Image(
                    painter = painterResource(R.drawable.remove),
                    contentDescription = "Backspace",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    alignment = Alignment.Center
                )
            } else {
                Text(
                    text = if (label == "Off" && isOffClicked) "On" else label,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (label in listOf("÷", "×", "-", "+", "=" , "C" , "Off" , "M")) Color.Blue else Color.Black,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun calculateResult(operand1: String, operand2: String, operator: String): String {
    val num1 = operand1.toDoubleOrNull() ?: return operand1
    val num2 = operand2.toDoubleOrNull() ?: return operand2

    val result = when (operator) {
        "+" -> num1 + num2
        "−" -> num1 - num2
        "×" -> num1 * num2
        "÷" -> if (num2 != 0.0) num1 / num2 else return "Error"
        else -> return operand1
    }
    return result.toString()
}

@Composable
fun DarkTheme(navController: NavController) {

    BackHandler {
        System.exit(0)
    }

    val bold = FontFamily(Font(R.font.nunitobold))
    val regular = FontFamily(Font(R.font.nunitoregular))

    var displayText by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf("") }
    var secondOperand by remember { mutableStateOf("") }
    var currentOperator by remember { mutableStateOf("") }
    var shouldReset by remember { mutableStateOf(false) }
    var isOffClicked by remember { mutableStateOf(false) }

    fun handleButtonClick(label: String) {
        when (label) {
            "Off" -> {
                isOffClicked = !isOffClicked
                if(isOffClicked) {
                    firstOperand = ""
                    secondOperand = ""
                    currentOperator = ""
                    displayText = ""
                } else {
                    firstOperand = ""
                    secondOperand = ""
                    currentOperator = ""
                    displayText = "0"
                }
            }
            "C" -> {
                firstOperand = ""
                secondOperand = ""
                currentOperator = ""
                displayText = "0"
            }
            "←" -> {
                displayText = if (displayText.length > 1) displayText.dropLast(1) else "0"
            }
            "÷", "×", "−", "+" -> {
                if (firstOperand.isNotEmpty() && secondOperand.isNotEmpty()) {
                    displayText = calculateResult(firstOperand, secondOperand, currentOperator)
                    firstOperand = displayText
                    secondOperand = ""
                }
                currentOperator = label
                firstOperand = displayText
                shouldReset = true
            }
            "=" -> {
                if (firstOperand.isNotEmpty() && secondOperand.isNotEmpty()) {
                    displayText = calculateResult(firstOperand, secondOperand, currentOperator)
                    firstOperand = displayText
                    secondOperand = ""
                    currentOperator = ""
                    shouldReset = true
                }
            }
            else -> {
                if (shouldReset) {
                    displayText = label
                    shouldReset = false
                } else {
                    displayText = if (displayText == "0") label else displayText + label
                }
                if (currentOperator.isEmpty()) {
                    firstOperand = displayText
                } else {
                    secondOperand = displayText
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0xFF092C50)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            Image(
                painter = painterResource(R.drawable.moon),
                contentDescription = "Backspace",
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .padding(20.dp)
                    .clickable(
                        indication = null, // حذف افکت کلیک
                        interactionSource = remember { MutableInteractionSource() } // حذف تعامل‌ها
                    ) {
                        navController.navigate("light")
                    },
                alignment = Alignment.Center
            )

        }

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = displayText,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC5DFFA),
                fontFamily = bold,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(20.dp))
            val buttons = listOf(
                listOf("Off", "C", "M", "←"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "÷"),
                listOf("1", "2", "3", "+"),
                listOf(".", "0", "=", "-")
            )

            buttons.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { label ->
                        ButtonCardDark(label, bold, isOffClicked) { handleButtonClick(label) }
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonCardDark(label: String, fontFamily: FontFamily, isOffClicked: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(70.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF165493)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (label == "←") {
                Image(
                    painter = painterResource(R.drawable.removedark),
                    contentDescription = "Backspace",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    alignment = Alignment.Center
                )
            } else {
                Text(
                    text = if (label == "Off" && isOffClicked) "On" else label, // تغییر متن به "On" اگر isOffClicked برابر true باشد
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (label in listOf("÷", "×", "-", "+", "=" , "C" , "Off" , "M")) Color(
                        0xFFC5DFFA
                    ) else Color(0xFF001556),
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

