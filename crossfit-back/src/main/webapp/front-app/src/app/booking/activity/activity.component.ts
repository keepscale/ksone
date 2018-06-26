import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { Booking } from '../domain/booking.model';
import { WodService } from '../../wod/wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodResult } from '../../wod/domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';
import { Wod } from '../../wod/domain/wod.model';
import { Router, ActivatedRoute } from '@angular/router';

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

  openedDate: string;
  
  status: string;
  error: string;

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
          this.status = "success";
      },
      e=>{
        this.status = "error";
        this.error = e.error;
    });

    this.route.paramMap.subscribe(map=>{
      this.openedDate=map.get("date");
      console.log(this.openedDate);
    });
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

  openBooking(b: Booking){
    this.router.navigate(["activity", {'date': b.date}]);
  }


  onUpdateResult(result: WodResult, updatedResult: WodResult){
    Object.assign(result, updatedResult);
  }
  onCreateResult(wod: Wod, createdResult: WodResult){
    if (!wod.myresults){
      wod.myresults = [];
    }
    wod.myresults.push(createdResult);
  }
  onDeleteResult(wod: Wod, deleteResult: WodResult){
    wod.myresults.splice(wod.myresults.indexOf(deleteResult), 1);
  }
}
