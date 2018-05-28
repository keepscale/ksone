import { Component, OnInit } from '@angular/core';
import { AppService } from '../../app.service';

@Component({
  selector: 'app-wod-list',
  templateUrl: './wod-list.component.html',
  styleUrls: ['./wod-list.component.scss']
})
export class WodListComponent implements OnInit {

  constructor(private appService: AppService) { }

  ngOnInit() {
    this.appService.setTitle("Gestion des wods")
  }

}
