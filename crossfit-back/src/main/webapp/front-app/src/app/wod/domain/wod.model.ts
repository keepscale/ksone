import { Movement } from "./movement.model";
import { Equipment } from "./equipment.model";

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
    
    public dates:Date[] = []

    constructor(){
    }
}