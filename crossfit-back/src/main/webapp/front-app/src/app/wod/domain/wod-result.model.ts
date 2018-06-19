import { Wod } from "./wod.model";

export class WodResult{
    public id: number;
    public date: Date;

    public wod: Wod;
    
	public totalLoadInKilo:number;
    public totalMinute:number;
    public totalSecond: number;
    
    public totalCompleteRound:number;
    public totalReps: number;

    public title: string;
    public category: string;

    constructor(){
    }
}