package com.example.domyjob.service;

import com.example.domyjob.model.Invoice;
import com.example.domyjob.model.JobChain;
import com.example.domyjob.model.Task;
import com.example.domyjob.repository.InvoiceRepository;
import com.example.domyjob.repository.JobChainRepository;
import com.example.domyjob.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TaskScheduler taskScheduler;

    // Keeps track of scheduled tasks
    private ScheduledFuture<?> scheduledTask;

    @Autowired
    private JobChainRepository jobChainRepository;
    public void scheduleJobChain(JobChain jobChain) {
        jobChainRepository.save(jobChain);
    }

    @PostConstruct
    public void scheduleRunnableWithCronTrigger() {
        Runnable task = () -> {
            JobChain jobChain = jobChainRepository.findNextScheduledJobChain(LocalDateTime.now());
            if (jobChain != null) {
                executeJobChain(jobChain);
                jobChainRepository.delete(jobChain);
            }
        };

        String cronExpression = getCronExpressionForNextJob();
        if (cronExpression != null) {
            CronTrigger cronTrigger = new CronTrigger(cronExpression, ZoneId.systemDefault());
            scheduledTask = taskScheduler.schedule(task, cronTrigger);
        }
    }

    private String getCronExpressionForNextJob() {
        // Retrieve the next jobChain (if any) and convert its scheduleTime to a cron expression
        JobChain nextJobChain = jobChainRepository.findNextScheduledJobChain(LocalDateTime.now());
        if (nextJobChain != null) {
            return convertLocalDateTimeToCron(nextJobChain.getScheduleTime());
        }
        return null;
    }

    private String convertLocalDateTimeToCron(LocalDateTime dateTime) {
        return String.format("%d %d %d %d %d ?", dateTime.getSecond(), dateTime.getMinute(), dateTime.getHour(),
                dateTime.getDayOfMonth(), dateTime.getMonthValue());
    }

    private void executeJobChain(JobChain jobChain) {
        // Assuming jobChain.getTaskIds() returns a single String of comma-separated task IDs
        String taskIdsString = jobChain.getTaskIds();
        String[] taskIds = taskIdsString.split(",");

        // Retrieve and execute each task by its ID
        for (String taskId : taskIds) {
            // Assuming you have a method to lookup Task details by taskId
            Task task = getTaskById(taskId.trim()); // Trim to remove any leading or trailing spaces

            if (task != null) { // Ensure task is found
                switch (task.getType()) {
                    case "DbUpdateTask":
                        // Logic to execute DB update task
                        updateInvoiceStatusToPaid(); // This method needs to be implemented based on your requirements
                        break;
                    case "EmailTask":
                        // Extract details for email task and execute
                        sendEmailNotification(task); // Adjust this method to accept Task object or extract details here
                        break;
                    default:
                        System.out.println("Unsupported task type: " + task.getType());
                        break;
                }
            } else {
                System.out.println("Task not found for ID: " + taskId);
            }
        }
    }



    public void sendEmailNotification(Task task) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(task.getToEmail());
        message.setFrom(task.getFromEmail());
        message.setSubject(task.getSubject());
        message.setText(task.getBody());
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

    public Task getTaskById(String taskId) {
        Optional<Task> taskOptional = taskRepository.findById(Long.valueOf(taskId));
        return taskOptional.orElse(null);
    }

}
