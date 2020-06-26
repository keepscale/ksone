import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TextCompleteDirective } from './text-complete.directive';
import { TextCompleteMenuComponent } from './text-complete-menu.component';
import { MatSelectModule } from '@angular/material/select';

@NgModule({
  declarations: [
    TextCompleteDirective,
    TextCompleteMenuComponent
  ],
  imports: [CommonModule, MatSelectModule],
  exports: [
    TextCompleteDirective,
    TextCompleteMenuComponent
  ],
  entryComponents: [TextCompleteMenuComponent]
})
export class TextCompleteModule {}