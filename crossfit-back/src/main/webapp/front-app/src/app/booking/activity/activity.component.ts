import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { Booking } from '../domain/booking.model';
import { WodService } from '../../wod/wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodResult } from '../../wod/domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.scss']
})
export class ActivityComponent implements OnInit {

  constructor(private toolbar: ToolBarService, private bookingService:BookingService, private wodService:WodService, 
    private principal: Principal) { }

  bookings: Booking[] = [];
  pastMonth: number=1;


  ngOnInit() {
    this.toolbar.setTitle("Mon activité")
    this.bookingService.findAllPastBooking(this.pastMonth).subscribe(
      res=>{
        this.bookings=res;
      }
    );
  }

  findMyResultAtDate(results:WodResult[], at:Date){
    let res = results.filter(r=>r.date === at);
    if (res.length > 0){
      return res[0];
    }
    else{
      return null;
    }
  }
}
