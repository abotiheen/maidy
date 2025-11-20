package com.example.maidy.feature.maid_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun ServiceItem(
    serviceName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaidProfileServiceIcon,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = serviceName,
            fontSize = 14.sp,
            color = MaidProfileServiceText
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ServiceItemPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .background(MaidProfileContentBackground)
                .padding(16.dp)
        ) {
            ServiceItem(
                serviceName = "Kitchen Cleaning"
            )
        }
    }
}

