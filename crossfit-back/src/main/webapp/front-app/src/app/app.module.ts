import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule, MatSidenavModule } from '@angular/material';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateModule, TranslateLoader} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './shared/auth/auth.guard';
import { SharedModule } from './shared/shared.module';
import { WodModule } from './wod/wod.module';
import { ToolbarModule } from './toolbar/toolbar.module';

// AoT requires an exported function for factories
export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient, "./assets/i18n/", ".json");
}
const appRoutes: Routes = [
  { path: '', component: HomeComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
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
        useHash: true 
      } // <-- debugging purposes only
    ),
    BrowserModule,
    BrowserAnimationsModule,
    LayoutModule,
    MatSidenavModule,
    WodModule,
    ToolbarModule
  ],
  bootstrap: [AppComponent],
  providers: []
})
export class AppModule { }
