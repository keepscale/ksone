import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodService, WodSearchRequest } from '../wod.service';
import { Wod } from '../domain/wod.model';
import { Principal } from '../../shared/auth/principal.service';
import { Event } from '../../planning/event';
import { EventService } from '../../planning/event.service';
import { Router } from '@angular/router';
import * as moment from 'moment';


@Component({
  selector: 'app-wod-list',
  templateUrl: './wod-list.component.html',
  styleUrls: ['./wod-list.component.scss'],
  providers: [EventService]
})
export class WodListComponent implements OnInit {

  currentMemberId: number;
  wods:Wod[] = [];

  events: Event[];

  constructor(private toolbar: ToolBarService, 
    private wodService: WodService,
    private eventService: EventService,
    private router: Router,
    private principal: Principal) { }

  ngOnInit() {
    this.toolbar.setTitle("Wods")
    this.toolbar.setSearchPlaceHolder("Rechercher des wods")
    this.toolbar.setOnSearch(this.onSearch.bind(this));

    this.principal.identity().subscribe(p=>this.currentMemberId=p.id)

    this.eventService.eventRequested$.subscribe(req=>this.search(new WodSearchRequest(null, req.start, req.end)));

  }

  onSearch(query:string){
    console.log("Search: " + query);
    this.search(new WodSearchRequest(query));
  }
  
  search(search:WodSearchRequest){
    this.wodService.findAll(search).subscribe(result=>{
      this.wods=result;

      let tmp = [];
      this.wods.forEach(w=>{
        w.publications.forEach(pub=>{
          tmp.push(new Event(pub.date, w.name, w.description, w));
        })
      })
      this.events = tmp;
      this.eventService.setEvents(this.events);
    });
  }

  newWod(date: Date){
    this.router.navigate(["wod/new"], {queryParams:{'date':moment(date).format("YYYY-MM-DD")}})
  }
  editWod(event: Event){
    let wod: Wod = event.payload;
    this.router.navigate(["wod", wod.id, "detail"]);
  }

  isOwner(wod:Wod){
    return this.currentMemberId == wod.shareProperties.ownerId;
  }
}
