import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodService } from '../wod.service';
import { Wod } from '../domain/wod.model';
import { Principal } from '../../shared/auth/principal.service';

@Component({
  selector: 'app-wod-list',
  templateUrl: './wod-list.component.html',
  styleUrls: ['./wod-list.component.scss']
})
export class WodListComponent implements OnInit {

  private currentMemberId: number;
  private wods:Wod[] = [];

  constructor(private toolbar: ToolBarService, 
    private wodService: WodService, 
    private principal: Principal) { }

  ngOnInit() {
    this.toolbar.setTitle("Wods")
    this.toolbar.setSearchPlaceHolder("Rechercher des wods")
    this.toolbar.setOnSearch(this.onSearch.bind(this));

    this.principal.identity().subscribe(p=>this.currentMemberId=p.id)

    this.onSearch(null);


  }

  onSearch(query:string){
    console.log("Search: " + query);
    this.wodService.findAll(query).subscribe(result=>this.wods=result);
  }

  isOwner(wod:Wod){
    return this.currentMemberId == wod.shareProperties.ownerId;
  }
}
