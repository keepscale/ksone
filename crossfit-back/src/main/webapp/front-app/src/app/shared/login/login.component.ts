import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Principal } from '../auth/principal.service';
import { ToolBarService } from '../../toolbar/toolbar.service';
import { BaseComponent } from 'src/app/common/base.component';
import { RunnerService } from 'src/app/common/runner.service';
import { mergeMap } from 'rxjs/operators';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent extends BaseComponent implements OnInit {

  username: string;
  password: string;
  rememberme: boolean;

  constructor(
    private authService: AuthService,  
    private router: Router, 
    private principal: Principal, 
    protected toolbar: ToolBarService,
    protected runner: RunnerService<any>) { 
    super(toolbar, runner);
  }

  ngOnInit() {
    this.title = "Connexion";
    this.rememberme = true;
    this.principal.identity(true).subscribe(res=>{
      if (res != null){
        this.redirectAfterLogin();
      }
    })
  }

  onLogin() {
    
    this.runner.run(
      this.authService.login(this.username, this.password, this.rememberme).pipe(
        mergeMap(result=>this.principal.identity(true))
      ),
      res=>this.redirectAfterLogin(),
      "Email ou mot de passe incorrect"
    );

  }

  redirectAfterLogin(){
    if (this.principal.hasAnyAuthority(['ROLE_COACH'])){
      this.router.navigate(['']);
    }
    else{
      this.router.navigate(['/account']);     
    }
  }

}
