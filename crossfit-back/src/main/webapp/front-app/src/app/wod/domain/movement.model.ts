export class Movement{
    private id: number;
    private _fullname: string;

    constructor(){
    }

    public getId():number{
        return this.id;
    }
    get fullname() {
        return this._fullname;
    }
}