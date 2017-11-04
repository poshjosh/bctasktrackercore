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
import com.bc.appcore.util.ListedOrder;
import com.bc.tasktracker.actions.TasktrackerCoreActionCommands;
import com.bc.tasktracker.html.TaskHtmlBuilder;
import com.bc.tasktracker.html.TaskresponseHtmlBuilder;
import com.bc.tasktracker.jpa.TasktrackerSearchContext;
import com.bc.tasktracker.jpa.TasktrackerSearchContextImpl;
import com.bc.tasktracker.jpa.entities.master.Doc;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Taskresponse;
import com.bc.tasktracker.jpa.entities.master.Unit;
import com.bc.tasktracker.jpa.model.TasktrackerCoreResultModel;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.jpa.model.EntityResultModel;
import java.util.Optional;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:10:58 PM
 */
public interface TasktrackerAppCore extends AppCore {
    
    @Override
    default <T> TasktrackerSearchContext<T> getSearchContext(Class<T> entityType) {
        final EntityResultModel resultModel = this.getResultModel(entityType, null);
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
        final int id = this.getConfig().getInt(ConfigNames.APEXAPPOINTMENT_APPOINTMENTID, 1);
        return this.getActivePersistenceUnitContext().getDao().find(Appointment.class, id);
    }
    
    default Appointment getUserAppointment(Appointment outputIfNone) {
        return this.getAppointment(this.getUser(), outputIfNone);
    }
    
    default Appointment getAppointment(User user, Appointment outputIfNone) {
        final Action action = this.getAction(
                TasktrackerCoreActionCommands.GET_APPOINTMENT_FOR_USER);
        final Optional optionalAppt = action.executeSilently(
                this, Collections.singletonMap(User.class.getName(), user));
        return optionalAppt.isPresent() ? (Appointment)optionalAppt.get() : outputIfNone;
    }

    @Override
    public default Class getDefaultEntityType() {
        return Task.class;
    }

    @Override
    public default String getSerialColumnName() {
        return this.getConfig().getString(ConfigNames.SERIAL_COLUMNNAME);
    }

    @Override
    default EntityResultModel createResultModel(Class entityType, String[] columnNames) {
        return new TasktrackerCoreResultModel(
                this, entityType, Arrays.asList(columnNames), 
                (col, val) -> true, (col, exception) -> 
                        Logger.getLogger(this.getClass().getName()).log(
                        Level.WARNING, "Error updating: " + col, exception
                ));
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
        final List<Appointment> list = this.getActivePersistenceUnitContext()
                .getDao().forSelect(Appointment.class)
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
        final List<Unit> list = this.getActivePersistenceUnitContext()
                .getDao().forSelect(Unit.class).from(Unit.class)
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
        final List<Appointment> topAppointments = this.getActivePersistenceUnitContext()
                .getDao().forSelect(Appointment.class)
                .from(Appointment.class)
                .getResultsAndClose(0, count);
        return topAppointments;
    }

    default Callable<List<File>> getUpdateOutputTask(List<Appointment> appointmentList) {
        return () -> { return Collections.EMPTY_LIST; };
    }
    

    @Override
    default String getDateTimePattern() {
        return this.getConfig().getString(ConfigNames.DATETIME_PATTERN, "dd-MMM-yy HH:mm");
    }

    @Override
    default String getDatePattern() {
        return this.getConfig().getString(ConfigNames.DATE_PATTERN, "dd-MMM-yy");
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