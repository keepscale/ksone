package org.crossfit.app.web.rest.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.WodPublication;
import org.crossfit.app.domain.workouts.WodResult;
import org.crossfit.app.domain.workouts.WodShareProperties;
import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;

public class WodDTO {
	
	public static Function<Wod, WodDTO> publicMapper(Set<WodResult> myresult) {
		return wod->{
			WodDTO dto = new WodDTO();
			dto.setId(wod.getId());
			dto.setPublications(wod.getPublications());
			dto.setName(wod.getName());
			dto.setCategory(wod.getCategory());
			dto.setDescription(wod.getDescription());
			dto.setScore(wod.getScore());
			dto.setShareProperties(wod.getShareProperties());
			dto.setMyresults(myresult.stream().filter(res->res.getWod().equals(wod)).collect(Collectors.toSet()));
			return dto;
		};
	}
	
	
	private Long id;

	private String name;
	private String description;

	private WodCategory category;
	private WodScore score;

	private Set<WodPublication> publications = new HashSet<>();
	private WodShareProperties shareProperties;
	
	private Set<WodResult> myresults = new HashSet<>();

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

	public Set<WodPublication> getPublications() {
		return publications;
	}

	public void setPublications(Set<WodPublication> publications) {
		this.publications = publications;
	}

	public WodShareProperties getShareProperties() {
		return shareProperties;
	}

	public void setShareProperties(WodShareProperties shareProperties) {
		this.shareProperties = shareProperties;
	}

	public Set<WodResult> getMyresults() {
		return myresults;
	}

	public void setMyresults(Set<WodResult> myresults) {
		this.myresults = myresults;
	}
}
