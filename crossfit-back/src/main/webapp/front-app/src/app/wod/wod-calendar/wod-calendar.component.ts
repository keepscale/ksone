import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodService, WodSearchRequest } from '../wod.service';
import { Wod } from '../domain/wod.model';
import { Principal } from '../../shared/auth/principal.service';
import { Router } from '@angular/router';
import * as moment from 'moment';
import { EventService } from 'src/app/agenda/event.service';
import { Event } from 'src/app/agenda/event';


@Component({
  selector: 'wod-calendar',
  templateUrl: './wod-calendar.component.html',
  styleUrls: ['./wod-calendar.component.scss'],
  providers: [EventService]
})
export class WodCalendarComponent implements OnInit {

  currentMemberId: number;
  wods:Wod[] = [];


  constructor(private toolbar: ToolBarService, 
    private wodService: WodService,
    private eventService: EventService,
    private router: Router,
    private principal: Principal) { }

  ngOnInit() {
    this.toolbar.setTitle("Planning des wods");

    this.principal.identity().subscribe(p=>this.currentMemberId=p.id);

    this.eventService.eventRequested$.subscribe(req=>this.search(new WodSearchRequest(null, req.start, req.end)));

  }
  
  search(search:WodSearchRequest){
    this.toolbar.setLoadingData(true);
    this.wodService.findAll(search).subscribe(
      result=>{
        this.wods=result;

        let events = [];
        this.wods.forEach(w=>{
          w.publications.forEach(pub=>{
            let start = moment(pub.startAt);
            let actual = moment(start);
            let end = moment(pub.endAt);
            let previous = null;
            let i = 0;
            do{
              let e = new Event(w.id, i, moment(actual), previous, start, end, w.name, w.description, w);
              previous = e;
              events.push(e);
              actual.add(1,'d');
              i++;
            }while(actual.isSameOrBefore(end))
          })
        })
        this.eventService.setEvents(events);
      },
      error=>{},
      ()=>{this.toolbar.setLoadingData(false);} 
    );
  }

  newWod(date: Date){
    this.router.navigate(["wod/create"], {queryParams:{'date':moment(date).format("YYYY-MM-DD")}})
  }
  editWod(event: Event){
    let wod: Wod = event.payload;
    this.router.navigate(["wod", wod.id, "detail"]);
  }

  isOwner(wod:Wod){
    return this.currentMemberId == wod.shareProperties.ownerId;
  }
}
