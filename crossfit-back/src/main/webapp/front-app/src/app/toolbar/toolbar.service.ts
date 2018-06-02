import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs';


@Injectable()
export class ToolBarService {

    private title = new Subject<string>();
    private searchPlaceHolder = new Subject<string>();
    private _toggleSideNav = new Subject<void>();
 
    private allowSearch = new Subject<boolean>();

    private onSearch: Function;
 

    toggleSideNav(){
        this._toggleSideNav.next();
    }

    sideNavToggle(): Observable<void>{
        return this._toggleSideNav.asObservable();
    }

    setTitle(title: string) {
        this.title.next(title);
    }
    getTitle(): Observable<any> {
        return this.title.asObservable();
    }

    setSearchPlaceHolder(searchPlaceHolder: string) {
        this.searchPlaceHolder.next(searchPlaceHolder);
    }
    getSearchPlaceHolder(): Observable<string>{
        return this.searchPlaceHolder.asObservable();
    }
 
    setAllowSearch(allow: boolean, onSearch: Function) {
        this.allowSearch.next(allow);
        this.onSearch = onSearch;
    }
    getAllowSearch(): Observable<boolean>{
        return this.allowSearch.asObservable();
    }
    search(query: string){
        this.onSearch.call(this, query);
    }
}