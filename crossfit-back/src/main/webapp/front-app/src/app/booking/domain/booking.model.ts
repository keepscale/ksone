import { WodResult } from "../../wod/domain/wod-result.model";

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

    results: WodResult[];
}