package org.crossfit.app.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Persistent tokens are used by Spring Security to automatically log in users.
 *
 * @see org.crossfit.app.security.CustomPersistentRememberMeServices
 */
@Entity
@Table(name = "PERSISTENT_TOKEN")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PersistentToken implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat
			.forPattern("d MMMM yyyy");

	private static final int MAX_USER_AGENT_LEN = 255;

	@Id
	private String series;

	@JsonIgnore
	@NotNull
	@Column(name = "token_value", nullable = false)
	private String tokenValue;

	@JsonIgnore
	@Column(name = "token_date")
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate tokenDate;

	// an IPV6 address max length is 39 characters
	@Size(min = 0, max = 39)
	@Column(name = "ip_address", length = 39)
	private String ipAddress;

	@Column(name = "user_agent")
	private String userAgent;

	@JsonIgnore
	@ManyToOne
	private Member member;

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public LocalDate getTokenDate() {
		return tokenDate;
	}

	public void setTokenDate(LocalDate tokenDate) {
		this.tokenDate = tokenDate;
	}

	@JsonGetter
	public String getFormattedTokenDate() {
		return DATE_TIME_FORMATTER.print(this.tokenDate);
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		if (userAgent.length() >= MAX_USER_AGENT_LEN) {
			this.userAgent = userAgent.substring(0, MAX_USER_AGENT_LEN - 1);
		} else {
			this.userAgent = userAgent;
		}
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
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

		PersistentToken that = (PersistentToken) o;

		if (!series.equals(that.series)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return series.hashCode();
	}

	@Override
	public String toString() {
		return "PersistentToken{" + "series='" + series + '\''
				+ ", tokenValue='" + tokenValue + '\'' + ", tokenDate="
				+ tokenDate + ", ipAddress='" + ipAddress + '\''
				+ ", userAgent='" + userAgent + '\'' + "}";
	}
}
