export class Wod{
    private id: number;
    private name: string;
    
	category:string;
    score:string;
    
	private description:string;
	private link:string;
    private videoLink:string;
    

    constructor(){
    }

    public getId():number{
        return this.id;
    }
}