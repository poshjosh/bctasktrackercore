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

package com.bc.tasktracker;

import com.bc.tasktracker.jpa.entities.master.Appointment;
import java.util.List;
import com.bc.appcore.AppCore;
import com.bc.appcore.User;
import com.bc.appcore.actions.Action;
import com.bc.appcore.html.HtmlBuilder;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.util.ListedOrder;
import com.bc.tasktracker.actions.TasktrackerCoreActionCommands;
import com.bc.tasktracker.html.TaskHtmlBuilder;
import com.bc.tasktracker.html.TaskresponseHtmlBuilder;
import com.bc.tasktracker.jpa.TasktrackerSearchContext;
import com.bc.tasktracker.jpa.TasktrackerSearchContextImpl;
import com.bc.tasktracker.jpa.entities.master.Doc;
import com.bc.tasktracker.jpa.entities.master.Doc_;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Task_;
import com.bc.tasktracker.jpa.entities.master.Taskresponse;
import com.bc.tasktracker.jpa.entities.master.Unit;
import com.bc.tasktracker.jpa.model.TasktrackerCoreResultModel;
import java.io.File;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.persistence.EntityManager;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:10:58 PM
 */
public interface TasktrackerAppCore extends AppCore {
    
    @Override
    default <T> TasktrackerSearchContext<T> getSearchContext(Class<T> entityType) {
        final ResultModel resultModel = this.getResultModel(entityType, null);
        return new TasktrackerSearchContextImpl<>(this, Objects.requireNonNull(resultModel));
    }
    
    @Override
    default Comparator getEntityOrderComparator() {
        return new ListedOrder(Unit.class, Appointment.class, Doc.class, Task.class, Taskresponse.class);
    }

    @Override
    default Class getUserEntityType() {
        return Appointment.class;
    }
    
    default Appointment getApexAppointment() {
        return this.getJpaContext().getDao(Appointment.class).find(Appointment.class, 1);
    }
    
    default Appointment getUserAppointment(Appointment outputIfNone) {
        return this.getAppointment(this.getUser(), outputIfNone);
    }
    
    default Appointment getAppointment(User user, Appointment outputIfNone) {
        final Action action = this.getAction(
                TasktrackerCoreActionCommands.GET_APPOINTMENT_FOR_USER);
        final Appointment appt = (Appointment)action.executeSilently(
                this, Collections.singletonMap(User.class.getName(), user), outputIfNone);
        return appt;
    }

    @Override
    default <T> ResultModel<T> getResultModel(Class<T> type, ResultModel<T> outputIfNone) {
        final ResultModel model = this.createDefaultResultModel();
        return model;
    }
    
    default ResultModel<Task> createDefaultResultModel() {
        final int serialColumnIndex = 0;
        return new TasktrackerCoreResultModel(
                this, Task.class, this.getTaskColumnNames(), serialColumnIndex    
        );
    }
    
    default List<String> getTaskColumnNames() {
        return Arrays.asList(
                    this.getConfig().getString(ConfigNames.SERIAL_COLUMNNAME), 
                    Task_.taskid.getName(), 
                    Doc_.subject.getName(), Doc_.referencenumber.getName(),
                    Doc_.datesigned.getName(), Task_.reponsibility.getName(),
                    Task_.description.getName(), Task_.timeopened.getName(),
                    "Response 1", "Response 2", "Remarks"
            );
    }

    default Predicate<String> getPersistenceUnitNameTest() {
        return this.getMasterPersistenceUnitTest();
    }

    @Override
    default EntityManager getEntityManager(Class resultType) {
        return this.getJpaContext().getEntityManager(resultType);
    }

    @Override
    default <T> HtmlBuilder<T> getHtmlBuilder(Class<T> entityType) {
        final HtmlBuilder output;
        if(entityType == Task.class) {
            output = new TaskHtmlBuilder(this);
        }else if (entityType == Taskresponse.class) {
            output = new TaskresponseHtmlBuilder(this);
        }else{
            throw new UnsupportedOperationException("Not supported yet.");
        }
        return output;
    }

    default String[] getAppointmentNames() {
        final List<Appointment> list = this.getJpaContext()
                .getBuilderForSelect(Appointment.class)
                .from(Appointment.class)
                .getResultsAndClose();
        final String [] names = new String[1 + list.size()];
        names[0] = null;
        for(int i = 0; i < names.length - 1; i++) {
            names[i + 1] = list.get(i).getAppointment();
        }
        return names;
    }

    default String[] getUnitNames() {
        final List<Unit> list = this.getJpaContext()
                .getBuilderForSelect(Unit.class)
                .getResultsAndClose();
        final String [] names = new String[1 + list.size()];
        names[0] = null;
        for(int i = 0; i < names.length - 1; i++) {
            names[i + 1] = list.get(i).getUnit();
        }
        return names;
    }
    
    default List<Appointment> getTopAppointments() {
        final int count = this.getConfig().getInt("topAppointmentsCount", 13);
        final List<Appointment> topAppointments = this.getJpaContext()
                .getBuilderForSelect(Appointment.class)
                .from(Appointment.class)
                .getResultsAndClose(0, count);
        return topAppointments;
    }

    default Callable<List<File>> getUpdateOutputTask(List<Appointment> appointmentList, boolean refreshDisplay) {
        return () -> { return Collections.EMPTY_LIST; };
    }
    
    @Override
    default DateFormat getDateTimeFormat() {
        return new com.bc.tasktracker.util.DateTimeFormat(this);
    }

    @Override
    default DateFormat getDateFormat() {
        return new com.bc.tasktracker.util.DateFormat(this);
    }
}
/**
 * 

    @Override
    default TasktrackerSearchContext getSearchContext(Class entityType) {
        final ResultModel resultModel = this.getResultModel(entityType, null);
        return new TasktrackerSearchContextImpl<>(this, Objects.requireNonNull(resultModel));
    }
    
    default Set getUsers() {
        return new LinkedHashSet(this.getJpaContext().getBuilderForSelect(Appointment.class).getResultsAndClose());
    }

    default Comparator getEntityOrderComparator() {
        return new ListedOrder(Unit.class, Appointment.class, Doc.class, Task.class, Taskresponse.class);
    }

 * 
 */