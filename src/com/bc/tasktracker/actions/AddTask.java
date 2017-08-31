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
import com.bc.tasktracker.jpa.entities.master.Appointment;
import com.bc.tasktracker.jpa.entities.master.Doc;
import com.bc.tasktracker.jpa.entities.master.Doc_;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Task_;
import com.bc.tasktracker.jpa.DocDao;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.jpa.dao.Criteria;
import com.bc.tasktracker.TasktrackerAppCore;
import java.util.List;
import javax.persistence.NonUniqueResultException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 12, 2017 4:07:23 PM
 */
public class AddTask implements Action<AppCore, Task> {

    @Override
    public Task execute(AppCore appCore, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final Logger logger = Logger.getLogger(AddTask.class.getName());
        
        logger.entering(this.getClass().getName(), "execute(AppCore, Map<String, Object>)");
        
        final TasktrackerAppCore app = (TasktrackerAppCore)appCore;
        
        try(Dao dao = app.getDao(Task.class)){
            
            dao.begin();
            
            final Doc doc;
            final Object docid = params.get(Doc_.docid.getName());
            
            logger.log(Level.FINE, "docid: {0}", docid);
            
            if(docid != null) {
                doc = app.getDao(Doc.class).findAndClose(Doc.class, docid);
                logger.log(Level.FINER, "Doc: {0}", doc);
                if(doc == null) {
                    throw new InvalidParameterException(Doc_.docid.getName() + " = " + docid);
                }
            }else{
                
                final String refnum = (String)params.get(Doc_.referencenumber.getName());
                final String subj = (String)params.get(Doc_.subject.getName());
                final Date datesigned = (Date)params.get(Doc_.datesigned.getName());
                
                doc = new DocDao(app.getJpaContext()).findOrCreateIfNone(datesigned, refnum, subj);
                if(doc.getDocid() == null) {
                    logger.log(Level.FINER, "Persisting: {0}", doc);
                    dao.persist(doc);
                }
            }
            
            final String resCol = Task_.reponsibility.getName();
            final String resVal = (String)params.get(resCol);
            if(resVal == null) {
                throw new ParameterNotFoundException(resCol);
            }
            
            final List<Appointment> found = app.getJpaContext().getTextSearch().search(Appointment.class, resVal, Criteria.ComparisonOperator.EQUALS);
            if(found == null || found.isEmpty()) {
                throw new InvalidParameterException(resCol + " = " + resVal);
            }
            if(found.size() > 1) {
                throw new NonUniqueResultException("Expected unique result but found otherwise searching for "
                        +(resCol+" = " + resVal)+" in entity: "+Appointment.class.getName());
            }
            
            final Appointment responsibility = found.get(0);
            
            if(responsibility == null) {
                throw new InvalidParameterException(resCol + " = " + resVal);
            }
            
            final Task task = new Task();
            task.setDescription((String)params.get(Task_.description.getName()));
            task.setDoc(doc);
            final Appointment author = app.getUserAppointment(null);
            if(author == null) {
                throw new TaskExecutionException("You must be logged in to perform the requested operation");
            }
            task.setAuthor(author);
            task.setReponsibility(responsibility);
            task.setTimeopened((Date)params.get(Task_.timeopened.getName()));
            
            dao.persist(task);
            
            dao.commit();
            
            logger.log(Level.FINER, "After commit docid: {0}", doc.getDocid());
            logger.log(Level.FINER, "After commit taskid: {0}", task.getTaskid());
            
            return task;
        }
    }
}    