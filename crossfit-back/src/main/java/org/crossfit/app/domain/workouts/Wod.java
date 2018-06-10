package org.crossfit.app.domain.workouts;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.AbstractAuditingEntity;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Member.
 */
@Entity
@Table(name = "WOD")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Wod extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@ManyToOne(optional = false, cascade = {})
	private CrossFitBox box;

	@Size(max = 255)
	@Column(name = "name", length = 255)
	private String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private WodCategory category;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "score", nullable = false)
	private WodScore score;

	@Size(max = 1024)
	@Column(name = "description", length = 1024)
	private String description;

	@Size(max = 512)
	@Column(name = "link", length = 512)
	private String link;

	@Size(max = 512)
	@Column(name = "video_link", length = 512)
	private String videoLink;

	@ManyToMany(fetch = FetchType.LAZY)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinTable(name = "WOD_MOVEMENT", joinColumns = @JoinColumn(name = "wod_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "movement_id", referencedColumnName = "ID"))
	private Set<Movement> taggedMovements;

	@ManyToMany(fetch = FetchType.LAZY)
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JoinTable(name = "WOD_EQUIPMENT", joinColumns = @JoinColumn(name = "wod_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "equipment_id", referencedColumnName = "ID"))
	private Set<Equipment> taggedEquipments;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="wod", cascade= {CascadeType.ALL})
	private Set<WodPublication> publications = new HashSet<>();

   
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CrossFitBox getBox() {
		return box;
	}

	public void setBox(CrossFitBox box) {
		this.box = box;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WodCategory getCategory() {
		return category;
	}

	public void setCategory(WodCategory category) {
		this.category = category;
	}

	public WodScore getScore() {
		return score;
	}

	public void setScore(WodScore score) {
		this.score = score;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public Set<Movement> getTaggedMovements() {
		return taggedMovements;
	}

	public void setTaggedMovements(Set<Movement> taggedMovements) {
		this.taggedMovements = taggedMovements;
	}

	public Set<Equipment> getTaggedEquipments() {
		return taggedEquipments;
	}

	public void setTaggedEquipments(Set<Equipment> taggedEquipments) {
		this.taggedEquipments = taggedEquipments;
	}

	public Set<WodPublication> getPublications() {
		return publications;
	}

	public void setPublications(Set<WodPublication> publications) {
		this.publications = publications;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wod other = (Wod) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
