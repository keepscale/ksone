import * as moment from 'moment';

export class Event{
    refId: any;
    date: moment.Moment;
    start: moment.Moment;
    end: moment.Moment;
    positionInDay: number = null;
    private _prevEvent: Event;
    title: string;
    detail: string;

    payload: any;
    
    constructor(refId:any, date: moment.Moment | Date, previousEvent: Event, start: moment.Moment | Date, end: moment.Moment | Date, title: string, detail?: string, payload?: any){
        this.refId = refId;
        this.date = moment(date);
        this.start = moment(start);
        this.end = moment(end);
        this._prevEvent = previousEvent;
        this.title = title;
        this.detail = detail;
        this.payload = payload;
    }

    get positionOfPreviousEvent(){
        return this._prevEvent.positionInDay;
    } 

    get isFirstEventOccurence(){
        return this._prevEvent == null;
    }
}

export class Day{
    date: moment.Moment;
    events: Event[];

    constructor(date: moment.Moment){
        this.date = moment(date);
    }

    get eventsRangePosition() : number[]{
        let max = Math.max.apply(null, this.events.map(e=>e.positionInDay));
        return Array.from(Array(max).keys());
    }

    public getEventAtPosition(position: number): Event{
        return this.events.find(e=>e.positionInDay == position);
    }
}