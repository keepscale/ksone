package org.crossfit.app.web.rest.dto.bills;

import java.text.SimpleDateFormat;

import org.crossfit.app.domain.Bill;

public class BillPeriodDTO {
		private static final SimpleDateFormat sdfShort = new SimpleDateFormat("YYYY-MM");
		private static final SimpleDateFormat sdfLong = new SimpleDateFormat("YYYY MMMM");
		private final String shortFormat, longFormat;
		
		public BillPeriodDTO(Bill bill) {
			this.shortFormat = sdfShort.format(bill.getEffectiveDate().toDate());
			this.longFormat = sdfLong.format(bill.getEffectiveDate().toDate());
		}

		public String getShortFormat() {
			return shortFormat;
		}

		public String getLongFormat() {
			return longFormat;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((longFormat == null) ? 0 : longFormat.hashCode());
			result = prime * result + ((shortFormat == null) ? 0 : shortFormat.hashCode());
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
			BillPeriodDTO other = (BillPeriodDTO) obj;
			if (longFormat == null) {
				if (other.longFormat != null)
					return false;
			} else if (!longFormat.equals(other.longFormat))
				return false;
			if (shortFormat == null) {
				if (other.shortFormat != null)
					return false;
			} else if (!shortFormat.equals(other.shortFormat))
				return false;
			return true;
		}
	}