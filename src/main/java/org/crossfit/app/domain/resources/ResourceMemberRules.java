package org.crossfit.app.domain.resources;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.AbstractAuditingEntity;
import org.crossfit.app.domain.Member;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * A MembershipType.
 */
@Entity
@Table(name = "RESOURCE_MEMBER_RULES")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceMemberRules extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(optional=false, cascade = {})
    private Member member;

    @NotNull
    @ManyToOne(optional=false, cascade = {})
    private Resource resource;
    
    //Ici, on peut eventuellement ajouter des regles de reservation
    //pour  un membre et la resource
    //private int hourPerMonth;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceMemberRules resourceMember = (ResourceMemberRules) o;

        if ( ! Objects.equals(id, resourceMember.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ResourceMemberAllowed{" +
                "id=" + id +
                ", membre='" + (member == null ? "null" : member.getId() )+ "'" +
                ", resource='" + (resource == null ? "null" : resource.getId()) + "'" +
                '}';
    }
}
