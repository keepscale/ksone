package org.crossfit.app.web.rest.dto.resources;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.crossfit.app.domain.resources.ResourceBooking;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.joda.time.Hours;
import org.joda.time.LocalDate;

public class Stats {

	public static class DateStats {
		private final LocalDate date;
		private final Long sumOfHours;
		public DateStats(LocalDate date, Long sumOfHours) {
			super();
			this.date = date;
			this.sumOfHours = sumOfHours;
		}
		public LocalDate getDate() {
			return date;
		}
		public Long getSumOfHours() {
			return sumOfHours;
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

	public Stats(List<ResourceBooking> resourceBookings) {
		super();
		Map<MemberDTO, Map<LocalDate, Long>> datas = resourceBookings.stream().collect(
				Collectors.groupingBy(b->{return MemberDTO.MAPPER.apply(b.getMember());}, 
					Collectors.groupingBy(r->{
						return r.getStartAt().withDayOfMonth(1).toLocalDate();
						}, 
						Collectors.summingLong(b->{return Hours.hoursBetween(b.getStartAt(), b.getEndAt()).getHours();})
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
						return new DateStats(d, datas.get(m).getOrDefault(d, 0L));
					})
					.sorted((d1,d2)->d2.date.compareTo(d1.date))
					.collect(Collectors.toList()));
			}).sorted((m1,m2)->memberComp.compare(m1.member, m2.member)).collect(Collectors.toList());
	}

	public List<MemberStats> getMemberStats(){
		return memberStats;
	}
	
}
