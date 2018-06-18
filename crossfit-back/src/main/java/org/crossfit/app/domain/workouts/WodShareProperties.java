package org.crossfit.app.domain.workouts;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.workouts.enumeration.WodVisibility;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Embeddable
@Table(name = "WOD_SHARE_PROPERTIES")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WodShareProperties {
		
	@JsonIgnore
	@NotNull
	@ManyToOne(optional = false, cascade = {})
	private Member owner;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "visibility", nullable = false)
	private WodVisibility visibility = WodVisibility.PUBLIC;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinTable(name = "WOD_SHARE_WITH", joinColumns = @JoinColumn(name = "wod_id", referencedColumnName = "ID"), 
		inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "ID"))
	private Set<Member> shareWith;

	public Member getOwner() {
		return owner;
	}

	public void setOwner(Member owner) {
		this.owner = owner;
	}
	
	public Long getOwnerId() {
		return this.owner.getId();
	}

	public WodVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(WodVisibility visibility) {
		this.visibility = visibility;
	}

	public Set<Member> getShareWith() {
		return shareWith;
	}

	public void setShareWith(Set<Member> shareWith) {
		this.shareWith = shareWith;
	}
}