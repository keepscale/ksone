import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs';


@Injectable()
export class AppService {
    private subjectAppTitle = new Subject<any>();
 
    setTitle(title: string) {
        this.subjectAppTitle.next(title);
    }
 
    getTitle(): Observable<any> {
        return this.subjectAppTitle.asObservable();
    }
}