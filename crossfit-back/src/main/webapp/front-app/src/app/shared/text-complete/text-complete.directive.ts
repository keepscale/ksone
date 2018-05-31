import {
  ComponentFactoryResolver,
  ComponentRef,
  Directive,
  ElementRef,
  EventEmitter,
  HostListener,
  Injector,
  Input,
  OnDestroy,
  Output,
  ViewContainerRef
} from '@angular/core';
import getCaretCoordinates from 'textarea-caret';
import { takeUntil } from 'rxjs/operators';
import { TextCompleteMenuComponent } from './text-complete-menu.component';
import { Subject } from 'rxjs';

export interface OptionSelectedEvent {
  option: any;
  insertedAt: {
    start: number;
    end: number;
  };
}

@Directive({
  selector:
    'textarea[textComplete],input[type="text"][textComplete]'
})
export class TextCompleteDirective implements OnDestroy {

  /**
   * The regular expression that will match the search text after the trigger character
   */
  @Input() searchRegexp = /^\w*$/;

  /**
   * The menu component to show with available options.
   * You can extend the built in `TextCompleteMenuComponent` component to use a custom template
   */
  @Input() menuComponent = TextCompleteMenuComponent;

  /**
   * Called when the options menu is shown
   */
  @Output() menuShown = new EventEmitter();

  /**
   * Called when the options menu is hidden
   */
  @Output() menuHidden = new EventEmitter();

  /**
   * Called when a choice is selected
   */
  @Output() optionSelected = new EventEmitter<OptionSelectedEvent>();

  /**
   * A function that accepts a search string and returns an array of options. Can also return a promise.
   */
  @Input() optionsFilter: ((value: string) => any[]) | null = null;

  /**
   * A function that formats the option in options list.
   */
  @Input() optionDisplay: (option: any) => string = option => option;

  /* tslint:disable member-ordering */
  private menu:
    | {
        component: ComponentRef<TextCompleteMenuComponent>;
        triggerCharacterPosition: number;
        lastCaretPosition?: number;
      }
    | undefined;

  private menuHidden$ = new Subject();

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver,
    private viewContainerRef: ViewContainerRef,
    private injector: Injector,
    private elm: ElementRef
  ) {}

  @HostListener('keypress', ['$event.key'])
  onKeypress(key: string) {
    if (key != 'Enter' && key.match(/^[a-zA-Z]+$/)){
      this.showMenu();
    }
  }

  @HostListener('document:keydown.Esc', ['$event'])
  onEsc(event: KeyboardEvent) {
    this.hideMenu();
  }
  @HostListener('document:keydown.Space', ['$event'])
  onSpace(event: KeyboardEvent) {
    this.hideMenu();
  }


  @HostListener('input', ['$event.target.value'])
  onChange(value: string) {
    if (this.menu) {
      const cursor = this.elm.nativeElement.selectionStart;
      if (cursor < this.menu.triggerCharacterPosition) {
        this.hideMenu();
      } else {
        const searchText = value.slice(
          this.menu.triggerCharacterPosition + 1,
          cursor
        );
        if (!searchText.match(this.searchRegexp)) {
          this.hideMenu();
        } else {
          this.menu.component.instance.searchText = searchText;
          this.menu.component.instance.options = [];
          this.menu.component.changeDetectorRef.detectChanges();
          Promise.resolve(this.optionsFilter(searchText))
            .then(options => {
              if (this.menu) {
                this.menu.component.instance.options = options;
                this.menu.component.changeDetectorRef.detectChanges();
              }
            })
            .catch(err => {
              if (this.menu) {
                this.menu.component.changeDetectorRef.detectChanges();
              }
            });
        }
      }
    }
  }

  @HostListener('blur')
  onBlur() {
    if (this.menu) {
      this.menu.lastCaretPosition = this.elm.nativeElement.selectionStart;
    }
  }

  private showMenu() {
    if (!this.menu) {
      const menuFactory = this.componentFactoryResolver.resolveComponentFactory<TextCompleteMenuComponent>(this.menuComponent);
      this.menu = {
        component: this.viewContainerRef.createComponent(
          menuFactory,
          0,
          this.injector
        ),
        triggerCharacterPosition: this.elm.nativeElement.selectionStart
      };
      const lineHeight = +getComputedStyle(
        this.elm.nativeElement
      ).lineHeight!.replace(/px$/, '');
      const { top, left } = getCaretCoordinates(
        this.elm.nativeElement,
        this.elm.nativeElement.selectionStart
      );
      this.menu.component.instance.position = {
        top: top + lineHeight,
        left
      };
      this.menu.component.changeDetectorRef.detectChanges();
      this.menu.component.instance.optionDisplay = this.optionDisplay;
      this.menu.component.instance.selectOption
        .pipe(takeUntil(this.menuHidden$))
        .subscribe(option => {
          const label = this.optionDisplay(option);
          const textarea: HTMLTextAreaElement = this.elm.nativeElement;
          const value: string = textarea.value;
          const startIndex = this.menu!.triggerCharacterPosition;
          const start = value.slice(0, startIndex);
          const caretPosition =
            this.menu!.lastCaretPosition || textarea.selectionStart;
          const end = value.slice(caretPosition);
          textarea.value = start + label + end;
          // force ng model / form control to update
          textarea.dispatchEvent(new Event('input'));
          this.hideMenu();
          const setCursorAt = (start + label).length;
          textarea.setSelectionRange(setCursorAt, setCursorAt);
          textarea.focus();
          this.optionSelected.emit({
            option,
            insertedAt: {
              start: startIndex,
              end: startIndex + label.length
            }
          });
        });
      this.menuShown.emit();
    }
  }

  private hideMenu() {
    if (this.menu) {
      this.menu.component.destroy();
      this.menuHidden$.next();
      this.menuHidden.emit();
      this.menu = undefined;
    }
  }

  ngOnDestroy() {
    this.hideMenu();
  }
}