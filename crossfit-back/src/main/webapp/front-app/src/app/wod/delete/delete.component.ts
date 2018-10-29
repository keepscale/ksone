import { Component, OnInit } from '@angular/core';
import { Wod } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import * as moment from 'moment';
import { WodResultService } from '../wod-result.service';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  styleUrls: ['./delete.component.scss']
})
export class DeleteComponent implements OnInit {

  wod: Wod;
  countResult: number;
  enabledDeleteButton: boolean;
  error: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private wodResultService: WodResultService,
    private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Supprimer le wod");
    this.toolbar.setLoadingData(true);
    this.route.params.subscribe(params=>{
        this.service.get(params["id"]).subscribe(w=>{
          if (w==null) return;
          this.wod = w;
        },
        err=>{
          this.toolbar.goBack();
        },
        ()=>this.toolbar.setLoadingData(false)
      );
    })
  }

  onDelete(){
    this.toolbar.setLoadingData(true);
    this.service.delete(this.wod).subscribe(res=>{
      this.router.navigate(["wod/calendar"]);
    },
    err=>{
      this.error = err;
    },
    ()=>this.toolbar.setLoadingData(false))
  }

}
