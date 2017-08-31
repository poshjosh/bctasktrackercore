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

package com.bc.tasktracker.jpa;

import com.bc.appcore.jpa.AbstractSelectionContext;
import com.bc.tasktracker.jpa.entities.master.Appointment;
import com.bc.tasktracker.jpa.entities.master.Appointment_;
import com.bc.tasktracker.jpa.entities.master.Unit;
import com.bc.tasktracker.TasktrackerAppCore;
/**
 * @author Chinomso Bassey Ikwuagwu on Mar 31, 2017 5:23:08 PM
 */
public class TasktrackerSelectionContext extends AbstractSelectionContext {

    public TasktrackerSelectionContext(TasktrackerAppCore app) {
        super(app);
    }
    
    @Override
    public String getSelectionColumn(Class entityType, String outputIfNone) {
        final String columnName;
        if(entityType == Appointment.class) {
            columnName = Appointment_.abbreviation.getName();
        }else if(entityType == Unit.class) {
            columnName = "abbreviation";//Unit_.abbreviation.getName();
        }else {
            columnName = outputIfNone;
        }   
        return columnName;
    }
}
