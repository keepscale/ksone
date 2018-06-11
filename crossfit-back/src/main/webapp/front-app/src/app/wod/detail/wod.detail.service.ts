import { Injectable } from "@angular/core";
import { Wod } from "../domain/wod.model";
import { WodResult } from "../domain/wod-result.model";

@Injectable()
export class WodDetailService {

    public wod: Wod;
    public wodResults: WodResult[];
    


}