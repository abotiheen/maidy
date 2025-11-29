package com.example.maidy.feature.booking_details.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.BookingDetailsBackground
import com.example.maidy.ui.theme.BookingDetailsDaySelectedBackground
import com.example.maidy.ui.theme.BookingDetailsDaySelectedText
import com.example.maidy.ui.theme.BookingDetailsDayUnselectedBorder
import com.example.maidy.ui.theme.BookingDetailsDayUnselectedText
import com.example.maidy.ui.theme.BookingDetailsSectionTitle
import com.example.maidy.ui.theme.MaidyTheme


enum class PaymentMethod(val title: String) {
    VISA_MASTER("Credit Card"),
    ZAIN_CASH("Zain Cash"),
    CASH("Cash")
}

@Composable
fun PaymentMethodSelector(
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Payment Method",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = BookingDetailsSectionTitle
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentMethod.values().forEach { method ->
                PaymentMethodChip(
                    text = method.title,
                    isSelected = selectedPaymentMethod == method,
                    onClick = {
                        selectedPaymentMethod =
                            if (selectedPaymentMethod == method) null else method
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        AnimatedContent(
            targetState = when (selectedPaymentMethod) {
                PaymentMethod.VISA_MASTER -> "visa"
                PaymentMethod.ZAIN_CASH -> "zain_cash"
                else -> "cash"
            },
        ) { targetState ->
            when (targetState) {
                "visa" -> VisaMastercardForm()
                "zain_cash" -> ZainCashForm()
                else -> CashForm()
            }
        }
    }
}

@Composable
private fun PaymentMethodChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) BookingDetailsDaySelectedBackground else Color.White
    val textColor =
        if (isSelected) BookingDetailsDaySelectedText else BookingDetailsDayUnselectedText
    val borderColor =
        if (isSelected) BookingDetailsDaySelectedBackground else BookingDetailsDayUnselectedBorder

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VisaMastercardForm(modifier: Modifier = Modifier) {
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PaymentTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = "Card Number",
            keyboardType = KeyboardType.Number
        )
        PaymentTextField(
            value = cardName,
            onValueChange = { cardName = it },
            label = "Cardholder Name"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PaymentTextField(
                modifier = Modifier.weight(1f),
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = "MM/YY",
                keyboardType = KeyboardType.Number
            )
            PaymentTextField(
                modifier = Modifier.weight(1f),
                value = cvc,
                onValueChange = { cvc = it },
                label = "CVC",
                keyboardType = KeyboardType.Number,
                isPassword = true
            )
        }
    }
}

@Composable
private fun ZainCashForm(modifier: Modifier = Modifier) {
    var phoneNumber by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PaymentTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Phone Number",
            keyboardType = KeyboardType.Phone
        )
        PaymentTextField(
            value = pin,
            onValueChange = { pin = it },
            label = "PIN",
            keyboardType = KeyboardType.NumberPassword,
            isPassword = true
        )
    }
}

@Composable
private fun CashForm(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Cash payment selected. Please pay your total at the time of service.",
            color = BookingDetailsDayUnselectedText,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}


@Preview(showBackground = true)
@Composable
private fun PaymentMethodSelectorPreview() {
    MaidyTheme {
        Column(
            Modifier
                .background(BookingDetailsBackground)
                .padding(16.dp)
        ) {
            PaymentMethodSelector()
        }
    }
}
