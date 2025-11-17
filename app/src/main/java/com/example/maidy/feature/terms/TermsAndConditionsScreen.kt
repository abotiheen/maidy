package com.example.maidy.feature.terms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.ui.theme.*

@Composable
fun TermsAndConditionsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Last Updated
        Text(
            text = "Last Updated: November 17, 2025",
            fontSize = 14.sp,
            color = MaidyTextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Introduction
        SectionTitle(text = "1. Introduction")
        SectionContent(
            text = "Welcome to Maidy! These Terms and Conditions govern your use of our mobile application and services. By accessing or using Maidy, you agree to be bound by these terms. If you do not agree with any part of these terms, please do not use our services."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Account Registration
        SectionTitle(text = "2. Account Registration")
        SectionContent(
            text = "To use Maidy services, you must register for an account by providing accurate and complete information. You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account. You must be at least 18 years old to create an account."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Services
        SectionTitle(text = "3. Services Provided")
        SectionContent(
            text = "Maidy connects customers with professional cleaning service providers. We facilitate the booking and payment process but do not directly employ the service providers. The quality and execution of services are the responsibility of the individual service providers."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User Obligations
        SectionTitle(text = "4. User Obligations")
        SectionContent(
            text = "You agree to:\n\n" +
                    "• Provide accurate information during booking\n" +
                    "• Treat service providers with respect\n" +
                    "• Ensure a safe working environment\n" +
                    "• Pay for services as agreed\n" +
                    "• Not misuse the platform for illegal activities\n" +
                    "• Comply with all applicable local laws and regulations"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Payment Terms
        SectionTitle(text = "5. Payment Terms")
        SectionContent(
            text = "All payments are processed through our secure payment gateway. Prices are clearly displayed before booking confirmation. Payment is required at the time of booking or as specified for the particular service. Refund policies vary based on cancellation timing and circumstances."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cancellation Policy
        SectionTitle(text = "6. Cancellation Policy")
        SectionContent(
            text = "Cancellations made 24 hours before the scheduled service will receive a full refund. Cancellations made less than 24 hours before service may incur a cancellation fee. Service providers may also cancel bookings with appropriate notice and reasoning."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Privacy
        SectionTitle(text = "7. Privacy and Data Protection")
        SectionContent(
            text = "We take your privacy seriously. Your personal information is collected, used, and protected in accordance with our Privacy Policy. We implement industry-standard security measures to protect your data. By using Maidy, you consent to our data practices as described in our Privacy Policy."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Liability
        SectionTitle(text = "8. Limitation of Liability")
        SectionContent(
            text = "Maidy acts as a platform connecting users with service providers. We are not liable for any damages, losses, or injuries arising from services provided by third-party service providers. Our maximum liability is limited to the amount paid for the specific service in question."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Intellectual Property
        SectionTitle(text = "9. Intellectual Property")
        SectionContent(
            text = "All content, trademarks, and intellectual property on the Maidy platform are owned by Maidy or licensed to us. You may not use, copy, or distribute any content without our explicit written permission."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Modifications
        SectionTitle(text = "10. Modifications to Terms")
        SectionContent(
            text = "We reserve the right to modify these Terms and Conditions at any time. Changes will be effective immediately upon posting. Continued use of the platform after changes constitutes acceptance of the modified terms."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dispute Resolution
        SectionTitle(text = "11. Dispute Resolution")
        SectionContent(
            text = "Any disputes arising from these terms or your use of Maidy shall be resolved through binding arbitration. You agree to resolve disputes on an individual basis and waive any right to participate in class-action lawsuits."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Contact Information
        SectionTitle(text = "12. Contact Us")
        SectionContent(
            text = "If you have any questions about these Terms and Conditions, please contact us at:\n\n" +
                    "Email: support@maidy.com\n" +
                    "Phone: +964 XXX XXX XXXX\n" +
                    "Address: Baghdad, Iraq"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Agreement Statement
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "By using Maidy, you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions.",
            fontSize = 14.sp,
            color = MaidyTextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaidyTextPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SectionContent(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = MaidyTextSecondary,
        lineHeight = 20.sp
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TermsAndConditionsScreenPreview() {
    MaidyTheme {
        TermsAndConditionsScreen()
    }
}
