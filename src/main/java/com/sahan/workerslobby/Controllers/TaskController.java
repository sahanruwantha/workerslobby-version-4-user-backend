package com.sahan.workerslobby.Controllers;

import com.sahan.workerslobby.Entities.Task;
import com.sahan.workerslobby.Entities.Ticket;
import com.sahan.workerslobby.Exceptions.TaskAndTicketNotFoundException;
import com.sahan.workerslobby.Exceptions.TaskNotFoundException;
import com.sahan.workerslobby.Exceptions.TicketNotFoundException;
import com.sahan.workerslobby.Exceptions.UserNotFoundException;
import com.sahan.workerslobby.Services.TaskService;
import com.sahan.workerslobby.Services.TicketService;
import com.sahan.workerslobby.Utils.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController
{
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpResponse> createTask(@RequestBody Task task) throws UserNotFoundException, TaskNotFoundException, TaskAndTicketNotFoundException, TicketNotFoundException {
        log.info(task.toString());
        taskService.createTask(task.getEngineerId(), task.getDescription(), task.getTicketId());
        return response(OK, "Task Created Successfully");
    }


    @GetMapping("/{engineerId}/pending")
    public ResponseEntity<List<Task>> getPendingTasks(@PathVariable("engineerId") long engineerId) throws UserNotFoundException {
        List<Task> pendingTasks = taskService.getPendingTasks(engineerId);
        return new ResponseEntity<>(pendingTasks, OK);
    }

    @GetMapping("/{engineerId}/done")
    public ResponseEntity<List<Task>> getDoneTasks(@PathVariable("engineerId") long engineerId) throws UserNotFoundException {
        List<Task> doneTasksTasks = taskService.getDoneTasks(engineerId);
        return new ResponseEntity<>(doneTasksTasks, OK);
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message)
    {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),
                httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }
}
