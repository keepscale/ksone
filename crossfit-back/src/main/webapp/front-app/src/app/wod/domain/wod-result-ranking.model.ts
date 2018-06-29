import { Movement } from "./movement.model";
import { Equipment } from "./equipment.model";

export class WodResultRanking{
    public id: number;
    public date: Date;
    public memberId: number;
    public displayName: string;
    public displayResult: string;
    public title: string;
    public category: string;

    constructor(){
    }
}