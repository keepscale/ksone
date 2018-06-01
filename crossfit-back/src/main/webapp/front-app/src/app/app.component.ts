import { Component, ViewChild } from '@angular/core';
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Principal } from './shared/auth/principal.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ToolBarService } from './toolbar/toolbar.service';
import { MatDrawerContent, MatSidenav } from '@angular/material';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  @ViewChild(MatSidenav)
  private sideNav: MatSidenav;

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
  .pipe(
    map(result => result.matches)
  );

  constructor(
    private breakpointObserver: BreakpointObserver, 
    public translate: TranslateService, 
    private principal: Principal, 
    private toolbar: ToolBarService) {

    translate.addLangs(['en', 'fr']);
    translate.setDefaultLang('fr');

    const browserLang = translate.getBrowserLang();
    //translate.use(browserLang.match(/en|fr/) ? browserLang : 'fr');
    translate.use('fr');

    this.toolbar.sideNavToggle().subscribe(o=>this.sideNav.toggle());
  }


  private displayName;

  ngOnInit() {
    this.principal.identity().subscribe(res=>{
      this.displayName = res != null ? res.firstName : "";
    });
  }


  isAuthenticated(){
    return this.principal.isAuthenticated() || false;
  }

  isInRole(roleName: string): boolean{
    return this.principal.hasAnyAuthority([roleName]);
  }
}
