/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.tasktracker.jpa.model;

import com.bc.appcore.exceptions.UserRuntimeException;
import com.bc.appcore.jpa.model.EntityResultModelImpl;
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.appcore.util.Pair;
import com.bc.config.Config;
import com.bc.tasktracker.TasktrackerAppCore;
import com.bc.tasktracker.functions.TaskreponseTestProvider;
import com.bc.tasktracker.jpa.entities.master.Appointment;
import com.bc.tasktracker.jpa.entities.master.Doc;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Taskresponse;
import com.bc.tasktracker.jpa.entities.master.Taskresponse_;
import com.bc.tasktracker.jpa.predicates.TaskresponseAuthorTest;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 1:17:28 PM
 */
public class TasktrackerCoreResultModel<T> extends EntityResultModelImpl<T> {
    
    private transient static final Logger logger = Logger.getLogger(TasktrackerCoreResultModel.class.getName());

    private final Appointment apexAppointment;
    
    private final Appointment currentUserAppointment;
    
    private final Predicate<Taskresponse> authorIsApexAppointment;
    
    private final Predicate<Taskresponse> taskresponseTest;
    
    public TasktrackerCoreResultModel(
            TasktrackerAppCore app, Class<T> coreEntityType, 
            List<String> columnNames, 
            BiPredicate<String, Object> updateFilter,
            BiConsumer<String, Exception> updateExceptionHandler) {
        
        super(app, coreEntityType, columnNames, 
                app.getOrException(TypeProvider.class), 
                updateFilter, updateExceptionHandler);

        this.apexAppointment = app.getApexAppointment();

        this.currentUserAppointment = app.getUserAppointment(null);
        
        this.authorIsApexAppointment = new TaskresponseAuthorTest(this.apexAppointment);
        
        final BiFunction<Appointment, Config, Predicate<Taskresponse>> trtp = new TaskreponseTestProvider();
        
        this.taskresponseTest = this.authorIsApexAppointment.negate().and(trtp.apply(this.currentUserAppointment, app.getConfig()));
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        final Class value;
        final String columnName = this.getColumnName(columnIndex);
        if(columnName.equals(this.getSerialColumnName())) {
            value = Integer.class;
        }else if("Response 1".equals(columnName)) {
            value = String.class;
        }else if("Response 2".equals(columnName)) {
            value = String.class;
        }else if("Remarks".equals(columnName)) {
            value = String.class;
        }else{
            value = super.getColumnClass(columnIndex);
        }
        return value;
    }
    
    @Override
    public Object get(T entity, int rowIndex, int columnIndex) {
        
        final Object value;
        if(entity instanceof Task) {
            
            final String columnName = this.getColumnName(columnIndex);
            
            final Task task = (Task)entity;
            if("Response 1".equals(columnName)) {
                final Taskresponse res = this.getTaskresponse(task, columnName, false);
                value = res == null ? null : res.getResponse();
            }else if("Response 2".equals(columnName)) {
                final Taskresponse res = this.getTaskresponse(task, columnName, false);
                value = res == null ? null : res.getResponse();
            }else if("Remarks".equals(columnName)) {
                final Taskresponse res = this.getRemark(task, columnName, false);
                value = res == null ? null : res.getResponse();
            }else{
                value = super.get(entity, rowIndex, columnIndex);
            }
        }else{
            value = super.get(entity, rowIndex, columnIndex);
        }
        return value;
    }
    
    @Override
    public Pair<Object, String> getEntityRelation(T entity, int rowIndex, int columnIndex, Object value) {
        
        final Pair<Object, String> relation;
        final String columnName = this.getColumnName(columnIndex);
        
        if(entity instanceof Task) {
            final Task task = (Task)entity;
            if("Response 1".equals(columnName)) {
                final Taskresponse res = this.getTaskresponse(task, columnName, true);
                relation = new Pair(res, Taskresponse_.response.getName());
            }else if("Response 2".equals(columnName)) {
                final Taskresponse res = this.getTaskresponse(task, columnName, true);
                relation = new Pair(res, Taskresponse_.response.getName());
            }else if("Remarks".equals(columnName)) {
                final Taskresponse res = this.getRemark(task, columnName, true);
                relation = new Pair(res, Taskresponse_.response.getName());
            }else{
                relation = super.getEntityRelation(entity, rowIndex, columnIndex, value);
            }
        }else{
            relation = super.getEntityRelation(entity, rowIndex, columnIndex, value);
        }
        return relation;
    }
    
    @Override
    public String getUpdateActionId(Object entity, String columnName, Object columnValue) {
        final String output;
        if(entity instanceof Taskresponse && 
                Taskresponse_.response.getName().equals(columnName) && 
                columnValue == null) {
            output = REMOVE;
        }else{
            output = super.getUpdateActionId(entity, columnName, columnValue);
        }
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Action ID: {0}, entity: {1}, update: {2} = {3}", new Object[]{output, entity, columnName, columnValue});
        }
        return output;
    }
    
    @Override
    public Object formatBeforeUpdate(Object target, String targetColumn, Object targetValue) {
        
        if(target instanceof Doc && 
                "subject".equals(targetColumn) &&
                targetValue == null) {
            
            targetValue = "";
        }
        
        return targetValue;
    }
    
    public Taskresponse getRemark(Task task, String columnName, boolean createIfNone) {
        
        return this.getTaskresponse(task, this.apexAppointment, 
                this.authorIsApexAppointment, columnName, createIfNone);
    }
    
    public Taskresponse getTaskresponse(Task task, String columnName, boolean createIfNone) {
        
        if(createIfNone && this.currentUserAppointment == null) {
            throw new UserRuntimeException("You must be logged in to perform the requested operation");
        }
        
        return this.getTaskresponse(task, this.currentUserAppointment, 
                this.taskresponseTest, columnName, createIfNone);
    }
    
    private Taskresponse getTaskresponse(Task task, Appointment author, 
            Predicate<Taskresponse> filter, String columnName, boolean createIfNone) {
        final int pos = this.getPos(columnName);
        final List<Taskresponse> list = this.filter(task.getTaskresponseList(), filter);
        Taskresponse res = this.getFromEnd(list, columnName, pos, 2);
        if(res == null && createIfNone) {
            if(author != null) {
                res = new Taskresponse();
                res.setAuthor(author);
                res.setTask(task);
            }else{
                throw new RuntimeException("Taskresponse author's appointment may not be null");
            }
        }
        return res;
    }
    
    @Override
    public int getPos(String columnName) {
        int pos = -1;
        switch(columnName) {
            case "Response 1": 
            case "Remarks":
                pos = 0; break;
            case "Response 2": 
                pos = 1; break;
            default: throw new UnsupportedOperationException("Unexpected response column name: "+columnName); 
        }
        return pos;
    }
    
    @Override
    public TasktrackerAppCore getApp() {
        return (TasktrackerAppCore)super.getApp(); 
    }
}
