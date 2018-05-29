import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'submit-button',
  templateUrl: './submit-button.component.html',
  styleUrls: ['./submit-button.component.css']
})
export class SubmitButtonComponent implements OnInit {

  @Input('value')
  private buttonValue = "Sauvegarder";
  
  @Input('i18n')
  private i18n = "";

  @Input('disabled')
  private disabled = false;

  constructor() { }

  ngOnInit() {
  }

}
