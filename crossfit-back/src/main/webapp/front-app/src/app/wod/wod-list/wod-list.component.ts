import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodService } from '../wod.service';
import { Wod } from '../domain/wod.model';
import { Principal } from '../../shared/auth/principal.service';
import { Event } from '../../planning/event';
import { EventService } from '../../planning/event.service';

@Component({
  selector: 'app-wod-list',
  templateUrl: './wod-list.component.html',
  styleUrls: ['./wod-list.component.scss']
})
export class WodListComponent implements OnInit {

  currentMemberId: number;
  wods:Wod[] = [];

  events: Event[];

  constructor(private toolbar: ToolBarService, 
    private wodService: WodService,
    private eventService: EventService,
    private principal: Principal) { }

  ngOnInit() {
    this.toolbar.setTitle("Wods")
    this.toolbar.setSearchPlaceHolder("Rechercher des wods")
    this.toolbar.setOnSearch(this.onSearch.bind(this));

    this.principal.identity().subscribe(p=>this.currentMemberId=p.id)

    this.eventService.eventRequested$.subscribe(req=>this.onSearch(null));

  }

  onSearch(query:string){
    console.log("Search: " + query);
    this.wodService.findAll(query).subscribe(result=>{
      this.wods=result;

      let tmp = [];
      this.wods.forEach(w=>{
        w.publications.forEach(pub=>{
          tmp.push(new Event(pub.date, w.name, w.description));
        })
      })
      this.events = tmp;
      this.eventService.setEvents(this.events);
    });
  }

  isOwner(wod:Wod){
    return this.currentMemberId == wod.shareProperties.ownerId;
  }
}
