import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ErrorService } from '../error/error.service';
import { ToolBarService } from '../toolbar/toolbar.service';

@Injectable({
  providedIn: 'root'
})
export class RunnerService<T> {

  constructor(
    private toolbar: ToolBarService,
    private error: ErrorService) { }


  run(observable: Observable<T>, next?: (value: T) => void){

    this.toolbar.setLoadingData(true);

    observable.subscribe(
      next,
      err=> this.error.manage(err),
      ()=>  this.toolbar.setLoadingData(false));
  }
}
