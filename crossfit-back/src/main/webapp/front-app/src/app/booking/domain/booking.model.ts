import { WodResult } from "../../wod/domain/wod-result.model";
import { Wod } from "../../wod/domain/wod.model";

export interface Booking{
    id: number;
    date: Date;
    startAt: Date;
    
	title:string;
    timeSlotId:number
    subscriptionId:number;

    createdAt: Date;
    memberId:number;

	cardUuid:string;
    checkInDate: Date;

    availableWods: Wod[];
}