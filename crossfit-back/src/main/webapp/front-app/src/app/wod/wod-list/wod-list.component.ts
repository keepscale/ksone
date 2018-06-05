import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { WodService } from '../wod.service';
import { Wod } from '../domain/wod.model';

@Component({
  selector: 'app-wod-list',
  templateUrl: './wod-list.component.html',
  styleUrls: ['./wod-list.component.scss']
})
export class WodListComponent implements OnInit {

  private wods:Wod[] = [];

  constructor(private toolbar: ToolBarService, private wodService: WodService) { }

  ngOnInit() {
    this.toolbar.setTitle("Wods")
    this.toolbar.setSearchPlaceHolder("Rechercher des wods")
    this.toolbar.setOnSearch(this.onSearch.bind(this));

    this.onSearch(null);

  }

  onSearch(query:string){
    console.log("Search: " + query);
    this.wodService.findAll(query).subscribe(result=>this.wods=result);
  }

}
