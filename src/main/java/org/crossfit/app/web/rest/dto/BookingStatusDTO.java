package org.crossfit.app.web.rest.dto;

import java.io.Serializable;

public class BookingStatusDTO implements Serializable {

	private int max;
	private long count;

	private boolean hasSubscribeNotification;
	
	private boolean canSubscribeNotification;

	public BookingStatusDTO() {
		super();
	}

	public BookingStatusDTO(int max, long count, boolean hasSubscribeNotification, boolean canSubscribeNotification) {
		super();
		this.max = max;
		this.count = count;
		this.hasSubscribeNotification = hasSubscribeNotification;
		this.canSubscribeNotification = canSubscribeNotification;
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

	public boolean isCanSubscribeNotification() {
		return canSubscribeNotification;
	}

	public void setCanSubscribeNotification(boolean canSubscribeNotification) {
		this.canSubscribeNotification = canSubscribeNotification;
	}
}
