import { Directive, ViewContainerRef, TemplateRef, Input, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { tap, filter } from 'rxjs/operators';
import { Principal } from './principal.service';

@Directive({
  selector: '[hasAnyRole]'
})
export class IfHasAnyRoleDirective implements OnInit, OnDestroy {

  auth$: Subscription;

  @Input('hasAnyRole') roles: string[]|string;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private principal: Principal) {

  }

  ngOnInit() {
    
    this.auth$ = this.principal.identity()
    .pipe( tap(()       => this.viewContainer.clear()))
    .pipe( filter(user  => this.principal.hasAnyAuthority(this.roles)))
    .subscribe(()       => this.viewContainer.createEmbeddedView(this.templateRef));
    
  }

  ngOnDestroy() {
    if(this.auth$)
      this.auth$.unsubscribe();
  }

}
