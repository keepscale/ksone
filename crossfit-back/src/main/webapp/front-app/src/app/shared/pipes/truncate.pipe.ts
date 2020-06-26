import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate'
})
export class TruncatePipe implements PipeTransform {

  transform(value: String, args?: any): any {
    var limit = args.length > 0 ? parseInt(args[0], 10) : 20;
    const after = args.length > 1 ? args[1] : ' ';
    const trail = args.length > 2 ? args[2] : '...';
    limit =  limit + value.substring(limit).indexOf(after) + 1;
    return value.length > limit ? value.substring(0, limit) + trail : value;
  }

}
