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

package com.bc.tasktracker.functions;

import com.bc.config.Config;
import com.bc.tasktracker.ConfigNames;
import com.bc.tasktracker.jpa.entities.master.Appointment;
import com.bc.tasktracker.jpa.entities.master.Taskresponse;
import com.bc.tasktracker.jpa.predicates.TaskresponseAuthorTest;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 30, 2017 6:41:40 PM
 */
public class TaskreponseTestProvider implements BiFunction<Appointment, Config, Predicate<Taskresponse>> {

    public TaskreponseTestProvider() { }

    @Override
    public Predicate<Taskresponse> apply(Appointment appt, Config config) {
        
        if(config.getBoolean(ConfigNames.USER_SEES_ONLY_OWN_RESPONSES, false)) {
            
            return new TaskresponseAuthorTest(appt);
            
        }else{
            
            return (taskresponse) -> true;
        }
    }
}
