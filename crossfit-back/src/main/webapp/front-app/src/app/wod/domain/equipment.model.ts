import { Taggable } from "./taggable.model";

export class Equipment implements Taggable{
    id: number;
    fullname: string;
    private type: string;
    private shortname: string;

    private link: string;
    private videoLink: string;
    
    constructor(){
    }


    /**
     * Getter $id
     * @return {number}
     */
	public get $id(): number {
		return this.id;
	}

    /**
     * Getter $fullname
     * @return {string}
     */
	public get $fullname(): string {
		return this.fullname;
	}

    /**
     * Getter $type
     * @return {string}
     */
	public get $type(): string {
		return this.type;
	}

    /**
     * Getter $shortname
     * @return {string}
     */
	public get $shortname(): string {
		return this.shortname;
	}

    /**
     * Getter $link
     * @return {string}
     */
	public get $link(): string {
		return this.link;
	}

    /**
     * Getter $videoLink
     * @return {string}
     */
	public get $videoLink(): string {
		return this.videoLink;
	}

    /**
     * Setter $id
     * @param {number} value
     */
	public set $id(value: number) {
		this.id = value;
	}

    /**
     * Setter $fullname
     * @param {string} value
     */
	public set $fullname(value: string) {
		this.fullname = value;
	}

    /**
     * Setter $type
     * @param {string} value
     */
	public set $type(value: string) {
		this.type = value;
	}

    /**
     * Setter $shortname
     * @param {string} value
     */
	public set $shortname(value: string) {
		this.shortname = value;
	}

    /**
     * Setter $link
     * @param {string} value
     */
	public set $link(value: string) {
		this.link = value;
	}

    /**
     * Setter $videoLink
     * @param {string} value
     */
	public set $videoLink(value: string) {
		this.videoLink = value;
	}
    
}