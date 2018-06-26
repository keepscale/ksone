import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { Booking } from '../domain/booking.model';
import { WodService } from '../../wod/wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodResult } from '../../wod/domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';
import { Wod } from '../../wod/domain/wod.model';
import { Router, ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';

export class BookingsDay{
  date: Date;
  bookings: Booking[];
  constructor(date:Date){
    this.date = date;
    this.bookings = [];
  }
}

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.scss']
})
export class ActivityComponent implements OnInit {

  constructor(private toolbar: ToolBarService, 
    private bookingService:BookingService, 
    private wodService:WodService, 
    private router: Router,
    private route: ActivatedRoute,
    private principal: Principal) { }

  bookings: BookingsDay[] = [];
  pastMonth: number=1;

  currentBookingsDay: BookingsDay;
  
  status: string;
  error: string;

  wods: Wod[] = [];

  ngOnInit() {
    this.toolbar.setTitle("Mon activité");
    this.status="wait";
    this.bookingService.findAllPastBooking(this.pastMonth).subscribe(
      success=>{
          this.bookings=success.reduce(function(map:BookingsDay[], booking, index, array){
            let bday= map.filter(bday=>bday.date===booking.date)[0];
            if (bday==null){
              bday = new BookingsDay(booking.date);
              map.push(bday);
            }
            bday.bookings.push(booking);
            return map;
          },[]);
          
          this.route.paramMap.subscribe(map=>{
            let match = this.bookings.filter(b=>b.date+""===map.get("date"));
            if (match.length==1){
              this.loadBookingDate(match[0]);
            }
          });
          
          this.status = "success";
      },
      e=>{
        this.status = "error";
        this.error = e.error;
    });

  }
  
  openBookingsDay(b: BookingsDay){
    this.router.navigate(["activity", {'date': b.date}]);
  }

  loadBookingDate(b: BookingsDay){
    this.wods = [];
    this.status="wait";

    this.wodService.findAllWodAtDateWithMyResult(b.date).subscribe(res=>{      
      this.wods = res;
      this.currentBookingsDay = b;
      this.status = "success";
    });
  }

  onSaveResult(wod: Wod, updatedResult: WodResult){
    if (wod.myresultAtDate == null){
      wod.myresultAtDate = updatedResult;
    }
    else{
      Object.assign(wod.myresultAtDate, updatedResult);
    }
  }
  onDeleteResult(wod: Wod){
    wod.myresultAtDate = null;
  }
}
