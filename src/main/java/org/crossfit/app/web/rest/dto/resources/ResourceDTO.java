package org.crossfit.app.web.rest.dto.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.AbstractAuditingEntity;
import org.crossfit.app.domain.resources.Resource;
import org.crossfit.app.web.rest.dto.MemberDTO;


/**
 * A ResourceDTO.
 */
public class ResourceDTO implements Serializable {

	public static final Function<Resource, ResourceDTO> MAPPER =  (resource)-> {
		ResourceDTO dto = new ResourceDTO();
		dto.setId(resource.getId());
		dto.setName(resource.getName());
		dto.setColor(resource.getColor());
		dto.setDescription(resource.getDescription());
		
		
		
		List<ResourceMemberRulesDTO> rulesdto = resource.getRules().stream().map(r->{
			ResourceMemberRulesDTO dto2 = new ResourceMemberRulesDTO();
			dto2.setId(r.getId());
			dto2.setMember(MemberDTO.MAPPER.apply(r.getMember()));
			return dto2;
		}).collect(Collectors.toList());
		
		dto.setRules(rulesdto);
		
		return dto;
	};
    private Long id;

    @NotNull        
    private String name;
    
    @NotNull     
    @Size(max = 32)
    private String color;
    
    @NotNull   
    @Size(max = 255)     
    private String description;
    
    private List<ResourceMemberRulesDTO> rules = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<ResourceMemberRulesDTO> getRules() {
		return rules;
	}

	public void setRules(List<ResourceMemberRulesDTO> rules) {
		this.rules = rules;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceDTO res = (ResourceDTO) o;

        if ( ! Objects.equals(id, res.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "Resource [id=" + id + ", name=" + name + ", description=" + description + ", rules="+rules+"]";
	}

}
