package org.azbuilder.server.job.rs.model.organization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relationships {
    JobData job;
    ModuleData module;
    WorkspaceData workspace;
}
