package org.crossfit.app.web.rest.dto;

import java.util.function.Function;

import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.WodShareProperties;
import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;
import org.crossfit.app.domain.workouts.result.WodResult;
import org.joda.time.LocalDate;

public class WodDTO {
	
	public static Function<Wod, WodDTO> publicMapper(LocalDate date, WodResult myresultAtDate) {
		return wod->{
			WodDTO dto = new WodDTO();
			dto.setId(wod.getId());
			dto.setDate(date);
			dto.setName(wod.getName());
			dto.setCategory(wod.getCategory());
			dto.setDescription(wod.getDescription());
			dto.setScore(wod.getScore());
			dto.setShareProperties(wod.getShareProperties());
			dto.setMyresultAtDate(myresultAtDate);
			return dto;
		};
	}
	
	
	private Long id;
	private LocalDate date;

	private String name;
	private String description;

	private WodCategory category;
	private WodScore score;

	private WodShareProperties shareProperties;
	
	private WodResult myresultAtDate;

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


	public WodShareProperties getShareProperties() {
		return shareProperties;
	}

	public void setShareProperties(WodShareProperties shareProperties) {
		this.shareProperties = shareProperties;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public WodResult getMyresultAtDate() {
		return myresultAtDate;
	}

	public void setMyresultAtDate(WodResult myresultAtDate) {
		this.myresultAtDate = myresultAtDate;
	}

}
