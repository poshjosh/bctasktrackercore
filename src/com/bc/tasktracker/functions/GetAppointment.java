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

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.tasktracker.jpa.entities.master.Appointment;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 16, 2017 11:02:10 PM
 */
public class GetAppointment implements BiFunction<PersistenceUnitContext, Object, Appointment> {

    @Override
    public Appointment apply(PersistenceUnitContext jpa, Object value) {
        Objects.requireNonNull(value);
        final Appointment author;
        if(value instanceof Appointment) {
            author = (Appointment)value;
        }else if(value instanceof Number){
           
            author = jpa.getDao().find(Appointment.class, ((Number)value).intValue());
        }else{
            final List<Appointment> found = jpa.getTextSearch()
                    .search(Appointment.class, value.toString());
            if(found.size() > 1) {
                throw new IllegalArgumentException("Found > 1 matching " + 
                        Appointment.class.getSimpleName() + " for: " + value);
            }
            author = found.get(0);
        }
        Objects.requireNonNull(author);
        return author;
    }
}
