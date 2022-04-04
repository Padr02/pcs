package com.example.application.data.views;
import com.example.application.data.FormEvent;
import com.example.application.data.entity.TaskEntity;
import com.example.application.data.entity.UserEntity;
import com.example.application.data.repository.TaskRepository;
import com.example.application.data.service.TaskService;
import com.example.application.data.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.Comparator;

@PageTitle("Tasks")
@PermitAll
@Route(value = "/tasks")
public class TaskView extends VerticalLayout {

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    Grid<TaskEntity> grid = new Grid<>(TaskEntity.class);
    TextField filter = new TextField();
    TaskForm taskForm;

    public TaskView(TaskService taskService, UserService userService)  {
       H1 title = new H1 ("THIS SITE IS UNDER CONSTRUCTION");
       title.addClassName("title");
        HorizontalLayout header;
       /* if (SecurityUtils.isAuthenticated()){
            Button logout = new Button("Logout", click -> SecurityUtils.logout());
            header = new HorizontalLayout(logout);
        } else {
            header = new HorizontalLayout();
        }*/
       // add (header);

        this.userService=userService;
        this.taskService=taskService;
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolBar(),getContent());
        updateFromFilter();
        closeEditor();
    }

    private void closeEditor() {
        taskForm.setTask(null);
        taskForm.setVisible(false);
    }

    private Component getContent() {
       HorizontalLayout content = new HorizontalLayout(grid, taskForm);
       content.setFlexGrow(2, grid);
       content.setFlexGrow(1, taskForm);
       content.setSizeFull();
       return content;
    }

    private void configureForm() {
        taskForm = new TaskForm(userService.getUsers());
        taskForm.setWidth("25em");
        taskForm.addListener(FormEvent.SaveEvent.class, this::saveTask);
        taskForm.addListener(FormEvent.DeleteEvent.class, this::deleteTask);
        taskForm.addListener(FormEvent.CloseEvent.class, e -> closeEditor());
    }

    private void saveTask(FormEvent.SaveEvent event){
        taskService.saveTask(event.getTask());
        updateFromFilter();
        closeEditor();
    }

    private void deleteTask(FormEvent.DeleteEvent event) {
        taskService.deleteTask(event.getTask().getId());
        updateFromFilter();
        closeEditor();
    }

    private void updateFromFilter() {
        grid.setItems(taskService.getTasksByFilter(filter.getValue()));
    }

    private Component getToolBar() {
        filter.setPlaceholder("Search by owner");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> updateFromFilter());
        filter.setWidth("2000px");
        Button addTaskBtn = new Button("Add task",new Icon(VaadinIcon.PLUS));
        addTaskBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTaskBtn.addClickListener(click -> addTask());
        addTaskBtn.setWidth("200px");
        HorizontalLayout horizontalLayout = new HorizontalLayout(filter, addTaskBtn);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        horizontalLayout.setWidth("-webkit-fill-available");
        return horizontalLayout;
    }

    void addTask() {
        grid.asSingleSelect().clear();
        editTask(new TaskEntity());
    }

    private void configureGrid() {
        /*if(userService.getUsers() == null)*/

        grid.setSizeFull();

        grid.setColumns("title", "startDate", "deadline");
        grid.addComponentColumn((item) -> {
           Icon icon;
           if (item.isCompleted()) {
               icon=VaadinIcon.CHECK.create();
               icon.setColor("hsla(145, 92%, 51%, 0.5)");
           } else {
               icon=VaadinIcon.CLOSE.create();
               icon.setColor("hsla(3, 75%, 62%, 0.5)");
           }
           return icon;
        }).setKey("completed").setComparator(Comparator.comparing(TaskEntity::isCompleted)).setHeader("Completed");
        grid.getColumnByKey("completed").setTextAlign(ColumnTextAlign.CENTER);
        grid.addColumn(TaskEntity::getOwnerName).setHeader("Owner");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(task -> editTask(task.getValue()));
    }


    public void editTask(TaskEntity task){
        if (task == null) {
            closeEditor();
        } else {
            taskForm.setTask(task);
            taskForm.setVisible(true);
        }
    }

    public void headerConfig() {
        Header header = new Header();
    }
}


