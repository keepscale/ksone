import { Movement } from "./movement.model";
import { Equipment } from "./equipment.model";

export class Wod{
    private id: number;
    private name: string;
    
	private category:string;
    private score:string;
    
	private description:string;
	private link:string;
    private videoLink:string;
    
    movements:Movement[] = [];
    equipments:Equipment[] = [];
    
    constructor(){
    }

    public getId():number{
        return this.id;
    }
}