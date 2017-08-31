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

import com.bc.appcore.ObjectFactory;
import com.bc.appcore.jpa.SelectionContext;
import com.bc.tasktracker.jpa.TasktrackerSelectionContext;
import com.bc.util.MapBuilder;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 31, 2017 5:24:29 PM
 */
public class TasktrackerCoreObjectFactory extends com.bc.appcore.ObjectFactoryImpl {

    public TasktrackerCoreObjectFactory(TasktrackerAppCore app) {
        super(app);
    }

    @Override
    public <T> T doGetOrException(Class<T> type) throws Exception {
        Object output;
        if(type.equals(SelectionContext.class)){
            output = new TasktrackerSelectionContext(this.getApp());
        }else if(type.equals(MapBuilder.class)){
            final MapBuilder mapBuilder = (MapBuilder)super.doGetOrException(type);
            output = mapBuilder.nullsAllowed(true).maxCollectionSize(0).maxDepth(3);
        }else if(type.equals(ObjectFactory.class)){
            output = new TasktrackerCoreObjectFactory(this.getApp());
        }else{
            output = super.doGetOrException(type);
        }  
        return (T)output;
    }

    @Override
    public TasktrackerAppCore getApp() {
        return (TasktrackerAppCore)super.getApp();
    }
}
