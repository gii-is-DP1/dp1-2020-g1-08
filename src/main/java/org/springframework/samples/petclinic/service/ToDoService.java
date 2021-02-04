package org.springframework.samples.petclinic.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.ToDo;
import org.springframework.samples.petclinic.repository.ToDoRepository;
import org.springframework.samples.petclinic.validation.ToDoLimitMilestoneException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ToDoService {

    private ToDoRepository toDoRepository;

    @Autowired
    public ToDoService(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    @Transactional(rollbackFor = ToDoLimitMilestoneException.class)
    public void saveToDo(ToDo toDo) throws DataAccessException, ToDoLimitMilestoneException {
        if (toDo.getDone() || toDo.getAssignee().getToDos().stream()
                .filter(x -> !toDo.getDone() && x.getMilestone() == toDo.getMilestone()).count() < 7) {
            toDoRepository.save(toDo);
        } else {
            throw new ToDoLimitMilestoneException();
        }
    }

    @Transactional(readOnly = true)
    public ToDo findToDoById(Integer toDoId) {
        return toDoRepository.findById(toDoId);
    }

    @Transactional(readOnly = true)
    public void deleteToDoById(Integer toDoId) throws DataAccessException {
        toDoRepository.deleteById(toDoId);
    }

    @Transactional(readOnly = true)
    public Collection<ToDo> findToDoByMilestoneAndUser(Integer milestoneId, Integer userId) {
        return toDoRepository.findToDoByMilestoneAndUser(milestoneId, userId);
    }

}
