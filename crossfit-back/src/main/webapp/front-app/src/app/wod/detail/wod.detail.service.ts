import { Injectable } from "@angular/core";
import { Wod } from "../domain/wod.model";
import { WodResult } from "../domain/wod-result.model";

@Injectable()
export class WodDetailService {

    public wod: Wod;
    public wodResults: WodResult[];
    

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

}