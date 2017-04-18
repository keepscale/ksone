package org.crossfit.app.web.rest.dto.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.crossfit.app.domain.resources.ResourceBooking;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ResourceStatsDTO {


	public static class ResaStats {
		@JsonSerialize(using = CustomDateTimeSerializer.class)
		private final DateTime startAt;
		
		@JsonSerialize(using = CustomDateTimeSerializer.class)
		private final DateTime endAt;

		public ResaStats(DateTime startAt, DateTime endAt) {
			super();
			this.startAt = startAt;
			this.endAt = endAt;
		}

		public DateTime getStartAt() {
			return startAt;
		}

		public DateTime getEndAt() {
			return endAt;
		}
	}
	
	public static class DateStats {

		@JsonSerialize(using = CustomLocalDateSerializer.class)
		private final LocalDate date;
		private final Long sumOfMinute;
		private final List<ResaStats> bookings;
		
		
		public DateStats(LocalDate date, Long sumOfMinute, List<ResaStats> bookings) {
			super();
			this.date = date;
			this.sumOfMinute = sumOfMinute;
			this.bookings = bookings;
		}
		public LocalDate getDate() {
			return date;
		}
		public Double getSumOfHours() {
			return (sumOfMinute / 60.0);
		}
		public List<ResaStats> getBookings() {
			return bookings;
		}
		
	}

	public static class MemberStats {
		private final MemberDTO member;
		private final List<DateStats> dates;
		public MemberStats(MemberDTO member, List<DateStats> dates) {
			super();
			this.member = member;
			this.dates = dates;
		}
		public MemberDTO getMember() {
			return member;
		}
		public List<DateStats> getDates() {
			return dates;
		}
		
	}
	
	private List<LocalDate> dates;
	private List<MemberDTO> members;
	private List<MemberStats> memberStats;

	public ResourceStatsDTO(List<ResourceBooking> resourceBookings) {
		super();
		Map<MemberDTO, Map<LocalDate, List<ResourceBooking>>> datas = resourceBookings.stream().collect(
				Collectors.groupingBy(b->{return MemberDTO.MAPPER.apply(b.getMember());}, 
					Collectors.groupingBy(r->{
						return r.getStartAt().withDayOfMonth(1).toLocalDate();
						}
					)
				)
			);	
		Comparator<? super MemberDTO> memberComp = (m1,m2)->{
			return (m1.getFirstName() + " " + m1.getLastName()).compareTo(m2.getFirstName() + " " + m2.getLastName());
		};
		members = resourceBookings.stream().map(b->{return MemberDTO.MAPPER.apply(b.getMember());}).distinct().collect(Collectors.toList());
		dates = resourceBookings.stream().map(b->{
			return b.getStartAt().withDayOfMonth(1).toLocalDate();
		}).distinct().sorted((d1,d2)->d2.compareTo(d1)).collect(Collectors.toList());
		
		memberStats = members.stream().map(m->{
			return new MemberStats(m, 
					dates.stream().map(d->{
						List<ResourceBooking> bookings = datas.get(m).getOrDefault(d, new ArrayList<>());
						
						List<ResaStats> resas = bookings.stream().map(b->new ResaStats(b.getStartAt(), b.getEndAt())).sorted((r1,r2)->r1.startAt.compareTo(r2.startAt)).collect(Collectors.toList());
						Long hours = bookings.stream().collect(Collectors.summingLong(b->{return Minutes.minutesBetween(b.getStartAt(), b.getEndAt()).getMinutes();}));
						
						return new DateStats(d, hours, resas);
					})
					.sorted((d1,d2)->d2.date.compareTo(d1.date))
					.collect(Collectors.toList()));
			}).sorted((m1,m2)->memberComp.compare(m1.member, m2.member)).collect(Collectors.toList());
	}

	public List<MemberStats> getMemberStats(){
		return memberStats;
	}
	
}
