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

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 8, 2017 11:25:32 PM
 */
public interface ConfigNames {
    
    String PERSISTENCE_UNIT_MASTER_REQUIRES_AUTH = "persistenceUnit.master.authenticationRequired";
    
    String PERSISTENCE_UNIT_SLAVE_REQUIRES_AUTH = "persistenceUnit.slave.authenticationRequired";
    
    String ABOUT_FILE_NAME = "aboutFilename";
    
    String LOGO_FILENAME = "logo.filename";
    
    String LOGO_DESCRIPTION = "logo.description";
    
    String USER_SEES_ONLY_OWN_RESPONSES = "userSeesOnlyOwnResponses";
    
    String DEADLINE_HOURS = "deadlineHours";
    
    String DEADLINE_REMINDER_INTERVAL_HOURS = "deadlineReminderIntervalHours";

    String DEFAULT_DEADLINE_EXTENSION_HOURS = "defaultDeadlineExtensionHours";
    
    String SEARCHRESULTS_PAGESIZE = "searchresultsPagesize";
    
    String SEARCHRESULTS_USECACHE = "searchresultsUsecache";
    
    String DATETIME_PATTERN = "dateTimePattern";
    
    String DATE_PATTERN = "datePattern";
    
    String REPORT_FOLDER_PATH = "reportsOutputDir";
    
    String SERIAL_COLUMNNAME = "serialColumnName";
}
