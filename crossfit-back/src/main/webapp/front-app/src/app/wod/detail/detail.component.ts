import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';
import { WodDetailService } from './wod.detail.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

  private status: string;
  private error: string;
  private wod: Wod;
  private wodResults: WodResult[];

  private result: any = {};
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private wodDetailService: WodDetailService,
    private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Mes résultat");
    let wodId = +this.route.snapshot.paramMap.get('id');
    if (wodId){
      this.service.get(wodId).subscribe(w=>{
          this.wod = w;
          this.wodDetailService.wod = this.wod;
          this.service.getMyResult(wodId).subscribe(res=>{
            this.wodResults=res;
            this.wod.publications.forEach(publi => {              
              if (this.wodResults.filter(r=>r.date==publi.date).length===0){
                let result = new WodResult();
                result.date = publi.date;
                this.wodResults.push(result);
                return result;
              }
            });
            this.wodResults.sort((r1,r2)=>r1.date.getDate() - r2.date.getDate());
            this.wodDetailService.wodResults = this.wodResults;
          });
        },
        err=>{
          this.router.navigate(["wod"]);
        }
      );
    }
  }


  onSubmit(){
    this.service.saveMyResult(this.wod, this.wodResults).subscribe();
  }
}
