import { Component } from '@angular/core';
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Principal } from './shared/auth/principal.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { AppService } from './app.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title: string;
  titleSubscription: Subscription;

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
  .pipe(
    map(result => result.matches)
  );

  constructor(public translate: TranslateService, private breakpointObserver: BreakpointObserver, private principal: Principal, private appService: AppService) {
    translate.addLangs(['en', 'fr']);
    translate.setDefaultLang('fr');

    const browserLang = translate.getBrowserLang();
    //translate.use(browserLang.match(/en|fr/) ? browserLang : 'fr');
    translate.use('fr');


    this.titleSubscription = this.appService.getTitle().subscribe(t=>this.title=t); 

  }


  private displayName;

  ngOnInit() {
    this.principal.identity().subscribe(res=>{
      this.displayName = res != null ? res.firstName : "";
    });
  }


  isAuthenticated(){
    return this.principal.isAuthenticated() || true;
  }

  isInRole(roleName: string): boolean{
    return this.principal.hasAnyAuthority([roleName]);
  }
  ngOnDestroy() {
    this.titleSubscription.unsubscribe();
  }
}
