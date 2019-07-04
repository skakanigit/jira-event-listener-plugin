package com.raptor.jira.plugin;


import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@ExportAsService
@Component
public class IssueEventListener implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(IssueEventListener.class);

    @JiraImport
    private final EventPublisher eventPublisher;

    @Override
    public void destroy() throws Exception {
        LOG.info("Enabling plugin");
        eventPublisher.register(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Disabling plugin");
        eventPublisher.unregister(this);
    }

    @Autowired
    public IssueEventListener(EventPublisher eventPublisher){
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent){

        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();

        if(issue != null && eventTypeId != null){
            if(eventTypeId.equals(EventType.ISSUE_CREATED_ID)){
                LOG.info("Issue {} has created at {}", issue.getKey(), issue.getCreated());
            }else if(eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)){
                LOG.info("Issue {} has resolved at {}", issue.getKey(), issue.getResolutionDate());
            }
        }

    }
}
