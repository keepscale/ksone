import { Component, HostListener } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  template: `
    <ul 
      *ngIf="options?.length > 0"
      class="dropdown-menu"
      [style.top.px]="position?.top"
      [style.left.px]="position?.left">
      <li 
        (click)="selectOption.next(o)"
        *ngFor="let o of options"
        [class.active]="activeOption === o">
        <a
          href="javascript:;">
          {{ optionDisplay(o) }}
        </a>
      </li>
    </ul>
  `,
  styles: [
    `
    .dropdown-menu {
      list-style: none;
      position: absolute;
      display: block;
      max-height: 200px;
      overflow-y: auto;
      margin: 5px 0 0 0;
      background-color: #FFF;
      padding: 5px;
      z-index: 999;
      box-shadow: 0 5px 5px -3px rgba(0,0,0,.2),0 8px 10px 1px rgba(0,0,0,.14),0 3px 14px 2px rgba(0,0,0,.12);
    }
    .dropdown-menu li{
      padding: 5px;
    }
    .dropdown-menu li.active, .dropdown-menu li:hover{
      background-color: lightgray;
      cursor: pointer;
    }
    .dropdown-menu li a{
      text-decoration: none;
    }
  `
  ]
})
export class TextCompleteMenuComponent {
  position: { top: number; left: number };
  selectOption = new Subject();
  activeOption: any;
  searchText: string;
  private _options: any[];

  optionDisplay: (option: any) => string = option => option;

  set options(options: any[]) {
    this._options = options;
    if (options.indexOf(this.activeOption) === -1 && options.length > 0) {
      this.activeOption = options[0];
    }
  }

  get options() {
    return this._options;
  }

  @HostListener('document:keydown.ArrowDown', ['$event'])
  onArrowDown(event: KeyboardEvent) {
    event.preventDefault();
    const index = this.options.indexOf(this.activeOption);
    if (this.options[index + 1]) {
      this.activeOption = this.options[index + 1];
    }
  }

  @HostListener('document:keydown.ArrowUp', ['$event'])
  onArrowUp(event: KeyboardEvent) {
    event.preventDefault();
    const index = this.options.indexOf(this.activeOption);
    if (this.options[index - 1]) {
      this.activeOption = this.options[index - 1];
    }
  }

  @HostListener('document:keydown.Enter', ['$event'])
  onEnter(event: KeyboardEvent) {
    if (this.options.indexOf(this.activeOption) > -1) {
      event.preventDefault();
      this.selectOption.next(this.activeOption);
    }
  }
}