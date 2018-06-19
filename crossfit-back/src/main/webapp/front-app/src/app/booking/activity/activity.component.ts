import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { Booking } from '../domain/booking.model';
import { WodService } from '../../wod/wod.service';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.scss']
})
export class ActivityComponent implements OnInit {

  constructor(private bookingService:BookingService, private wodService:WodService) { }

  bookings: Booking[] = [];
  pastMonth: number=1;

  ngOnInit() {
    this.bookingService.findAllPastBooking(this.pastMonth).subscribe(
      res=>{
        this.bookings=res;
        this.wodService.findAllMyPastResult(this.pastMonth).subscribe(
          results=>{
            results.forEach(r=>{
              this.bookings.filter(b=>b.date==r.date).map(b=>{b.results = b.results || []; b.results.push(r); return b;});
            })
          }
        )
      }
    );
  }

}
