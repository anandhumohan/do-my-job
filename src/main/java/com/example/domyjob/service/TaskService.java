package com.example.domyjob.service;

import com.example.domyjob.dto.EmailDetailsDTO;
import com.example.domyjob.dto.TaskRequest;
import com.example.domyjob.model.Invoice;
import com.example.domyjob.model.JobChain;
import com.example.domyjob.model.Task;
import com.example.domyjob.repository.InvoiceRepository;
import com.example.domyjob.repository.JobChainRepository;
import com.example.domyjob.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JobChainRepository jobChainRepository;
    public void scheduleJobChain(JobChain jobChain) {
        jobChainRepository.save(jobChain);
    }

    // This method will be triggered based on a fixed delay and check for job chains to run
    @Scheduled(fixedDelayString = "${job.chain.fixed.delay:60000}") // Value from properties or default to 60 seconds
    public void runScheduledJobChains() {
        List<JobChain> jobChainsToRun = jobChainRepository.findAll();
        ZonedDateTime now = ZonedDateTime.now();

        for (JobChain jobChain : jobChainsToRun) {
            // Assuming one-time execution, for recurring tasks you would use the cron expression
            if (jobChain.getScheduledTime() != null && jobChain.getScheduledTime().isBefore(now)) {
                executeJobChain(jobChain);
                // After execution, remove the job chain or update its schedule
                jobChainRepository.delete(jobChain); // For one-time execution
            }
        }
    }

    // Logic to execute the job chain based on its configuration
    private void executeJobChain(JobChain jobChain) {
        // Extract and iterate over the tasks in the job chain
        List<String> tasks = jobChain.getTaskNameList();
        for (String taskName : tasks) {
            switch (taskName) {
                case "updateInvoiceStatus":
                    updateInvoiceStatusToPaid(); // Call the existing method to update invoice statuses
                    break;
                case "sendEmailNotification":
                    sendEmailNotification(new EmailDetailsDTO(/* details */)); // Call the existing method to send emails
                    break;
                default:
                    // Handle unknown task names or log a warning
                    break;
            }
        }
    }

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


    @Transactional
    public void updateInvoiceStatusToPaid() {
        List<Invoice> pendingInvoices = invoiceRepository.findPendingInvoicesWithFullPayment();
        for (Invoice invoice : pendingInvoices) {
            invoice.setStatus("Paid");
            invoiceRepository.save(invoice);
        }
    }

}
