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

import com.bc.appcore.jpa.SearchContextImpl;
import com.bc.tasktracker.ConfigNames;
import com.bc.jpa.dao.SelectDao;
import java.util.Objects;
import com.bc.tasktracker.TasktrackerAppCore;
import com.bc.appcore.jpa.model.EntityResultModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 20, 2017 8:05:21 PM
 */
public class TasktrackerSearchContextImpl<T> 
        extends SearchContextImpl<T> implements TasktrackerSearchContext<T>  {
    
    private final TasktrackerAppCore app;
    
    public TasktrackerSearchContextImpl(TasktrackerAppCore app, EntityResultModel<T> resultModel) {
        super(app, resultModel,
                app.getConfig().getInt(ConfigNames.SEARCHRESULTS_PAGESIZE, 20),
                app.getConfig().getBoolean(ConfigNames.SEARCHRESULTS_USECACHE, true));
        this.app = Objects.requireNonNull(app);
    }

    @Override
    public SelectDao<T> getSelectDao() {
        final SelectDao<T> selectDao = this.getSelectDaoBuilder().closed(false).build();
        return selectDao;
    }

    @Override
    public SelectDaoBuilder<T> getSelectDaoBuilder() {
        final Class<T> resultType = this.getResultType();
        final SelectDaoBuilder builder = new SelectDaoBuilderImpl();
        builder.resultType(resultType).persistenceUnitContext(app.getActivePersistenceUnitContext());
        return builder;
    }
}
