package com.salarypayoutsystem.employeeservice.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendSalaryCreditEmail(String toEmail, String employeeName, String month, Integer year, Double amount) {
        try {
            // 1. Generate PDF in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            
            document.add(new Paragraph("SalaryCore Enterprise - Official Payslip", titleFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Employee Name: " + employeeName, bodyFont));
            document.add(new Paragraph("Salary Month: " + month + " " + year, bodyFont));
            document.add(new Paragraph("Amount Credited: Rs. " + amount, bodyFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("This is a computer generated document and requires no signature.", bodyFont));
            document.close();

            // 2. Create MimeMessage and attach PDF
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(toEmail);
            helper.setSubject("Salary Credited: " + month + " " + year + " (Payslip Attached)");
            
            String body = "Dear " + employeeName + ",\n\n"
                    + "Your salary of Rs. " + amount + " for " + month + " " + year + " has been credited.\n"
                    + "Please find your official PDF payslip attached to this email.\n\n"
                    + "Regards,\nPayroll Team";
            
            helper.setText(body);
            
            ByteArrayDataSource bds = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
            helper.addAttachment("Payslip_" + month + "_" + year + ".pdf", bds);
            
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
