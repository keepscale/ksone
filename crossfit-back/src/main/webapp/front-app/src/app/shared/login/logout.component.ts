import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Principal } from '../auth/principal.service';
import { BaseComponent } from 'src/app/common/base.component';
import { ToolBarService } from 'src/app/toolbar/toolbar.service';
import { RunnerService } from 'src/app/common/runner.service';
import { map, tap } from 'rxjs/operators';


@Component({
  template:"<p>Déconnexion en cours....</p>"
})
export class LogoutComponent extends BaseComponent implements OnInit, OnDestroy {



  constructor(
    private authService: AuthService, 
    private principal: Principal,  
    private route: ActivatedRoute, 
    private router: Router, 
    protected toolbar: ToolBarService,
    protected runner: RunnerService<any>) { 
    super(toolbar, runner);
  }

  ngOnInit() {
    this.title = "Déconnexion";
        
    this.authService.logout().subscribe(()=>{
      this.principal.logout();
      this.router.navigate(['/login']);
    });
    
  }

  ngOnDestroy(){

  }
}
