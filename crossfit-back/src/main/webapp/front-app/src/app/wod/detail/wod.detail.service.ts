import { Injectable } from "@angular/core";
import { Wod } from "../domain/wod.model";
import { WodResult } from "../domain/wod-result.model";
import { Subject } from "rxjs";

@Injectable()
export class WodDetailService {

    public wod: Wod;
    public wodResults: WodResult[] = [];
    
    private resultSavedSource = new Subject<WodResult>();
    resultSaved$ = this.resultSavedSource.asObservable();

    public getMyResult(date:Date){
        let resultsMatch = this.wodResults.filter(r=>{
            let dateR = new Date(r.date);
            return dateR.getDate() == date.getDate();
        });
        if (resultsMatch.length == 1){
            return resultsMatch[0];
        }
        return null;
    }

    saveResult(wodResult: WodResult, newResult: WodResult){
        Object.assign(wodResult, newResult);
        this.resultSavedSource.next(wodResult);
    }
}