import { Taggable } from "./taggable.model";

export class Movement implements Taggable{
    id: number;
    _fullname: string;

    constructor(){
    }

    public getId():number{
        return this.id;
    }
    get fullname() {
        return this._fullname;
    }
    set fullname(fullname:string) {
        this._fullname = fullname;
    }
}