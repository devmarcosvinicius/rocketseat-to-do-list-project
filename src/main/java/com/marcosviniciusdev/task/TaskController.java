package com.marcosviniciusdev.task;

import com.marcosviniciusdev.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início e de termino deve ser maior do que a data atual.");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor que a data de término.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.save(taskModel));
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        return this.taskRepository.findByIdUser((UUID) idUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);
        var idUser = request.getAttribute("idUser");

        if (task == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");

        if (!task.getIdUser().equals(idUser)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não têm permissão para alterar essa tarefa.");

        Utils.copyNonNullProperties(taskModel, task);

        return ResponseEntity.ok().body(this.taskRepository.save(task));
    }

}
