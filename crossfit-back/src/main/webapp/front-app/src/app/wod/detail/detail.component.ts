import { Component, OnInit } from '@angular/core';
import { WodService } from '../wod.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { Wod, WodPublication } from '../domain/wod.model';
import { ActivatedRoute, Router } from '@angular/router';
import { WodResult } from '../domain/wod-result.model';
import { Principal } from '../../shared/auth/principal.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {

  status: string;
  error: string;
  wod: Wod;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: WodService,
    private toolbar: ToolBarService, 
    private principal: Principal) { }

  ngOnInit() {
    this.toolbar.setTitle("Mes résultat");
    this.toolbar.setOnGoBack(this.goToSearch.bind(this));

    let wodId = +this.route.snapshot.paramMap.get('id');
    if (wodId){
      this.service.get(wodId).subscribe(w=>{
          this.wod = w;
        },
        err=>{
          this.router.navigate(["wod"]);
        }
      );
    }
  }

  goToSearch(){
    this.router.navigate(['wod']);
    return;
  }
}
