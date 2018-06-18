import { Movement } from "./movement.model";
import { Equipment } from "./equipment.model";

export class WodPublication{
    
    public id: number;
    public date: Date;
    constructor(date?: Date){
        this.date = date;
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

    constructor(){
    }
}