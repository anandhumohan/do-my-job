package com.example.domyjob.service;

import com.example.domyjob.dto.EmailDetailsDTO;
import com.example.domyjob.dto.TaskRequest;
import com.example.domyjob.model.Invoice;
import com.example.domyjob.model.Task;
import com.example.domyjob.repository.InvoiceRepository;
import com.example.domyjob.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailNotification(EmailDetailsDTO emailDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDetails.getRecipient());
        message.setFrom("anandhumohan5@gmail.com");
        message.setSubject("Notification");
        message.setText(emailDetails.getBody());
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.out.print(e.getMessage());
        }
    }


    public void updateInvoiceStatusToPaid() {
        List<Invoice> pendingInvoices = invoiceRepository.findPendingInvoicesWithFullPayment();
        for (Invoice invoice : pendingInvoices) {
            invoice.setStatus("Paid");
            invoiceRepository.save(invoice);
        }
    }

}
