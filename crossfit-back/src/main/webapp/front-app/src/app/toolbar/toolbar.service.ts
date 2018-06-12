import { Injectable, Testability } from '@angular/core';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs';


@Injectable()
export class ToolBarService {

    private title = new Subject<string>();
    private searchPlaceHolder = new Subject<string>();
 
    private allowSearch = new Subject<boolean>();

    private onSearch: Function;

    private menuItems = new Subject<any[]>();
 

    setTitle(title: string) {
        this.title.next(title);
    }
    getTitle(): Observable<any> {
        return this.title.asObservable();
    }

    addMenuItem(action: Function, icon:string, text:string){
        /*let i = new MenuItem();
        i.action = action;
        i.icon = icon;
        i.text = text;*/
    }

    getMenuItem(){
        return this.menuItems.asObservable();
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
}