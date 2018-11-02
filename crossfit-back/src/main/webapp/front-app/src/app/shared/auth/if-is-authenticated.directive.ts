import { Directive, Input, ViewContainerRef, TemplateRef, OnInit, OnDestroy } from '@angular/core';
import { Principal } from './principal.service';
import { tap, filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';


@Directive({
  selector: '[appAuthenticated]'
})
export class IfIsAuthenticatedDirective implements OnInit, OnDestroy {

  @Input('appAuthenticated') appAuthenticated: boolean = true;
  auth$: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private principal: Principal) {

  }

  ngOnInit() {/*
    this.auth$ = this.principal.identity()
    .pipe( tap(()       => this.viewContainer.clear()))
    .pipe( filter(user  => {
      if ( (this.appAuthenticated && this.principal.isAuthenticated())
       ||  (!this.appAuthenticated && !this.principal.isAuthenticated()) ){
        return true;
      }
      else{
        return false;
      }
    }))
    .subscribe(()       => this.viewContainer.createEmbeddedView(this.templateRef));
*/
    if ( (this.appAuthenticated && this.principal.isAuthenticated())
    ||  (!this.appAuthenticated && !this.principal.isAuthenticated()) ){
      this.viewContainer.createEmbeddedView(this.templateRef);
      
   }
  }

  ngOnDestroy() {
    if(this.auth$)
      this.auth$.unsubscribe();
  }

}
