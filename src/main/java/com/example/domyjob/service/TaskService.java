package com.example.domyjob.service;

import com.example.domyjob.dto.TaskRequest;
import com.example.domyjob.model.Task;
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
    private JavaMailSender mailSender; // For EMAIL tasks

    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleNewTask(Task task) {
        Runnable taskRunnable = createRunnableForTask(task);
        CronTrigger cronTrigger = new CronTrigger(task.getCronExpression());
        taskScheduler.schedule(taskRunnable, cronTrigger);
    }

    private Runnable createRunnableForTask(Task task) {
        return () -> {
            switch (task.getTaskType()) {
                case "EMAIL":
                    sendEmailNotification(task.getDescription()); // Assume this method sends an email
                    break;
                case "DB_UPDATE":
                    updateDatabase(task.getDescription()); // Custom method to update DB
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported task type: " + task.getTaskType());
            }
        };
    }

    private void sendEmailNotification(String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient@example.com"); // Configure the recipient dynamically if needed
        message.setSubject("Notification");
        message.setText(messageContent);
        try {
            mailSender.send(message);
            // Log success or additional actions
        } catch (MailException e) {
            // Log failure or take corrective action
            e.printStackTrace();
        }
    }

    private void updateDatabase(String description) {
        int updatedCount = taskRepository.updateStatus("COMPLETED", description);
        System.out.println(updatedCount + " tasks were marked as COMPLETED.");
    }

}
