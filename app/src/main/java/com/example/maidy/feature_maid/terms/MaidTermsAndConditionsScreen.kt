package com.example.maidy.feature_maid.terms

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
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary
import com.example.maidy.feature_maid.auth.MaidAppTextSecondary
import com.example.maidy.ui.theme.*

@Composable
fun MaidTermsAndConditionsScreen(
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
            color = MaidAppTextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Introduction
        MaidSectionTitle(text = "1. Introduction")
        MaidSectionContent(
            text = "Welcome to Maidy for Maids! These Terms and Conditions govern your use of our mobile application and services as a service provider. By accessing or using Maidy, you agree to be bound by these terms. If you do not agree with any part of these terms, please do not use our services."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Account Registration
        MaidSectionTitle(text = "2. Service Provider Registration")
        MaidSectionContent(
            text = "To provide services through Maidy, you must register as a service provider by providing accurate and complete information. You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account. You must be at least 18 years old and legally authorized to work in Iraq."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Services
        MaidSectionTitle(text = "3. Services Provided")
        MaidSectionContent(
            text = "As a Maidy service provider, you agree to provide professional cleaning services to customers. You are an independent contractor and not an employee of Maidy. You are responsible for the quality, safety, and execution of all services you provide through the platform."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Service Provider Obligations
        MaidSectionTitle(text = "4. Service Provider Obligations")
        MaidSectionContent(
            text = "You agree to:\n\n" +
                    "• Provide accurate information about your skills and availability\n" +
                    "• Treat customers with respect and professionalism\n" +
                    "• Arrive on time for scheduled bookings\n" +
                    "• Provide services as described and agreed upon\n" +
                    "• Maintain appropriate insurance coverage\n" +
                    "• Comply with all applicable local laws and regulations\n" +
                    "• Respect customer property and privacy\n" +
                    "• Use your own cleaning supplies unless otherwise agreed"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Terms
        MaidSectionTitle(text = "5. Payment Terms")
        MaidSectionContent(
            text = "You will receive payment for completed services through our platform. Maidy charges a service fee for each booking, which will be clearly communicated. Payments are processed according to our standard schedule. You are responsible for any applicable taxes on your earnings."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cancellation Policy
        MaidSectionTitle(text = "6. Cancellation Policy")
        MaidSectionContent(
            text = "You may cancel bookings with appropriate notice to customers. Repeated cancellations may result in account suspension or termination. If you need to cancel, please provide as much advance notice as possible through the app. Emergency cancellations should be communicated immediately."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy
        MaidSectionTitle(text = "7. Privacy and Data Protection")
        MaidSectionContent(
            text = "We take your privacy seriously. Your personal information is collected, used, and protected in accordance with our Privacy Policy. We implement industry-standard security measures to protect your data. You must not share customer information with third parties or use it for purposes outside of providing services through Maidy."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Liability
        MaidSectionTitle(text = "8. Limitation of Liability")
        MaidSectionContent(
            text = "As an independent contractor, you are responsible for any damages, losses, or injuries that occur during service provision. You agree to indemnify and hold Maidy harmless from any claims, damages, or expenses arising from your services. We recommend maintaining appropriate liability insurance."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Background Checks
        MaidSectionTitle(text = "9. Verification and Background Checks")
        MaidSectionContent(
            text = "Maidy may conduct background checks and verify your credentials before approving your account. You authorize us to conduct such checks and verify your identity, references, and work authorization. You must promptly update any changes to your credentials or legal status."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Account Suspension
        MaidSectionTitle(text = "10. Account Suspension and Termination")
        MaidSectionContent(
            text = "Maidy reserves the right to suspend or terminate your account for violations of these terms, poor service quality, customer complaints, or any other reason deemed appropriate. You may also terminate your account at any time by contacting support."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Intellectual Property
        MaidSectionTitle(text = "11. Intellectual Property")
        MaidSectionContent(
            text = "All content, trademarks, and intellectual property on the Maidy platform are owned by Maidy or licensed to us. You may not use, copy, or distribute any content without our explicit written permission."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Modifications
        MaidSectionTitle(text = "12. Modifications to Terms")
        MaidSectionContent(
            text = "We reserve the right to modify these Terms and Conditions at any time. Changes will be effective immediately upon posting. Continued use of the platform after changes constitutes acceptance of the modified terms."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dispute Resolution
        MaidSectionTitle(text = "13. Dispute Resolution")
        MaidSectionContent(
            text = "Any disputes arising from these terms or your use of Maidy shall be resolved through binding arbitration. You agree to resolve disputes on an individual basis and waive any right to participate in class-action lawsuits."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contact Information
        MaidSectionTitle(text = "14. Contact Us")
        MaidSectionContent(
            text = "If you have any questions about these Terms and Conditions, please contact us at:\n\n" +
                    "Email: support@maidy.com\n" +
                    "Phone: +964 XXX XXX XXXX\n" +
                    "Address: Baghdad, Iraq"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Agreement Statement
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "By using Maidy as a service provider, you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions.",
            fontSize = 14.sp,
            color = MaidAppTextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun MaidSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaidAppTextPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun MaidSectionContent(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = MaidAppTextSecondary,
        lineHeight = 20.sp
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MaidTermsAndConditionsScreenPreview() {
    MaidyTheme {
        MaidTermsAndConditionsScreen()
    }
}
