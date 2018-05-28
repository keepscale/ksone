import { Component } from '@angular/core';
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Principal } from './shared/auth/principal.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'app';

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
  .pipe(
    map(result => result.matches)
  );

  constructor(public translate: TranslateService, private breakpointObserver: BreakpointObserver, private principal: Principal) {
    translate.addLangs(['en', 'fr']);
    translate.setDefaultLang('fr');

    const browserLang = translate.getBrowserLang();
    translate.use(browserLang.match(/en|fr/) ? browserLang : 'fr');
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
}
