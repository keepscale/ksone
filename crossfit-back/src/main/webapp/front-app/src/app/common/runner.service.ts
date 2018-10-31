import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { ErrorService } from '../error/error.service';
import { ToolBarService } from '../toolbar/toolbar.service';

@Injectable({
  providedIn: 'root'
})
export class RunnerService<T> {

  constructor(
    private toolbar: ToolBarService,
    private error: ErrorService) { }

  forkJoin(observables: Observable<any>[], next?: (value: any[]) => void, overridedFailureMessage?: string){
    
    this.toolbar.setLoadingData(true);

    forkJoin(observables).subscribe(
      next,
      err=> {
        this.toolbar.setLoadingData(false);
        this.error.manage(err, overridedFailureMessage);
      },
      ()=>  this.toolbar.setLoadingData(false));
  }

  run(observable: Observable<T>, next?: (value: T) => void, overridedFailureMessage?: string){

    this.toolbar.setLoadingData(true);

    observable.subscribe(
      next,
      err=> {
        this.toolbar.setLoadingData(false);
        this.error.manage(err, overridedFailureMessage);
      },
      ()=>  this.toolbar.setLoadingData(false));
  }
}
