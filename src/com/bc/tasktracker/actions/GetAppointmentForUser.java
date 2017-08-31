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
import com.bc.appcore.actions.Action;
import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.Criteria;
import com.bc.tasktracker.jpa.entities.master.Appointment;
import java.util.List;
import java.util.Map;
import com.bc.appcore.User;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 31, 2017 7:54:24 PM
 */
public class GetAppointmentForUser implements Action<AppCore, Appointment> {

    @Override
    public Appointment execute(AppCore app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final Object oval = params.get(User.class.getName());
        
        final User user = oval != null ? (User)oval : app.getUser();
        
        if(!user.isLoggedIn()) {
            
            return null;
        }
        
        assert user.isLoggedIn();
        
        final String username = user.getName();
        
        final JpaContext jpaContext = app.getJpaContext();
        
        final List<Appointment> found = jpaContext.getTextSearch().search(Appointment.class, username, Criteria.ComparisonOperator.EQUALS);

        if(found == null || found.size() < 1) {
            throw new TaskExecutionException("Could not find any appointment matching supplied username: " + username);
        }else if(found.size() > 1) {
            throw new TaskExecutionException("Found > 1 appointment matching supplied username: " + username);
        }
        
        return found.get(0);
    }
}
