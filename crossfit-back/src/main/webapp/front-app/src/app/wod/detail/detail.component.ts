import { Component, OnInit } from '@angular/core';
import { Wod } from '../domain/wod.model';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  
  private availableWodScore: String[] = ["test", "test2"];

  private wod: Wod;

  constructor() {
    this.wod = new Wod();
  }

  ngOnInit() {
  }

}
