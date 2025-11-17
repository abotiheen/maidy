package com.example.maidy.feature.maid_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maid_details.ServiceIconType
import com.example.maidy.feature.maid_details.ServiceOffered
import com.example.maidy.ui.theme.*

@Composable
fun ServicesOfferedSection(
    services: List<ServiceOffered>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Services Offered",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaidProfileSectionTitle
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid layout with 2 columns
        val chunkedServices = services.chunked(2)
        chunkedServices.forEachIndexed { index, rowServices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                rowServices.forEach { service ->
                    ServiceItem(
                        serviceName = service.name,
                        iconType = service.iconType,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Fill empty space if odd number
                if (rowServices.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Add spacing between rows, but not after the last row
            if (index < chunkedServices.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesOfferedSectionPreview() {
    MaidyTheme {
        Box(modifier = Modifier.background(MaidProfileContentBackground)) {
            ServicesOfferedSection(
                services = listOf(
                    ServiceOffered("1", "Kitchen Cleaning", ServiceIconType.KITCHEN_CLEANING),
                    ServiceOffered("2", "Bathroom Cleaning", ServiceIconType.BATHROOM_CLEANING),
                    ServiceOffered("3", "Laundry", ServiceIconType.LAUNDRY),
                    ServiceOffered("4", "Dusting", ServiceIconType.DUSTING),
                    ServiceOffered("5", "Vacuuming", ServiceIconType.VACUUMING),
                    ServiceOffered("6", "Window Washing", ServiceIconType.WINDOW_WASHING)
                )
            )
        }
    }
}

