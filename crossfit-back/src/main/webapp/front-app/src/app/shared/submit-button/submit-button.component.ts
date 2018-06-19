import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'submit-button',
  templateUrl: './submit-button.component.html',
  styleUrls: ['./submit-button.component.css']
})
export class SubmitButtonComponent implements OnInit {

  @Input('value')
  buttonValue = "Sauvegarder";
  
  @Input('i18n')
  i18n = "";

  @Input('disabled')
  disabled = false;

  constructor() { }

  ngOnInit() {
  }

}
