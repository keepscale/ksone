import { Component, OnInit } from '@angular/core';
import { Principal } from '../shared/auth/principal.service';
import { User } from '../shared/domain/user.model';
import { ToolBarService } from '../toolbar/toolbar.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  account: User;

  constructor(private principal: Principal, private toolbar: ToolBarService) { }

  ngOnInit() {
    this.toolbar.setTitle("Mes informations")
    this.principal.identity(true).subscribe(res=>this.account=res);
  }

}
