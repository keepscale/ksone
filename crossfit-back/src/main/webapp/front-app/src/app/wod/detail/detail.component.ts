import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';
import * as moment from 'moment';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

  error: string;
  wod: Wod;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private toolbar: ToolBarService, 
    private principal: Principal) { }

  ngOnInit() {
    this.toolbar.setLoadingData(true);
    this.toolbar.setTitle("Détail d'un wod");

    this.route.params.subscribe(params=>{
        this.service.get(params["id"]).subscribe(w=>{
          if (w==null) return;
          this.wod = w;
          this.wod.publications = this.wod.publications.sort((pub1,pub2)=>{return moment(pub2.endAt).diff(moment(pub1.endAt));});
        },
        err=>{
          this.toolbar.goBack();
        },
        ()=>this.toolbar.setLoadingData(false)
      );
    })
  }
}
