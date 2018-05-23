package org.crossfit.app.i18n.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crossfit.app.domain.AbstractAuditingEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Table(name = "i18n_key")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class I18nKey extends AbstractAuditingEntity {


	@Id
    @GeneratedValue(generator = "i18n_key_seq")
    @SequenceGenerator(name = "i18n_key_seq",
            sequenceName = "i18n_key_id_seq", allocationSize = 20)
    private Long id;
	

	@Column(name="group_name")
	private String groupName;

	private String name;

	public I18nKey() {
		super();
	}

	public I18nKey(String name, String groupName) {
		this();
		this.name = name;
		this.groupName = groupName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
}
