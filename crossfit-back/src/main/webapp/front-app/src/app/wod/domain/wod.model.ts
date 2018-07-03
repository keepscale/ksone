import { Movement } from "./movement.model";
import { Equipment } from "./equipment.model";
import { WodResult } from "./wod-result.model";

export class WodPublication{
    
    public id: number;
    public startAt: Date;
    public endAt: Date;
    constructor(date?: Date){
        this.startAt = date;
        this.endAt = date;
    }

}
export class WodWhareProperties{
    
    public ownerId: number;
    public visibility: string;

}
export class Wod{
    public id: number;
    public name: string;
    
	public category:string;
    public score:string;
    
	public description:string;
	public link:string;
    public videoLink:string;
    
    public taggedMovements:Movement[] = [];
    public taggedEquipments:Equipment[] = [];
    
    public publications:WodPublication[] = [];

    public shareProperties:WodWhareProperties;

    myresults: WodResult[];
    myresultAtDate: WodResult;


    constructor(){
    }
}