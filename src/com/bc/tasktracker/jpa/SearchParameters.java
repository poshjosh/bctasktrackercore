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

import com.bc.tasktracker.jpa.entities.master.Task_;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 14, 2017 10:23:45 PM
 */
public interface SearchParameters {
    String PARAM_JPACONTEXT = "jpaContext";
    String PARAM_RESULT_TYPE = "entityType";
    String PARAM_QUERY = "query";
    String PARAM_FROM = "from";
    String PARAM_TO = "to";
    String PARAM_OPENED = "opened";
    String PARAM_CLOSED = "closed";
    String PARAM_WHO = Task_.reponsibility.getName();
    String PARAM_DEADLINE_FROM = "deadlineFrom";
    String PARAM_DEADLINE_TO = "deadlineto";
}
