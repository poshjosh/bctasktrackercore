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

package com.bc.tasktracker.actions;

import com.bc.appcore.AppCore;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.jpa.dao.Dao;
import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Task_;
import com.bc.tasktracker.jpa.entities.master.Taskresponse;
import com.bc.tasktracker.jpa.entities.master.Taskresponse_;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.appcore.util.TextHandler;
import com.bc.tasktracker.TasktrackerAppCore;
import com.bc.tasktracker.jpa.entities.master.Appointment;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 2:15:30 PM
 */
public class AddTaskresponse implements Action<AppCore, Taskresponse> {
    
    @Override
    public Taskresponse execute(AppCore appCore, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Logger logger = Logger.getLogger(AddTaskresponse.class.getName());

        final TasktrackerAppCore app = (TasktrackerAppCore)appCore;
        
        try(Dao dao = app.getDao(Task.class)){
            
            final Object taskid = params.get(Task_.taskid.getName());

            if(taskid == null) {
                throw new ParameterNotFoundException(Task_.taskid.getName());
            }

            final Task task = dao.find(Task.class, taskid);
            
            if(task == null) {
                throw new InvalidParameterException(Task_.taskid.getName() + " = " + taskid);
            }
            
            Date deadline = (Date)params.get(Taskresponse_.deadline.getName());
            final String respStr = (String)params.get(Taskresponse_.response.getName());
            if(deadline == null) {
                final TextHandler textHandler = app.getOrException(TextHandler.class);
                if(!textHandler.isNullOrEmpty(respStr)) {
                    final String dateStr = textHandler.getLastDateStr(respStr);
                    if(!textHandler.isNullOrEmpty(dateStr)) {
                        deadline = textHandler.getDate(dateStr);
                    }
                    if(logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Converted last date text in response: {0} to deadline: {1}", 
                                new Object[]{dateStr, deadline});
                    }
                }
            }
            
            final Taskresponse response = new Taskresponse();
            final Appointment author = app.getUserAppointment(null);
            if(author == null) {
                throw new TaskExecutionException("You must be logged in to perform the requested operation");
            }
            response.setAuthor(author);
            response.setDeadline(deadline);
            response.setResponse(respStr);
            response.setTask(task); 
            
            app.getDao(Taskresponse.class).begin().persistAndClose(response);
            
            return response;
        }
    }
}
