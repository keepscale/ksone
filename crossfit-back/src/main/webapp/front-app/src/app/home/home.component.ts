import { Component, OnInit } from '@angular/core';
import { ToolBarService } from '../toolbar/toolbar.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Accueil");
  }

}
