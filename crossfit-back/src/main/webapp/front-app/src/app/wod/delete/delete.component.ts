import { Component, OnInit } from '@angular/core';
import { Wod } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import * as moment from 'moment';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  styleUrls: ['./delete.component.scss']
})
export class DeleteComponent implements OnInit {

  wod: Wod;
  countResult: number;
  enabledDeleteButton: boolean;
  status: string;
  error: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Supprimer le wod");
    this.toolbar.setOnGoBack(()=>{
      this.router.navigate(['wod', this.wod.id, 'detail']);
      }
    );
    
    let wodId = +this.route.snapshot.paramMap.get('id');
    if (wodId){
      this.service.get(wodId).subscribe(w=>{
          this.wod = w;
        },
        err=>{
          this.toolbar.goBack();
        }
      );

      this.service.getRanking(wodId).subscribe(ranking=>{
        this.countResult = ranking.length;
        this.enabledDeleteButton = this.countResult === 0;
      });
    }
    else{
      this.toolbar.goBack();
    }
  }

  onDelete(){
    this.status = "wait";
    this.error = "";
    this.service.delete(this.wod).subscribe(res=>{
      this.router.navigate(["wod"]);
    },
    err=>{
      this.status = "error";
      this.error = err;
    })
  }

}
