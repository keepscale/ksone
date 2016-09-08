package org.crossfit.app.web.rest.dto;

import java.io.Serializable;

public class BookingStatusDTO implements Serializable {

	private int max;
	private long count;

	private boolean hasSubscribeNotification;

	public BookingStatusDTO() {
		super();
	}

	public BookingStatusDTO(int max, long count, boolean hasSubscribeNotification) {
		super();
		this.max = max;
		this.count = count;
		this.hasSubscribeNotification = hasSubscribeNotification;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public boolean isHasSubscribeNotification() {
		return hasSubscribeNotification;
	}

	public void setHasSubscribeNotification(boolean hasSubscribeNotification) {
		this.hasSubscribeNotification = hasSubscribeNotification;
	}

}
