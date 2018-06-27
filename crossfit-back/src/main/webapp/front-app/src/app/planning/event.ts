import * as moment from 'moment';

export class Event{
    date: moment.Moment;
    title: string;
    detail: string;
    
    constructor(date: moment.Moment | Date, title: string, detail?: string){
        this.date = moment(date);
        this.title = title;
        this.detail = detail;
    }
}

export class Day{
    date: moment.Moment;
    events: Event[];

    constructor(date: moment.Moment){
        this.date = moment(date);
    }
}