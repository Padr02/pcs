package com.example.application.data.views;

import com.example.application.data.FormEvent;
import com.example.application.data.entity.TaskEntity;
import com.example.application.data.entity.UserEntity;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class TaskForm extends FormLayout {

    TextField title = new TextField("Title");
    DatePicker startDate = new DatePicker("Start date");
    DatePicker deadline = new DatePicker("Deadline");
    ComboBox<UserEntity> owner = new ComboBox<>("Owner");
    Binder<TaskEntity> binder = new BeanValidationBinder<>(TaskEntity.class);
    Checkbox status = new Checkbox("Status");
    TaskEntity task;
    private ComponentEventBus eventBus = null;

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");

    public TaskForm(List<UserEntity> users) {
        owner.setItems(users);
        owner.setItemLabelGenerator(UserEntity::getUsername);
        add(title, startDate, deadline, owner, status, createBtnLayout());
        binder.bindInstanceFields(this);
    }

    // TODO: Kontroll att man inte får lägga in en task deadline som är före startdatum
    public void setTask(TaskEntity task) {
        this.task = task;
        binder.readBean(task);
    }

    private Component createBtnLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        status.setValue(false);
        status.addValueChangeListener(e -> save.setEnabled(binder.isValid()));
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new FormEvent.DeleteEvent(this,task)));
        cancel.addClickListener(event -> fireEvent(new FormEvent.CloseEvent(this)));
        return new HorizontalLayout(save, delete, cancel);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(task);
            fireEvent(new FormEvent.SaveEvent(this, task));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    protected ComponentEventBus getEventBus() {
        if (this.eventBus == null) {
            this.eventBus = new ComponentEventBus(this);
        }
        return this.eventBus;
    }

    protected <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return this.getEventBus().addListener(eventType, listener);
    }

    protected boolean hasListener(Class<? extends ComponentEvent> eventType) {
        return this.eventBus != null && this.eventBus.hasListener(eventType);
    }

}

