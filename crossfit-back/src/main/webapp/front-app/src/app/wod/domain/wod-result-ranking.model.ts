import { Movement } from "./movement.model";
import { Equipment } from "./equipment.model";

export class WodResultRanking{
    public id: number;
    public orderInCategory: number;
    public order: number;
    public displayName: string;
    public displayResult: string;
    public title: string;
    public category: string;

    constructor(){
    }
}