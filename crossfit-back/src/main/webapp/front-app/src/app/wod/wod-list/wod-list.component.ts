import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../../toolbar/toolbar.service';

@Component({
  selector: 'app-wod-list',
  templateUrl: './wod-list.component.html',
  styleUrls: ['./wod-list.component.scss']
})
export class WodListComponent implements OnInit {

  constructor(private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Wods")
    this.toolbar.setSearchPlaceHolder("Rechercher des wods")
    this.toolbar.setAllowSearch(true, this.onSearch.bind(this));
  }

  text: string;
  onSearch(query:string){
    console.log("Search: " + query);
    this.text = "Recherche de : " + query;
  }

}
