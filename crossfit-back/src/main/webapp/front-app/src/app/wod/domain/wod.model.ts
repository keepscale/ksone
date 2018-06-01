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
    
    public movements:Movement[] = [];
    public equipments:Equipment[] = [];
    
    constructor(){
    }
}