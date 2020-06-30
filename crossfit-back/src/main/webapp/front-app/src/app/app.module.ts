import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import {TranslateModule, TranslateLoader} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import { SharedModule } from './shared/shared.module';
import { WodModule } from './wod/wod.module';
import { ToolbarModule } from './toolbar/toolbar.module';
import { BookingModule } from './booking/booking.module';
import { AccountComponent } from './account/account.component';
import { AccountModule } from './account/account.module';
import { ErrorComponent } from './error/error.component';
import { ErrorModule } from './error/error.module';
import { HttpClientModule, HttpClient } from '@angular/common/http';

// AoT requires an exported function for factories
export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient, "./assets/i18n/", ".json");
}
const appRoutes: Routes = [
  { path: '**', redirectTo: '/activity' }
];

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    SharedModule,
    RouterModule.forRoot(
      appRoutes,
      { 
        enableTracing: false,
        useHash: true ,
        scrollPositionRestoration: 'enabled'
      } // <-- debugging purposes only
    ),
    BrowserModule,
    BrowserAnimationsModule,
    LayoutModule,
    ErrorModule,
    MatSidenavModule,
    WodModule,
    BookingModule,
    ToolbarModule,
    AccountModule
  ],
  bootstrap: [AppComponent],
  providers: [ { provide: LOCALE_ID, useValue: 'fr' } ],
})
export class AppModule { }
