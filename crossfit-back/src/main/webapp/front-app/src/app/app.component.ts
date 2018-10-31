import { Component, ViewChild, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { BreakpointObserver, Breakpoints, BreakpointState, MediaMatcher } from '@angular/cdk/layout';
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
export class AppComponent  implements OnDestroy{


  mobileQuery: MediaQueryList;
  mobileQueryPortrait: MediaQueryList;
  private _mobileQueryListener: () => void;

  constructor(
    changeDetectorRef: ChangeDetectorRef, 
    media: MediaMatcher, 
    public toolbar: ToolBarService, 
    public translate: TranslateService, 
    private principal: Principal) {

    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this.mobileQueryPortrait = media.matchMedia('(max-width: 400px)');
    this._mobileQueryListener = () => {
      changeDetectorRef.detectChanges();
      this.toolbar.setCanCloseSideNav(this.mobileQuery.matches);
    }
    this.mobileQuery.addListener(this._mobileQueryListener);

    translate.addLangs(['en', 'fr']);
    translate.setDefaultLang('fr');

    const browserLang = translate.getBrowserLang();
    //translate.use(browserLang.match(/en|fr/) ? browserLang : 'fr');
    translate.use('fr');
  }

  ngOnDestroy(): void {
    this.mobileQuery.removeListener(this._mobileQueryListener);
  }

  ngOnInit() {
  }


  isAuthenticated(){
    return this.principal.isAuthenticated() || false;
  }

  isInRole(roleName: string): boolean{
    return this.principal.hasAnyAuthority([roleName]);
  }
}
