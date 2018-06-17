import { Movement } from "./movement.model";
import { Equipment } from "./equipment.model";

export class WodResult{
    public id: number;
    public date: Date;
    
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