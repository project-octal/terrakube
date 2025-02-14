package org.terrakube.api.rs.tag;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.workspace.tag.WorkspaceTag;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;
import java.util.List;

@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Tag extends GenericAuditFields {
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private Organization organization;

}
