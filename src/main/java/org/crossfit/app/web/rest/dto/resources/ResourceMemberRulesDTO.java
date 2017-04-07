package org.crossfit.app.web.rest.dto.resources;

import java.io.Serializable;
import java.util.Objects;

import org.crossfit.app.web.rest.dto.MemberDTO;


public class ResourceMemberRulesDTO implements Serializable {

    private Long id;

    private MemberDTO member;
    
    //Ici, on peut eventuellement ajouter des regles de reservation
    //pour  un membre et la resource
    //private int hourPerMonth;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MemberDTO getMember() {
		return member;
	}

	public void setMember(MemberDTO member) {
		this.member = member;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceMemberRulesDTO resourceMember = (ResourceMemberRulesDTO) o;

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
                '}';
    }
}
