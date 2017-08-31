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
import com.bc.tasktracker.jpa.entities.master.Taskresponse;
import java.text.DateFormat;
import com.bc.tasktracker.TasktrackerAppCore;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 5:43:58 PM
 */
public class TaskresponseHtmlBuilder implements HtmlBuilder<Taskresponse> {

    private final TasktrackerAppCore app;
    
    private boolean buildAttempted;
    
    private Taskresponse taskresponse;

    public TaskresponseHtmlBuilder(TasktrackerAppCore app) {
        this.app = app;
    }

    @Override
    public HtmlBuilder<Taskresponse> with(Taskresponse task) {
        this.taskresponse = task;
        return this;
    }

    @Override
    public String build() {

        this.checkBuildAttempted();
        
        final DateFormat dateFormat = app.getDateTimeFormat();
        
        final String author = taskresponse.getAuthor().getAbbreviation();
        final String time = dateFormat.format(taskresponse.getTimemodified());
        final String response = taskresponse.getResponse();
        final String deadlineStr = taskresponse.getDeadline() == null ? null : 
                dateFormat.format(taskresponse.getDeadline());
        
        final HtmlGen htmlGen = new HtmlGen();
        final StringBuilder builder = new StringBuilder(10_000);
        
        final StringBuilder html = new StringBuilder();
        final String style = "font-size:12px; text-align:right; float:right;";
        htmlGen.tagStart("div", "style", style, html)
                .append("<tt>").append(author).append("</tt>&emsp;").append(time).append("</div>");
        html.append(response);
        if(deadlineStr != null) {
            htmlGen.tagStart("div", "style", style, html)
                    .append("Deadline: ").append(deadlineStr).append("</div>");
        }
        htmlGen.enclosingTag("div", html, builder);
        
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
