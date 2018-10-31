export class PaginateList<T>{
    
    public length: number;
    public pageSize: number = 20;
    public pageIndex: number = 0;

    public results: T[];

    constructor(){}

}