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

package com.bc.tasktracker.html;

import com.bc.appcore.html.HtmlBuilder;
import com.bc.html.HtmlGen;
import com.bc.tasktracker.jpa.entities.master.Appointment;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Taskresponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Predicate;
import com.bc.tasktracker.TasktrackerAppCore;
import com.bc.tasktracker.functions.TaskreponseTestProvider;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 2:20:04 AM
 */
public class TaskHtmlBuilder implements HtmlBuilder<Task> {

    private final TasktrackerAppCore app;
    
    private boolean buildAttempted;
    
    private Task task;

    public TaskHtmlBuilder(TasktrackerAppCore app) {
        this.app = app;
    }

    @Override
    public HtmlBuilder<Task> with(Task task) {
        this.task = task;
        return this;
    }

    @Override
    public String build() {
        
        this.checkBuildAttempted();
        
        final String from = task.getAuthor().getAppointment();
        final String to = task.getReponsibility().getAppointment();
        final String desc = task.getDescription();
        
        final String timeStr = task.getTimeopened() == null ? null : 
                app.getDateTimeFormat().format(task.getTimeopened());
        
        final HtmlGen htmlGen = new HtmlGen();
        htmlGen.setUseNewLine(true);
        final StringBuilder builder = new StringBuilder(10000);
        
        htmlGen.tagStart("div", "style", "width:100%; font-size:16px;", builder);
        
        final String timePart = timeStr == null ? "" : "<tt>" + timeStr + "</tt><br/>";
        htmlGen.enclosingTag("div", timePart + "<i>"+from+"</i>&emsp;<b>&gt;&nbsp;&gt;&nbsp;&gt;</b>&emsp;<i>" + to + "</i>", builder);
        htmlGen.enclosingTag("p", "style", "font-weight:900;", desc, builder);
        builder.append("<br/>");
        
        final List<Taskresponse> responseList = task.getTaskresponseList();
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                "Task has {0} responses", responseList == null ? null :responseList.size());
        
        if(responseList != null && !responseList.isEmpty()) {
            
            builder.append("<table style=\"width:100%\">");
            
            final Appointment currUserAppt = app.getUserAppointment(null);
            
            final Predicate<Taskresponse> taskresponseFilter = 
                    new TaskreponseTestProvider().apply(currUserAppt, app.getConfig());
            
            
            
            for(Taskresponse response : responseList) {
                
                if(!taskresponseFilter.test(response)) {
                    continue;
                }
                
                final boolean currentUserIsAuthor = response.getAuthor().equals(currUserAppt);
                
                final String responseBody = this.builder(Taskresponse.class).with(response).build();
                
                builder.append("<tr>");
                if(!currentUserIsAuthor) {
                    builder.append("<td></td>");
                }
                htmlGen.enclosingTag("td", "style", "border:1px solid gray;", responseBody, builder);
                if(currentUserIsAuthor) {
                    builder.append("<td></td>");
                }
                builder.append("</tr>");
            }
            
            builder.append("</table>");
            
        }else{
            
            htmlGen.enclosingTag("p", "style", "width:100%", "There are no deadlines or reponses to this task", builder);
        }
        
        htmlGen.tagEnd("div", builder);

//System.out.println("-------------------------------------------------------");        
//System.out.println(builder);
//System.out.println("-------------------------------------------------------");        
        return builder.toString();
    }

    @Override
    public <B> HtmlBuilder<B> builder(Class<B> builderType) {
        return app.getHtmlBuilder(builderType);
    }
    
    private void checkBuildAttempted() {
        if(buildAttempted) {
            throw new IllegalStateException("build method may be called only once");
        }
        buildAttempted = true;
    }
}
