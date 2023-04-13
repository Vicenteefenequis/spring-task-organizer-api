package com.vicente.task.organizer.task.update;

import com.vicente.task.organizer.exceptions.NotFoundException;
import com.vicente.task.organizer.exceptions.NotificationException;
import com.vicente.task.organizer.task.Task;
import com.vicente.task.organizer.task.TaskGateway;
import com.vicente.task.organizer.task.TaskID;
import com.vicente.task.organizer.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultUpdateTaskUseCase extends UpdateTaskUseCase {

    private final TaskGateway taskGateway;

    public DefaultUpdateTaskUseCase(TaskGateway taskGateway) {
        this.taskGateway = Objects.requireNonNull(taskGateway);
    }

    @Override
    public UpdateTaskOutput execute(UpdateTaskCommand aCommand) {
        final TaskID taskID = TaskID.from(aCommand.id());

        final var aTask = this.taskGateway.findById(taskID).orElseThrow(notFound(taskID));

        final var notification = Notification.create();

        notification.validate(() -> aTask.update(aCommand.name(), aCommand.description(), aCommand.isCompleted(), aCommand.dueDate()));

        if (notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Task %s".formatted(aCommand.id()), notification);
        }

        return  UpdateTaskOutput.from(this.taskGateway.update(aTask));
    }

    private static Supplier<NotFoundException> notFound(final TaskID anId) {
        return () -> NotFoundException.with(Task.class, anId);
    }
}
