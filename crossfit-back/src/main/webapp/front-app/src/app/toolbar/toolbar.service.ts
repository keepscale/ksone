import { Injectable, Testability } from '@angular/core';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs';
import { MenuItem } from './menu-item.model';
import { Location } from '@angular/common';


@Injectable({
    providedIn: 'root'
})
export class ToolBarService {

    private title = new Subject<string>();
    private searchPlaceHolder = new Subject<string>();

    private allowCloseSideNav = new Subject<boolean>();
 
    private allowSearch = new Subject<boolean>();
    private onSearch: Function;

    private allowGoBack = new Subject<boolean>();

   /* 
    private onGoBack: Function;
*/
    private menuItemsAdded = new Subject<MenuItem>();
 
    private loadingData = new Subject<boolean>();

    
  constructor(private _location: Location) { }

    setTitle(title: string) {
        this.title.next(title);
    }
    getTitle(): Observable<any> {
        return this.title.asObservable();
    }
    
    setLoadingData(loading: boolean) {
        this.loadingData.next(loading);
    }
    getLoadingData(): Observable<boolean> {
        return this.loadingData.asObservable();
    }

    addMenuItem(action: Function, icon:string, text:string){
        let i = new MenuItem();
        i.action = action;
        i.icon = icon;
        i.text = text;
        this.menuItemsAdded.next(i);
    }

    getMenuItemAdded(){
        return this.menuItemsAdded.asObservable();
    }

    setSearchPlaceHolder(searchPlaceHolder: string) {
        this.searchPlaceHolder.next(searchPlaceHolder);
    }
    getSearchPlaceHolder(): Observable<string>{
        return this.searchPlaceHolder.asObservable();
    }
 
    setOnSearch(onSearch: Function) {
        this.allowSearch.next(true);
        this.onSearch = onSearch;
    }
    getAllowSearch(): Observable<boolean>{
        return this.allowSearch.asObservable();
    }
    search(query: string){
        this.onSearch.call(this, query);
    }

    setCanCloseSideNav(value: boolean){
        this.allowCloseSideNav.next(value);
    }
    getAllowCloseSideNav(): Observable<boolean>{
        return this.allowCloseSideNav.asObservable();
    }


    /*
    setOnGoBack(onGoBack: Function) {
        this.allowGoBack.next(true);
        this.onGoBack = onGoBack;
    }
    getAllowGoBack(): Observable<boolean>{
        return this.allowGoBack.asObservable();
    }
    goBack(){
        this.onGoBack.call(this);
    }
    */

    setAllowGoBack(allow: boolean) {
        this.allowGoBack.next(allow);
    }
    getAllowGoBack(): Observable<boolean>{
        return this.allowGoBack.asObservable();
    }
    goBack(){
        this._location.back();
    }
}